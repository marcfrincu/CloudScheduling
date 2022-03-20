package mosaic.scheduler.platform.resources;

import java.util.UUID;
import java.util.Vector;

import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.simulator.util.misc.Pooled;


/**
 * Class that contains information about a partition.
 * A partition is an isolated memory space (similar with a JVM) in which components execute.
 * A node is made of several partitions which in turn can hold many components.
 * @author Marc Frincu
 *
 */
public final class Partition extends Pooled<Partition> {
	/**
	 * Unique partition ID
	 */
	private String ID = null;
	/**
	 * The list of components assigned to this partition
	 */
	private Vector<Component> components = new Vector<Component>();
	/**
	 * The ID of the node this component belongs to
	 */
	private String assignedNodeID = null;

	private static Pool<Partition> pool = new Pool<Partition>(Partition.class);
	
	public Partition(Pool<Partition> pool) {
		super(pool);
	}
	
	/**
	 * Calculates the load of the partition based on its components usage of the CPU, memory and bandwidth at the
	 * specified time
	 * @param time the time for which we want to compute the partition load
	 * @return a value between 0 and 100 representing the load of the partition
	 */
	public int computeLoad(int time) {
		ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();

		float load = 0;
		//the weights assigned to each of the node characteristic
		double w1 = SystemSettings.getSystemSettings().getNode_cpu_weight(), w2 = SystemSettings.getSystemSettings().getNode_memory_weight(), w3 = SystemSettings.getSystemSettings().getNode_network_weight();
		Vector<Component> comps = this.getAssignedComponents();
		for (Component component : comps) {
			load += (w1 * crl.getComponentRequirements(component.getType()).getCpuUsage(time) + 
					w2 * crl.getComponentRequirements(component.getType()).getMemoryUsage(time) + 
					w3 * crl.getComponentRequirements(component.getType()).getNetworkUsage(time));
		}
		return Math.round(load);
	}
	
	/**
	 * Returns a DEEP COPY of the partition
	 */
	@Override
	public Partition clone() {
		Partition part = Partition.provide(this.assignedNodeID);
		part.assignToNode(this.assignedNodeID);
		for (Component c : this.components)
			part.components.add(c.clone());
		return part;
	}
	
	public void assignToNode(String nodeID) {
		this.assignedNodeID = nodeID;
	}
	
	public void removeAssignedComponents() {
		this.components.clear();
	}
	
	public String getAssignedNodeID() {
		return this.assignedNodeID;
	}
	
	public String getID(){
		return this.ID;
	}
		
	public void addComponent(Component component) {
		this.components.add(component);
	}
	
	public Vector<Component> getAssignedComponents() {
		return this.components;
	}
	
	public static Partition provide (String nodeId) {
		return (pool.dequeue(nodeId));
	}
	
	protected void initialize(Object ... a) {
		if (a.length == 1) {
			this.assignedNodeID = (String)a[0];
			this.ID = UUID.randomUUID().toString();
		}
	}
		
	protected void deinitialize() {
		components.clear();
	}
}
