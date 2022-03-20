package mosaic.scheduler.platform.resources;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

import mosaic.scheduler.platform.settings.SystemSettings;


/**
 * Class for holding information related to a computing node.
 * @author Marc Frincu
 *
 */
public final class Node {
	/**
	 * Unique node ID
	 */
	private String ID = null;
	/**
	 * Unique datacenter ID to which the node belongs to
	 */
	private String dataCenterID = null;
	/**
	 * Unique cloud ID to which the datacenter belongs to
	 */
	private String cloudID = null;
	private Vector<Partition> partitions = null;
	
	private int nodePartitionIterator = 0;
		
	public Vector<Integer> markedBy = null;
	
	public Hashtable<Integer, NodeHistory> history = null;
	
	public boolean startedSearch = false;
	public boolean isRelayNode = false;
	public boolean isStopped = false;
	
	public static enum NODE_STATUS {NEW, EXISTING, TO_BE_REMOVED};
	
	public NODE_STATUS status = NODE_STATUS.NEW;
	
	/**
	 * Adds a new node to the cloud federation
	 * @param dataCenterID the ID of the data center
	 * @param cloudID the ID of the cloud
	 */
	public Node(String dataCenterID, String cloudID) {
		 this (UUID.randomUUID().toString(), dataCenterID, cloudID);
	}
	
	/**
	 * Adds a new node to the cloud federation
	 * @param nodeID the predefined nodeID
	 * @param dataCenterID the ID of the data center
	 * @param cloudID the ID of the cloud
	 */
	public Node(String nodeID, String dataCenterID, String cloudID) {
		this.history = new Hashtable<Integer,NodeHistory>();
		this.partitions = new Vector<Partition>();
		this.dataCenterID = dataCenterID;
		this.cloudID = cloudID;
		this.ID = nodeID;
		this.markedBy = new Vector<Integer>();
	}
		
	/**
	 * Compute the load of this node at a given time moment.
	 * @param time the moment in time
	 * @return an integer value between 0 and 100 indicating the load of the system in terms of CPU, memory and network usage
	 */
	public int computeLoad(int time, int k) {
		ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();
		float load = 0;
		//the weights assigned to each of the node characteristic
		double w1 = SystemSettings.getSystemSettings().getNode_cpu_weight(), w2 = SystemSettings.getSystemSettings().getNode_memory_weight(), w3 = SystemSettings.getSystemSettings().getNode_network_weight();
		float variation = 0;
		Vector<Component> comps = null;
		//System.out.println("sdsdsdsds: " + this.partitions.size());
		for (Partition partition : this.partitions) {			
			comps = partition.getAssignedComponents();
			for (Component component : comps) {
				switch (SystemSettings.getSystemSettings().getNode_type()) {
				case UNIFORM: variation = (float)SystemSettings.getSystemSettings().getNode_variation()[0][k];
								break;
				case UNRELATED: variation = (float)SystemSettings.getSystemSettings().getNode_variation()[k][component.getType()];
								break;
				case HOMOGENEOUS: variation = 1;
								break;
				default: variation = 1;
						break;
				}				
				load += variation * (
						w1 * crl.getComponentRequirements(component.getType()).getCpuUsage(time) + 
						w2 * crl.getComponentRequirements(component.getType()).getMemoryUsage(time) +
						w3 * crl.getComponentRequirements(component.getType()).getNetworkUsage(time)
					);				
			}
		}
		
		return Math.round(load);
	}
	
	/**
	 * This method computes the load of node given a <i>customNodeVariation</i>
	 * @param time
	 * @param k
	 * @param customNodeVariation a max_number_nodes x no_component_types matrix indicating the fluctuation of the execution time of a component type on every existing node
	 * @return an integer value between 0 and 100 indicating the load of the system in terms of CPU, memory and network usage
	 */
	public int computeLoad(int time, int k, double[][] customNodeVariation) {
		ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();
		float load = 0;
		//the weights assigned to each of the node characteristic
		double w1 = SystemSettings.getSystemSettings().getNode_cpu_weight(), w2 = SystemSettings.getSystemSettings().getNode_memory_weight(), w3 = SystemSettings.getSystemSettings().getNode_network_weight();
		Vector<Component> comps = null;
		for (Partition partition : this.partitions) {
			comps = partition.getAssignedComponents();
			for (Component component : comps) {								
				load += customNodeVariation[k][component.getType()] * (
						w1 * crl.getComponentRequirements(component.getType()).getCpuUsage(time) + 
						w2 * crl.getComponentRequirements(component.getType()).getMemoryUsage(time) +
						w3 * crl.getComponentRequirements(component.getType()).getNetworkUsage(time)
					);
			}
		}
		return Math.round(load);
	}

