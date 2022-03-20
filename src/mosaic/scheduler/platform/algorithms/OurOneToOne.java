package mosaic.scheduler.platform.algorithms;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import mosaic.scheduler.platform.algorithms.util.AlgorithmUtil;
import mosaic.scheduler.platform.resources.Component;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.resources.Partition;
import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.test.Test;

import org.apache.log4j.Logger;



public class OurOneToOne implements IAlgorithm {
	private static Logger logger = Logger.getLogger(OurOneToOne.class.getPackage().getName());

	@Override
	public Vector<Node> executeOnce(Vector<Node> nodes,	int[] componentsToBeGenerated, int time) {
		Node n;
		Partition p;
		String s = "";
		for (int i=0; i<componentsToBeGenerated.length; i++) {
			s = s + componentsToBeGenerated[i] + " ";
		}
		OurOneToOne.logger.debug("componentsToBeGenerated : " + s);
		//add components & possibly nodes
		for (int i=0; i<componentsToBeGenerated.length; i++) {
			for (int j=0; j< componentsToBeGenerated[i]; j++) {
				n = nodes.get((int)(Math.random() * (nodes.size() - 1)));
				p = Partition.provide(n.getID());
				p.addComponent(new Component(false, i));
				n.addPartition(p);
			}
		}
		
		nodes = this.scale(nodes, time);
		
		//try to remove nodes
		Collections.sort(nodes, new NodeComparator());
		int k = 0;
		Vector<Partition> partitions = null;
		boolean found, found2;
		do {
			found = false;
			found2 = false;
			for (int i=0; i<componentsToBeGenerated.length; i++) {
				if (componentsToBeGenerated[i] < 0 && Math.abs(componentsToBeGenerated[i]) < AlgorithmUtil.getNumberComponents(nodes.get(k), i)) {
					found = true; 
				}
				if (componentsToBeGenerated[i] > 0)
					found2 = true;

			}
			if (!found && !found2) {
				nodes.get(k).status = Node.NODE_STATUS.TO_BE_REMOVED;
				for (int i=0; i<componentsToBeGenerated.length; i++) {
					componentsToBeGenerated[i] = componentsToBeGenerated[i] +  AlgorithmUtil.getNumberComponents(nodes.get(k), i);
				}
			}
			else {
				for (int i=0; i<componentsToBeGenerated.length; i++) {
					partitions = nodes.get(k).getAssignedPartitions();
					int j=0;
					do {
						Component c = partitions.get(j).getAssignedComponents().size() == 1 ? partitions.get(j).getAssignedComponents().get(0) : null;
						if (c != null && c.getType() == i && AlgorithmUtil.getNumberComponents(nodes.get(k), i) > 1 && Math.abs(componentsToBeGenerated[i]) > 0) {
							partitions.remove(j);
							componentsToBeGenerated[i]++;
						}
						else
							j++;
					} while (j < partitions.size());
				}
			}
			
			found = false;
			for (int i=0; i<componentsToBeGenerated.length; i++) {
				if (componentsToBeGenerated[i] < 0)
					found = true;
			}
			k++;
		} while (k<nodes.size() && found);
		
		return nodes;
	}

	@Override
	public Vector<Node> executeOnce(Vector<Node> nodes, int time) {
		//scale if necessary
		return this.scale(nodes, time);
	}

