package mosaic.scheduler.simulator.algorithms;

import java.util.Vector;

import mosaic.scheduler.platform.algorithms.IAlgorithm;
import mosaic.scheduler.platform.algorithms.util.AlgorithmUtil;
import mosaic.scheduler.platform.resources.Component;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.resources.Partition;
import mosaic.scheduler.platform.settings.SystemSettings;


/**
 * Simple implementation of the Round Robin strategy in case a fixed number (ring) of partitions are used
 * @author Marc Frincu
 *
 */
public final class RoundRobin implements IAlgorithm {
	public int[] iteratorComponents = new int[SystemSettings.getSystemSettings().getNo_component_types()];

	/**
	 * Round Robin algorithm with node creation if load is exceeded
	 * @param nodes
	 * @param componentsNoToBeGenerated
	 */
	public RoundRobin() {		
		for (int j=0; j<this.iteratorComponents.length; j++) {			
				this.iteratorComponents[j] = SystemSettings.getSystemSettings().getMax_number_nodes() * j;
		}		
	}

	@Override
	public Vector<Node> executeOnce(Vector<Node> nodes, int[] componentsToBeGenerated, int time) {
		int partitionIndex = 0;
		Component component = null;

		for (int k=0; k<componentsToBeGenerated.length; k++) {
			for (int i=0; i<componentsToBeGenerated[k]; i++) {
				for (int j=0; j<this.iteratorComponents.length; j++) {
					if (this.iteratorComponents[j] >= SystemSettings.getSystemSettings().getMax_number_nodes() * j + SystemSettings.getSystemSettings().getMax_number_nodes() - 1)
						this.iteratorComponents[j] = SystemSettings.getSystemSettings().getMax_number_nodes() * j;
				}	
	
				//generate random component
				component = new Component(false, k);
				//link to other components as indicated by the relationship matrix
				for (int j=0; j<SystemSettings.getSystemSettings().getNo_component_types(); j++) {
					if (SystemSettings.getSystemSettings().getComponent_connection_table()[k][j] == 1) {
						component.addConnection(AlgorithmUtil.getRandomComponent(j, nodes));
					}
				}
				
				partitionIndex = this.iteratorComponents[k];
				
				// add component to current partition
				AlgorithmUtil.gatherPartitions(nodes).get(partitionIndex).addComponent(component);
				this.iteratorComponents[k]++;
				
				this.selectNodeAndScale(nodes, AlgorithmUtil.gatherPartitions(nodes), partitionIndex, time);		
			}
		}
		
		//simulate failure and recreate failed components
		/*if (time == 20) {
			failedComponents = this.simulateFailure(nodes, 2);
			// nodes have failed. recreate partition list based on existing 
			if (failedComponents != null) {
				for (Component c : failedComponents) {
					
					for (int j=0; j<this.iteratorComponents.length; j++) {
						if (this.iteratorComponents[j] >=Test.MAX_NUMBER_NODES * j + Test.MAX_NUMBER_NODES - 1)
							this.iteratorComponents[j] = Test.MAX_NUMBER_NODES * j;
					}	
					
					final int x = c.getType();
					
					partitionIndex = this.iteratorComponents[x];
					
					// add component to current partition
					this.gatherPartitions(nodes).get(partitionIndex).addComponent(component);
					this.iteratorComponents[x]++;
					
					this.selectNodeAndScale(nodes, this.gatherPartitions(nodes), partitionIndex, time);
			
				}
			}
		}*/
		
		// for statistics get node history
		String format = "";
		int k=0;
		for (Node n: nodes) {
			for (int i=0;i<SystemSettings.getSystemSettings().getNo_component_types(); i++)
				format += AlgorithmUtil.getNumberComponents(n, i) + "#";
			n.history.put(time, n.new NodeHistory(n.computeLoad(time,k++), AlgorithmUtil.getNoComponentTypes(n), AlgorithmUtil.computeNodeCost(n, nodes), format, n.startedSearch, n.isRelayNode, n.isStopped));
		}
		
		return null;
	}
	
	public Vector<Node> selectNodeAndScale(Vector<Node> nodes, Vector<Partition> partitions, int partitionIndex, int time){
		Node newNode = null;
		//String oldL = "", newL = "";
		int load;
		
		if (AlgorithmUtil.computeNumberOverloadedNodes(nodes, time) > 0) {
			if (nodes.size() < SystemSettings.getSystemSettings().getMax_number_nodes() + AlgorithmUtil.computeNoFailedNodes(nodes)) {
				newNode = AlgorithmUtil.addNewRandomNode();
				nodes.add(newNode);
			}
			else {
				load = Integer.MAX_VALUE;
				int k=0;
				for (Node no :  nodes) {
					if (!no.isStopped && no.computeLoad(time,k) < load) {
						load = no.computeLoad(time,k);
						newNode = no;
					}
					k++;
				}
			}
		
		/*while (this.computeNumberOverloadedNodes(nodes, time) > 0) {			
			newL = "";
			//if we have to previous load configurations identical it means we reached stability => not good.. try to find new node which can further optimize load
			for (Node no :  nodes) {
				newL += no.computeLoad(time);
			}

			if (newL.compareTo(oldL) == 0) {
				load = Integer.MAX_VALUE;
				for (Node no :  nodes) {
					if (!no.hasFailed() && no.computeLoad(time) < load) {
						load = no.computeLoad(time);
						newNode = no;
					}
				}
			}
			System.out.println(oldL + " " + newL);
			oldL = newL;
*/							

			for (Node n : nodes) {
				if (n.getID().compareToIgnoreCase(newNode.getID()) != 0 && n.getAssignedPartitions().size() > 0) {
					Partition p = n.removePartition(n.getAssignedPartitions().get(n.modifyNodePartitionIterator()));
					newNode.addPartition(p);
					p.assignToNode(newNode.getID());
				}
			}
		}
		
		return nodes;
	}
			
	/**
	 * Reassigns a partition from the list of nodes to the newly created node
	 * @param nodes
	 * @param newNode
	 */
	@SuppressWarnings("unused")
	private void reassignPartition(Vector<Node> nodes, Node newNode, int time) {
		Node maxNode = nodes.get(0);
		for (Node node: nodes) {
			if (node.getAssignedPartitions().size() > maxNode.getAssignedPartitions().size() && !(node.isStopped)) {
				maxNode = node;
			}
		}
		// pick random partition
		Partition partition = AlgorithmUtil.getLargestPartition(maxNode.getAssignedPartitions(), maxNode, time);//maxNode.getAssignedPartitions().get(0 + (int)(Math.random() * ((maxNode.getAssignedPartitions().size()-1 - 0) + 1)));
		//reassign
		Partition p = maxNode.removePartition(partition);
		newNode.addPartition(p);		
		p.assignToNode(newNode.getID());
	}

	@Override
	public Vector<Node> executeOnce(Vector<Node> nodes, int time) {
		throw new UnsupportedOperationException();
	}

}