	/**
	 * Computes the load of one of this node's partition
	 * @param time the moment in time
	 * @param p the partition we are interested in
	 * @return an integer value between 0 and 100 indicating the weighted load of the system in terms of CPU, memory and network usage 
	 */
	public int computeLoad(int time, Partition p) {
		//ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();		
		//the weights assigned to each of the node characteristic
		//float w1 = 1, w2 = 1, w3 = 1;
		//Vector<Component> comps = null;
		//float load = 0;
		for (Partition partition : this.partitions) {
			if (partition.getID().compareToIgnoreCase(p.getID()) == 0) {
				return partition.computeLoad(time);
				/*comps = partition.getAssignedComponents();
				for (Component component : comps) {
					
					load += (w1 * crl.getComponentRequirements(component.getType()).getCpuUsage(time) + 
							w2 * crl.getComponentRequirements(component.getType()).getMemoryUsage(time) + 
							w3 * crl.getComponentRequirements(component.getType()).getNetworkUsage(time)) / (w1 + w2 + w3);
				}*/
				//break;
			}
		}
		return -1;
		//return Math.round(load);
	}
	
	/**
	 * Method for returning a DEEP COPY of the node and its components
	 */
	@Override
	public Node clone() {
		Node node = new Node(this.ID, this.dataCenterID, this.cloudID);
		Vector<Partition> parts = new Vector<Partition>();
		for (Partition p : this.partitions) {
			parts.add(p.clone());
		}
		node.partitions = parts;
		node.history = new Hashtable<Integer, NodeHistory>();
		Integer key;
		Enumeration<Integer> keys = this.history.keys();
		while(keys.hasMoreElements()) {
			key = keys.nextElement();
			node.history.put(key, this.history.get(key).clone());
		}
		
		return node;
	}
	
	public final int getNodePartitionIterator() {
		return this.nodePartitionIterator;
	}
	
	public final int modifyNodePartitionIterator() {
		final int n = this.nodePartitionIterator;
		if (1 >= this.partitions.size() - 1) {
			this.nodePartitionIterator = 0;
		}
		else
			this.nodePartitionIterator = 1;
		return n;	
	}
		
	public final void addPartition(Partition partition) {
		this.partitions.add(partition);		
	}

	public final Partition removePartition(Partition partition) {
		for (int i=0; i<this.partitions.size(); i++)
			if (this.partitions.get(i).getID().compareToIgnoreCase(partition.getID())==0) {
				return this.partitions.remove(i);
			}
		return null;		
	}
	
	public final Vector<Partition> getAssignedPartitions() {
		return this.partitions;
	}
	
	public final Partition getPartition(int index) {
		if (index > 0 && index < this.partitions.size())
			return this.partitions.get(index);
		return null;
	}
	
	public final String getID() {
		return this.ID;
	}
	
	public final String getDataCenterID() {
		return this.dataCenterID;
	}
	
	public final String getCloudID() {
		return this.cloudID;
	}
	
	public void setVisited(int nodeIndex) {	
		this.markedBy.add(nodeIndex);
	}
	
	public void removeVisited(int nodeIndex) {
		for (int i=0; i<this.markedBy.size(); i++) {
			if (this.markedBy.get(i) == nodeIndex) {
				this.markedBy.remove(i);
				break;
			}
			
		}
	}

	public void addMarked(Vector<Integer> markedBy) {
		for (int i : markedBy) {
			if (!this.markedBy.contains(i))
				this.markedBy.add(i);
		}	
	} 
	
	/**
	 * Class for holding historical information on the node at a given moment: load, cost, number of service types and 
	 * number of services per service type
	 * @author Marc Frincu
	 *
	 */
	public final class NodeHistory {
		private int load, noServiceTypes; 
		private float cost;
		private String numberServicesPerType;
		private boolean isOverloaded;
		private boolean isRelayNode;
		private boolean isStopped;
		
		public NodeHistory(int load, 
							int noServiceTypes, 
							float cost, 
							String numberServicesPerType, 
							boolean isOverloaded,
							boolean isRelayNode,
							boolean isStopped) {
			this.load = load;
			this.noServiceTypes = noServiceTypes;
			this.cost = cost;
			this.numberServicesPerType = numberServicesPerType;
			this.isOverloaded = isOverloaded;
			this.isRelayNode = isRelayNode;
			this.isStopped = isStopped;
		}

		public int getLoad() {
			return this.load;
		}
		
		public int getNoServiceTypes() {
			return this.noServiceTypes;
		}

		public String getNumberServicesPerType() {
			return this.numberServicesPerType;
		}
		
		public float getCost() {
			return this.cost;
		}		
	
		public boolean isOverloaded() {
			return isOverloaded;
		}

		public boolean isRelayNode() {
			return isRelayNode;
		}

		public boolean isStopped() {
			return isStopped;
		}

		public NodeHistory clone() {
			return new NodeHistory (this.load, this.noServiceTypes, this.cost, this.numberServicesPerType, this.isOverloaded, this.isRelayNode, this.isStopped);
		}
		
		
		
	}
}