	private Vector<Node> scale(Vector<Node> nodes, int time) {		
		// apply GA for each element in population. move random number of partitions from overloaded node to other nodes.
		// after movement pick one in which all nodes are under-loaded & has all component types & cost is minimized
		// if such case is inexistent create node and move random one partition from every component type
		// keep the one which minimizes cost
		Hashtable<Integer,Vector<Node>> mutatedNodes = new Hashtable<Integer, Vector<Node>>();
		for (int i=0; i<OurManyToOne.POPULATION_SIZE; i++) {
			try {
				mutatedNodes.put(i, this.mutate(Test.cloneNodes(nodes), time));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// get the solution with the least cost and that has all nodes under-loaded
		int minIndex = 0;
		float minCost = Float.MAX_VALUE;
		boolean ok = true;
		int k=0;
		for (int i=0; i<OurManyToOne.POPULATION_SIZE; i++) {
			if (AlgorithmUtil.computePlatformCost(mutatedNodes.get(i)) < minCost) {
				ok = true;
				k = 0;
				for (Node n : mutatedNodes.get(i)) {
					if (n.computeLoad(time, k++) > SystemSettings.getSystemSettings().getMax_node_load_threshold()) {
						ok = false;
					}
				}
				if (ok == true) {
					minCost = AlgorithmUtil.computePlatformCost(mutatedNodes.get(i));
					minIndex = i;
				}
			}
		}

		return mutatedNodes.get(minIndex);
	}


	private Vector<Node> mutate(Vector<Node> nodes, int time) {
		Partition partition = null;
		int repeats = 0, repeats2 = 0;
		int maxRepeats = 0;
		Node n2 = null, newNode = null;
		Node n;
		int m;
		
		for (int k=0; k<nodes.size(); k++) {
			if (nodes.get(k).status == Node.NODE_STATUS.NEW)
				nodes.get(k).status = Node.NODE_STATUS.EXISTING;
		}
		//synchronized(nodes) {
			for (int k=0; k<nodes.size(); k++) {
				n = nodes.get(k);				 
				OurOneToOne.logger.debug("Load on node " + n.getID() + " : " + n.computeLoad(time, k));
				repeats = 0;
				maxRepeats = 30;
				while (n.computeLoad(time, k) > SystemSettings.getSystemSettings().getMax_node_load_threshold() && maxRepeats > repeats++) {
					OurOneToOne.logger.debug("Node " + n.getID() + " overloaded. Trying to reasign partitions");
					repeats2 = 0;
					//pick random node which is under-loaded
					do {		
						m = 0 + (int)(Math.random() * ((nodes.size() - 1 - 0) + 1));
						//OurOnetoOne.logger.debug("Attempting to move from node " + n.getID() + " to node " + nodes.get(m).getID());
						if (repeats2 < maxRepeats) {
							n2 = nodes.get(m);
							//pick random partition
							partition = n.getAssignedPartitions().get(0 + (int)(Math.random() * ((n.getAssignedPartitions().size() - 1 - 0) + 1)));
							String[] pTypes = AlgorithmUtil.computeNumberServiceTypes(n).split("#");
							if (Integer.parseInt(pTypes[partition.getAssignedComponents().get(0).getType()]) - 1 >= 1) {
								if (n2.getID().compareToIgnoreCase(n.getID()) != 0)
									if (n2.computeLoad(time, m) + partition.computeLoad(time) < SystemSettings.getSystemSettings().getMax_node_load_threshold()) {
										n.getAssignedPartitions().remove(partition);
										//reassign it
										n2.addPartition(partition);
										partition.assignToNode(n2.getID());
									}
							}
						}
					} while (maxRepeats > repeats2++);									
					
					// we were not able to decrease load so create new node
					if (repeats2 >= maxRepeats) {	
						if (nodes.size() < SystemSettings.getSystemSettings().getMax_number_nodes() + AlgorithmUtil.computeNoFailedNodes(nodes)) {
							newNode = AlgorithmUtil.addNewRandomNode();
							nodes.add(newNode);
							Vector<Integer> types = new Vector<Integer>();
							OurOneToOne.logger.debug("Could not releave load. Load :" + n.computeLoad(time, k) + " Creating new node " + newNode.getID());
							//decrease load of node by moving random partitions
							int reps = 0;
							while (n.computeLoad(time, k) > SystemSettings.getSystemSettings().getMax_node_load_threshold() && reps++ < 1000) {
								//pick random a partition from overloaded node
								partition = n.getAssignedPartitions().get(0 + (int)(Math.random() * ((n.getAssignedPartitions().size() - 1 - 0) + 1)));
								String[] pTypes = AlgorithmUtil.computeNumberServiceTypes(n).split("#");
								if (Integer.parseInt(pTypes[partition.getAssignedComponents().get(0).getType()]) - 1 >= 1) {						
									if (newNode.computeLoad(time, nodes.size()-1) + partition.computeLoad(time) < SystemSettings.getSystemSettings().getMax_node_load_threshold()
											&& !types.contains(partition.getAssignedComponents().get(0).getType())) {
										//reassign it
										n.getAssignedPartitions().remove(partition);
										newNode.addPartition(partition);
										partition.assignToNode(newNode.getID());
										types.add(partition.getAssignedComponents().get(0).getType());
										//OurOnetoOne.logger.debug("Moved partition of type " + partition.getAssignedComponents().get(0).getType() + " from node " + n.getID() + " to new node " + newNode.getID() + ". Loads : " + n.computeLoad(time, k) + "/" + newNode.computeLoad(time, k));
									}
								}
							}
							//reassign a partition of each type to new node						
							int count = 0;
							//for all component types
							OurOneToOne.logger.debug("Trying to achieve HA on new node");
							for (int kk=0;kk<SystemSettings.getSystemSettings().getNo_component_types();kk++) {
								//check if this type is not already present
								if (!types.contains(kk)) {
									reps = 0;
									do {											
										n2 = nodes.get(0 + (int)(Math.random() * ((nodes.size() - 1))));
										//OurOnetoOne.logger.debug("Selected node " + n2.getID() + " for moving partition from it to new node " + newNode.getID());
										count = 0;
										if (n2.getID() != newNode.getID()) {															
											for (Partition pp : n2.getAssignedPartitions()) {
												if (pp.getAssignedComponents().get(0).getType() == kk)
													count++;
											}
											if (count > 1)
												for (Partition pp : n2.getAssignedPartitions())
													if (pp.getAssignedComponents().get(0).getType() == kk)
														partition = pp;											
											//OurOnetoOne.logger.debug("Count on partition of type " + kk + " : " + count);
											//if succeeded in moving partition
											if (count > 1 && newNode.computeLoad(time, nodes.size()-1) + partition.computeLoad(time) < SystemSettings.getSystemSettings().getMax_node_load_threshold()) {
												partition = n2.removePartition(partition);
												newNode.addPartition(partition);
												partition.assignToNode(newNode.getID());
												types.add(partition.getAssignedComponents().get(0).getType());
												//OurOnetoOne.logger.debug("Moved partition of type " + partition.getAssignedComponents().get(0).getType() + " from node " + n2.getID() + " to new node " + newNode.getID() + ". Loads : " + n.computeLoad(time, k) + "/" + newNode.computeLoad(time, k));										
											}
										}
									} while (count <= 1 && reps++ < 1000);
								}
							}
						}
					}
				}
				OurOneToOne.logger.debug("Load after attempt :" + n.computeLoad(time, k));
			}
			//}		
		OurOneToOne.logger.debug("Component types after scheduling :");
		for (int ii=0; ii<nodes.size(); ii++) {
			Node nn = nodes.get(ii);
			OurOneToOne.logger.debug(nn.getID() + " : " + AlgorithmUtil.computeNumberServiceTypes(nn) + " " + nn.computeLoad(0, ii));
		}
		
		return nodes;
	}	
	
	class NodeComparator implements java.util.Comparator<Node> {
	    public int compare(Node node1, Node node2) {
	        return AlgorithmUtil.gatherComponents(node2).size() - AlgorithmUtil.gatherComponents(node1).size();
	    }
	}
}
