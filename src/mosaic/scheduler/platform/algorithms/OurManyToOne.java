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

/**
 * This class implements the high availability scheduling algorithm in which a partition can hold multiple components
 * @author Marc Frincu
 *
 */
public final class OurManyToOne implements IAlgorithm {
	private static Logger logger = Logger.getLogger(OurManyToOne.class.getPackage().getName());

	//public static int MAX_ITERATION = 600;
	public static int POPULATION_SIZE = 1;	

	@Override
	public Vector<Node> executeOnce(Vector<Node> nodes, int time) {
		return this.executeOnce(nodes, new int[]{}, time);
	}

	
	@Override
	public Vector<Node> executeOnce(Vector<Node> nodes, int[] componentsToBeGenerated, int time) {
		Component component = null;
			
		for (int i=0; i<componentsToBeGenerated.length; i++) {
			for (int k=0; k<componentsToBeGenerated[i]; k++) {
				component = new Component(false, i);
				//link to other components as indicated by the relationship matrix
				for (int j=0; j<SystemSettings.getSystemSettings().getNo_component_types(); j++) {
					if (SystemSettings.getSystemSettings().getComponent_connection_table()[i][j] == 1) {
						component.addConnection(AlgorithmUtil.getRandomComponent(j, nodes));
					}
				}
				//add component
				AlgorithmUtil.gatherPartitions(nodes).get(this.getPartitionIndex(nodes, component, time)).addComponent(component);
			}
			
		}
		//scale if necessary
		nodes = this.scale(nodes, time);
		
		//try to remove nodes
		if (nodes.size() > 1) {
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
		}
		
		//store some trace data
		String format = "";
		int j=0;
		for (Node n: nodes) {
			format += "|";
			for (int i=0;i<SystemSettings.getSystemSettings().getNo_component_types(); i++)
				format += AlgorithmUtil.getNumberComponents(n, i) + "#";
			// this is where we handle the historical data
			n.history.put(time, n.new NodeHistory(n.computeLoad(time, j++), AlgorithmUtil.getNoComponentTypes(n), AlgorithmUtil.computeNodeCost(n, nodes), format, n.startedSearch, n.isRelayNode, n.isStopped));
		}
		
		return nodes;
	}
	
	/**
	 * Returns a partition index with the smallest number of components situated on the node with the smallest cost. 
	 * This is the partition where the component should be placed
	 * @param nodes the list of nodes
	 * @param component the component needing a partition
	 * @param time the time at which this takes place
	 * @return the partition index
	 */
	private int getPartitionIndex(Vector<Node> nodes, Component component, int time) {
		Vector<Integer> v = new Vector<Integer>();		
		Vector<Partition> partitions = AlgorithmUtil.gatherPartitions(nodes);

		// select node with least cost
		String nName = "";
		float minCost = Float.MAX_VALUE;
		float cost;
		int k = 0;
		for (Node n : nodes) {
			cost = AlgorithmUtil.computeCostForComponent(nodes, component, n);
			if (cost < minCost && !n.isStopped) {
				minCost = cost + n.computeLoad(time, k++);
				nName = n.getID();
			}
		}

		synchronized (partitions) {
			int pSize = 0;
			for (int i = SystemSettings.getSystemSettings().getMax_number_nodes() * component.getType(); i<(SystemSettings.getSystemSettings().getMax_number_nodes() * component.getType() + SystemSettings.getSystemSettings().getMax_number_nodes() - 1); i++ ) {
				pSize = partitions.get(i).getAssignedComponents().size();
				for (int j=0; j<pSize; j++) {
					v.add(i);
				}
				v.add(i);//1 occurrence for size=0 and +1 for the rest			
			}
		}

		//pick random partition from the partitions assigned to the node that provided the least cost
		//if node does not oversee any partition attached to the component type , pick random 
		int pIndex = 0;
		//int repeats = 0;
		/*Vector<Partition> parts = this.gatherPartitions(nodes);
		do {
			pIndex = v.get(0 + (int)(Math.random() * ((v.size() - 1 - 0) + 1)));
		} while (parts.get(pIndex).getAssignedNodeID().compareToIgnoreCase(nName) != 0 && repeats++ < 100);*/
		
		//pick partition with smallest number of C/Cs
		int smallest = Integer.MAX_VALUE;
		Partition part = null;
		for (Node n: nodes) {
			if (n.getID().compareToIgnoreCase(nName) == 0) {
				for (Partition p : n.getAssignedPartitions()) {
					if (p.getAssignedComponents().size() < smallest) {
						smallest = p.getAssignedComponents().size();
						part = p; 
					}
				}
			}
		}
		
		for (int i=0; i<partitions.size(); i++) {
			if (part != null && partitions.get(i).getID().compareToIgnoreCase(part.getID()) == 0) {
				pIndex = i;
			}
		}
		
		
		return pIndex;
	}
	
	/**
	 * This method does the scheduling (scaling here is seen just a a requirement to add new nodes if necessary). The real scaling takes place inside the scaler.
	 * Scheduling is done through a genetic algorithm
	 * @param nodes the list of nodes. The list is a new object and not a reference to the input one
	 * @param time the time at which this action takes place
	 * @return
	 */
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
	
