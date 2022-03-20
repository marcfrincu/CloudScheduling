package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class PartitionLoadListBean {
	private int component_type;
	private ComponentLoadListMinutesBean component_load;
	private int number_components;
	private int partition_index;
        
	public int getComponent_type() {
		return component_type;
	}
	public void setComponent_type(int componentType) {
		component_type = componentType;
	}
	public ComponentLoadListMinutesBean getComponent_load() {
		return component_load;
	}
	public void setComponent_load(ComponentLoadListMinutesBean componentLoad) {
		component_load = componentLoad;
	}
	public int getNumber_components() {
		return number_components;
	}
	public void setNumber_components(int numberComponents) {
		number_components = numberComponents;
	}
	public int getPartition_index() {
		return partition_index;
	}
	public void setPartition_index(int partitionIndex) {
		partition_index = partitionIndex;
	}


	
	
}
