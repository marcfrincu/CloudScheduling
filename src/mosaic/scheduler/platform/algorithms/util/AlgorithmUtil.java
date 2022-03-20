package mosaic.scheduler.platform.algorithms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import mosaic.scheduler.platform.resources.Component;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.resources.Partition;
import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.test.TestRunner;

import org.apache.log4j.Logger;


public class AlgorithmUtil {

	public static Logger logger = Logger.getLogger(TestRunner.class.getPackage().getName());

	/**
	 * Use the Pooled class instead for speed
	 * @param list
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static Vector<Node> deepCopyNodes(
			Vector<Node> list) throws Exception {

		// serialize Vector into byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(list);
		byte buf[] = baos.toByteArray();
		oos.close();

		// deserialize byte array into Vector
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Vector<Node> newlist = (Vector<Node>) ois.readObject();
		ois.close();

		return newlist;
	}
	
	/**
	 * Computes the cost for a given component
	 * @param nodes
	 * @param component
	 * @param assignedNode the node the component is assigned to
	 * @return
	 */
	public static float computeCostForComponent (Vector<Node> nodes, Component component, Node assignedNode) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return Float.NEGATIVE_INFINITY;
		}
		if (component == null) {
			AlgorithmUtil.logger.debug("component null");
			return Float.NEGATIVE_INFINITY;
		}
		
		Vector<Component> components = null;
		Vector<String> dependencies = component.getConnections();
		if (assignedNode == null)
			assignedNode = AlgorithmUtil.findNode(nodes, component);
		
		float c1 = 1 /*same DC*/, c2 = 2 /*same C*/, c3 = 4 /*different C*/;
		float cost = 0;
		
		for (String d : dependencies) {
			if (d == null)
				continue;
			
			for (Node node : nodes) {
				components = AlgorithmUtil.gatherComponents(node);
				if (components == null)
					continue;
				
				for (Component c : components) {
					if (d.compareToIgnoreCase(c.getID()) == 0) {
						if (assignedNode.getCloudID().compareToIgnoreCase(node.getCloudID()) != 0) {
							cost += c3;
						}
						else {
							if (assignedNode.getDataCenterID().compareToIgnoreCase(node.getDataCenterID()) == 0) {
								cost += c1;
							}
							else {
								cost += c2;
							}
						}
					}
				}
			}
		}
		return cost;
	}

	/**
	 * Retrieves the vector of existing partitions.
	 * The returned list is a reference and not a DEEP COPY 
	 * @param nodes
	 * @return
	 */
	public static Vector<Partition> gatherPartitions(Vector<Node> nodes) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		Vector<Partition> partitions = new Vector<Partition>();		
		for (Node node : nodes) {
			partitions.addAll(node.getAssignedPartitions());
		}
		
		return partitions;
	}

	/**
	 * Retrieves the list of components assigned to the given node
	 * The returned list is a reference and not a DEEP COPY
	 * @param node
	 * @return
	 */
	public static Vector<Component> gatherComponents(Node node) {
		if (node == null) {
			AlgorithmUtil.logger.debug("node null");
			return null;
		}
		Vector<Component> components = new Vector<Component>();
		Vector<Partition> partitions = node.getAssignedPartitions();
		for (Partition partition : partitions) {
			components.addAll(partition.getAssignedComponents());
		}
		return components;
	}

	/**
	 * Returns the number of components of a specified type found on a given node
	 * @param node
	 * @param type
	 * @return
	 */
	public static int getNumberComponents(Node node, int type) {
		if (node == null) {
			AlgorithmUtil.logger.debug("node null");
			return (int)Float.NEGATIVE_INFINITY;
		}
		if (type >= SystemSettings.getSystemSettings().getNo_component_types()) {
			AlgorithmUtil.logger.debug("type greater than largest allowed type value");
			return (int)Float.NEGATIVE_INFINITY;		
		}
		Vector<Component> c = gatherComponents(node);
		int count =0;
		for (Component com : c) {
			if (com.getType() == type) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Retrieves the list of existing components. 
	 * The list contains references and not DEEP COPIES 
	 * @param nodes
	 * @return
	 */
	public static Vector<Component> gatherComponents(Vector<Node> nodes) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		Vector<Partition> partitions = gatherPartitions(nodes);
		Vector<Component> components = new Vector<Component>();
		
		for (Partition partition : partitions) {
			components.addAll(partition.getAssignedComponents());
		}
		
		return components;
	}

	/**
	 * Retrieves the list of existing components that have a specified type.
	 * The list contains references and not DEEP COPIES
	 * @param nodes
	 * @param type
	 * @return
	 */
	public static Vector<Component> gatherComponents(Vector<Node> nodes, int type) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		if (type >= SystemSettings.getSystemSettings().getNo_component_types()) {
			AlgorithmUtil.logger.debug("type greater than largest allowed type value");
			return null;
		}
		Vector<Component> c = gatherComponents(nodes);
		Vector<Component> list = new Vector<Component>();
		for (Component com : c) {
			if (com.getType() == type) {
				list.add(com);
			}
		}
		return list;
	}
	
	public static String computeNumberServiceTypes(Node node) {
		String format = "";
		for (int i=0;i<SystemSettings.getSystemSettings().getNo_component_types(); i++)
			format += AlgorithmUtil.getNumberComponents(node, i) + "#";
		
		return format;
	}

	/**
	 * Computes the cost of the platform
	 * @param nodes
	 * @return
	 */
	public static float computePlatformCost(Vector<Node> nodes) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return Float.NEGATIVE_INFINITY;
		}
		Vector<Component> components = gatherComponents(nodes);
		//TODO: when computing costs ignore already considered nodes. Eg. if A linked to B and A has been considered, ignore it when processing B's cost 
		//	Hashtable<String, Integer> alreadySeen = new Hashtable<String, Integer>();	 
		float cost = 0;
		
		for (Component component : components) {
			cost += AlgorithmUtil.computeCostForComponent(nodes, component, null);
		}
		return cost;
	}

	/**
	 * Creates a random node attached to an arbitrary cloud and data center
	 * @return the newly created node
	 */
	public static Node addNewRandomNode() {
		int dci = 0 + (int)(Math.random() * SystemSettings.getSystemSettings().getNumber_clouds());
		int ci = 0 + (int)(Math.random() * SystemSettings.getSystemSettings().getNumber_datacenters_per_cloud());
		
		return new Node("DC" + dci, "C" + ci);
	}
	
	/**
	 * Retrieves the partition with the biggest load
	 * @param partitions
	 * @return
	 */
	public static Partition getLargestPartition(Vector<Partition> partitions, Node node, int time){
		if (node == null) {
			AlgorithmUtil.logger.debug("node null");
			return null;
		}
		if (partitions == null) {
			AlgorithmUtil.logger.debug("partition null");
			return null;
		}
		
		Partition maxPartition = partitions.get(0);
		float cost = node.computeLoad(time, maxPartition), cost2;
		for (Partition partition : partitions) {
			cost2 = node.computeLoad(time, partition);
			if (cost2 > cost) {
				cost = cost2;
				maxPartition = partition;
			}
		}
		return maxPartition;
	}
	
	/**
	 * Retrieves the node with the given ID.
	 * The node is a reference and not a DEEP COPY
	 * @param nodes
	 * @param ID
	 * @return
	 */
	protected static Node getNodeFromList(Vector<Node> nodes, String ID) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		for (Node node: nodes) {
			if (node.getID().compareToIgnoreCase(ID) == 0) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the component types that are assigned on the given node
	 * @param node
	 * @return a string containing the component types in integer format: "0123" -> 4 types 0, 1, 2, 3
	 */
	protected static String getComponentTypes(Node node) {
		int[] types = new int[SystemSettings.getSystemSettings().getNo_component_types()];
		for (Partition p : node.getAssignedPartitions()) 
			for (Component c : p.getAssignedComponents())
				types[c.getType()] = c.getType();
		
		StringBuilder s = new StringBuilder();
		for (int i=0; i<types.length; i++) {
			if (types[i] != 0) 
				s.append(types[i]);  
		}
		return s.toString();
	}
	
	/**
	 * Retrieves the number of component types existing on a given node
	 * @param node
	 * @return
	 */
	public static int getNoComponentTypes(Node node) {
		int[] types = new int[SystemSettings.getSystemSettings().getNo_component_types()];
		for (Partition p : node.getAssignedPartitions()) 
			for (Component c : p.getAssignedComponents())
				types[c.getType()] = c.getType()+1;
		
		int count = 0;
		for (int i=0; i<types.length; i++) {
			if (types[i] != 0)
				++count;
		}
		return count;
	}	
	
	/**
	 * Retrieves a random component of a certain type given the list of nodes
	 * @param type
	 * @param nodes
	 * @return
	 */
	public static String getRandomComponent(int type, Vector<Node> nodes) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		if (type >= SystemSettings.getSystemSettings().getNo_component_types()) {
			AlgorithmUtil.logger.debug("type greater than largest allowed type value");
			return null;		
		}
		Vector<Component> components = AlgorithmUtil.gatherComponents(nodes);
		int i = 0;
		while (i < components.size()) {
			if (components.get(i).getType() != type)
				components.remove(i);
			else
				i++;
		}
		if (components.size() > 0) 
			return components.get(0 + (int)(Math.random() * ((components.size() -1 - 0) + 1))).getID();
		return null;
	}
		
	/**
	 * Computes the cost of a node 
	 * @param node
	 * @param nodes
	 * @return
	 */
	public static float computeNodeCost(Node node, Vector<Node> nodes) {
		if (node == null) {
			AlgorithmUtil.logger.debug("node null");
			return (int)Float.NEGATIVE_INFINITY;
		}
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return (int)Float.NEGATIVE_INFINITY;
		}
		Vector<Component> components = AlgorithmUtil.gatherComponents(node);		
		float cost = 0;
		
		for (Component component : components) {
			cost += AlgorithmUtil.computeCostForComponent(nodes, component, null);	
		}
		return cost;
	}
		
	/**
	 * Simulates a failure in one or more nodes
	 * @param nodes
	 * @return the list of components from the failed nodes
	 */
	protected static Vector<Component> simulateFailure(Vector<Node> nodes, int noNodesToFail) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		if (noNodesToFail <= 0) {
			AlgorithmUtil.logger.debug("noNodesToFail not a positive value");
			return null;		
		}		Vector<Component> components = new Vector<Component>();
		
		int j = 0, selected = 0;
		if (nodes.size() == 1)
			return null;

		for (int i=0; i< noNodesToFail; i++) {
				do {
					selected = 0 + (int)(Math.random() * ((nodes.size() -1  - 0) + 1));
				} while (nodes.get(selected).isStopped);
				
				nodes.get(selected).isStopped = true;
				components.addAll(new Vector<Component>(AlgorithmUtil.gatherComponents(nodes.get(selected))));

				// Reassign in a round robin manner node partitions to the rest of the nodes
				j=0;				
				while (nodes.get(selected).getAssignedPartitions().size() > 0){
					if (j == selected || nodes.get(j).isStopped)
						j++;
					
					if (j >= nodes.size())
						j=0;
					
					Partition p = nodes.get(selected).getAssignedPartitions().remove(0);
					nodes.get(j).addPartition(p);
					p.assignToNode(nodes.get(j).getID());
					p.removeAssignedComponents();
					
					j++;
					if (j >= nodes.size())
						j=0;
				}
		}
		return components;
	}
	
	/**
	 * Returns the number of overloaded nodes
	 * @param nodes
	 * @param time
	 * @return
	 */
	public static int computeNumberOverloadedNodes(Vector<Node> nodes, int time) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return (int)Float.NEGATIVE_INFINITY;
		}
		int count = 0;
		int k=0;
		for (Node node: nodes) {
			if (node.computeLoad(time,k++) > SystemSettings.getSystemSettings().getMax_node_load_threshold())
				count++;
		}
		return count;
	}

	/**
	 * Computes the costs of relocating a partition on every available nodes. The partition must always have a positive no of components
	 * @param partition
	 * @param nodes
	 * @return a vector sorted ascending after relocation costs
	 */
	@SuppressWarnings("unchecked")
	protected static Vector<CostData> computePartitionRelocationCost(Partition partition, Vector<Node> nodes) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		if (partition == null) {
			AlgorithmUtil.logger.debug("partition null");
			return null;
		}
		if (partition.getAssignedComponents().size() == 0)
			return null;
		
		float cost = 0f;
		Vector<CostData> costs = new Vector<CostData>();
		for (Node n : nodes) {
			cost = 0f;
			for (Component c : partition.getAssignedComponents()) {	
				if (n.getID().compareToIgnoreCase(partition.getAssignedNodeID()) != 0 && !n.isStopped) {
					cost += AlgorithmUtil.computeCostForComponent(nodes, c, n);
				}
			}
			costs.add(new AlgorithmUtil(). new CostData(partition, n, cost, partition.getAssignedComponents().get(0).getType()));
		}	
		Collections.sort(costs, new AlgorithmUtil(). new ItemComparatorAsc());		
		return costs;
	}

	/**
	 * Returns the number of failed nodes
	 * @param nodes
	 * @return
	 */
	public static int computeNoFailedNodes(Vector<Node> nodes) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return (int)Float.NEGATIVE_INFINITY;
		}		int no = 0;
		for (Node node : nodes) {
			if (node.isStopped)
				no++;
		}
		return no;
	}

	/**
	 * Returns the node the given component belongs to
	 * @param nodes
	 * @param component
	 * @return
	 */
	private static Node findNode(Vector<Node> nodes, Component component) {
		if (nodes == null) {
			AlgorithmUtil.logger.debug("nodes null");
			return null;
		}
		if (component == null) {
			AlgorithmUtil.logger.debug("component null");
			return null;
		}		
		Vector<Partition> partitions;
		Vector<Component> components;
		for (Node node : nodes) {
			partitions = node.getAssignedPartitions();
			for (Partition partition : partitions) {
				components = partition.getAssignedComponents();
				for (Component c : components) {					 
					if (component.getID().compareToIgnoreCase(c.getID()) == 0) {
						return node;
					}
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected class ItemComparatorAsc implements Comparator {
		public final int compare(Object a, Object b) {
			if (((CostData) a).getCost() >= ((CostData) b).getCost())
				return -1;
			return 1;
		}
	}
	
	public final class CostData {
		public float cost;
		public Partition partition;
		public Node node;
		public int partitionType;
		
		public CostData(Partition partition, Node node, float cost, int partitionType) {
			this.partition = partition;
			this.node = node;
			this.cost = cost;
			this.partitionType = partitionType;
		}

		public float getCost() {
			return this.cost;
		}

		public Partition getPartition() {
			return this.partition;
		}

		public Node getNode() {
			return this.node;
		}

		public int getPartitionType() {
			return this.partitionType;
		}
	}
}