	/**
	 * Here takes place the actual scheduling. This method performs one transformation inside the schedule
	 * @param nodes the list of nodes
	 * @param time the time at which this action takes place
	 * @return the new configuration stored in a vector of Node
	 */
	private Vector<Node> mutate(Vector<Node> nodes, int time) {
		Partition partition = null;
		int repeats = 0, repeats2 = 0;
		int maxRepeats = 0, index = 0;
		Node n2 = null, newNode = null;
		Vector<Partition> parts = null;
		Node n;
		int m;
		
		for (int k=0; k<nodes.size(); k++) {
			if (nodes.get(k).status == Node.NODE_STATUS.NEW)
				nodes.get(k).status = Node.NODE_STATUS.EXISTING;
		}
		
		for (int k=0; k<nodes.size(); k++) {
			n = nodes.get(k);
			repeats = 0;
			maxRepeats = 30;
			OurManyToOne.logger.debug("Load on node " + n.getID() + " : " + n.computeLoad(time, k));
			while (n.computeLoad(time, k) > SystemSettings.getSystemSettings().getMax_node_load_threshold() && maxRepeats > repeats++) {
				repeats2 = 0;
				OurManyToOne.logger.debug("Node " + n.getID() + " overloaded. Trying to reasign partitions");
				//pick random node which is under-loaded
				do {		
					 m = 0 + (int)(Math.random() * ((nodes.size() - 1 - 0) + 1));
			
					if (repeats2 < maxRepeats) {
						n2 = nodes.get(m);
						//pick random partition
						partition = n.getAssignedPartitions().get(0 + (int)(Math.random() * ((n.getAssignedPartitions().size() - 1 - 0) + 1)));
						if (n2.getID().compareToIgnoreCase(n.getID()) != 0)
						if (n2.computeLoad(time, m) + partition.computeLoad(time) < SystemSettings.getSystemSettings().getMax_node_load_threshold()) {
							n.getAssignedPartitions().remove(partition);
							//reassign it
							n2.addPartition(partition);
							partition.assignToNode(n2.getID());
						}
					}
				} while (maxRepeats > repeats2++);
				
				// we were not able to decrease load so create new node
				if (repeats2 >= maxRepeats) {
					if (nodes.size() < SystemSettings.getSystemSettings().getMax_number_nodes() + AlgorithmUtil.computeNoFailedNodes(nodes)) {
						newNode = AlgorithmUtil.addNewRandomNode();
						nodes.add(newNode);
						OurManyToOne.logger.debug("Could not releave load. Load :" + n.computeLoad(time, k) + " Creating new node " + newNode.getID());

						//decrease load of node by moving random partitions
						while (n.computeLoad(time, k) > SystemSettings.getSystemSettings().getMax_node_load_threshold()) {
							//pick random a partition from overloaded node
							partition = n.getAssignedPartitions().remove(0 + (int)(Math.random() * ((n.getAssignedPartitions().size() - 1 - 0) + 1)));
							//reassign it
							newNode.addPartition(partition);
							partition.assignToNode(newNode.getID());
						}						
						//reassign a partition of each type to new node
						OurManyToOne.logger.debug("Trying to achieve HA on new node");
						parts = AlgorithmUtil.gatherPartitions(nodes);
						Vector<Partition> aParts;
						Node tmpNode;
						for (int j=0; j<SystemSettings.getSystemSettings().getNo_component_types(); j++) {
							int reps = 0;
							do {
								index = SystemSettings.getSystemSettings().getMax_number_nodes() * j + (int)(Math.random() * ((SystemSettings.getSystemSettings().getMax_number_nodes() * j + SystemSettings.getSystemSettings().getMax_number_nodes() - 1 - SystemSettings.getSystemSettings().getMax_number_nodes() * j) + 1));
							} while (parts.get(index).getAssignedComponents().size() == 0 && reps++ < maxRepeats);
						
							partition = parts.get(index);
							tmpNode = null;
							for (Node node : nodes) {								
								if (node.getID().compareToIgnoreCase(parts.get(index).getAssignedNodeID()) == 0) {
									aParts = node.getAssignedPartitions();
									for (int i=0; i<aParts.size(); i++) {
										if (aParts.get(i).getID().compareToIgnoreCase(partition.getID()) == 0)
											partition = aParts.get(i);
											tmpNode = node;
											//found = true;
											//break;
										
									}
								}
								//if (found)
								//	break;
							}
							if (newNode.computeLoad(time, nodes.size()-1) + partition.computeLoad(time) < SystemSettings.getSystemSettings().getMax_node_load_threshold()) {
								tmpNode.removePartition(partition);
								//reassign it
								newNode.addPartition(partition);								
								partition.assignToNode(newNode.getID());
							}
						}
					}
				}
			}
			OurManyToOne.logger.debug("Load after attempt :" + n.computeLoad(time, k));
		}
		
		OurManyToOne.logger.debug("Component types after scheduling :");
		for (int ii=0; ii<nodes.size(); ii++) {
			Node nn = nodes.get(ii);
			OurManyToOne.logger.debug(nn.getID() + " : " + AlgorithmUtil.computeNumberServiceTypes(nn) + " " + nn.computeLoad(0, ii));
		}
		return nodes;
	}
	
	class NodeComparator implements java.util.Comparator<Node> {
	    public int compare(Node node1, Node node2) {
	        return AlgorithmUtil.gatherComponents(node2).size() - AlgorithmUtil.gatherComponents(node1).size();
	    }
	}
}