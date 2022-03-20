package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class RequestNodeConfigValues {
	private int number_components;
	private int component_type;
	private String node_id;
	private int partition_index;

	public int getNumber_components() {
		return number_components;
	}

	public int getComponent_type() {
		return component_type;
	}

	public String getNode_id() {
		return node_id;
	}

	public int getPartition_index() {
		return partition_index;
	}

	public void setPartition_index(int partitionIndex) {
		partition_index = partitionIndex;
	}

	public void setNumber_components(int x) {
		number_components = x;
	}

	public void setComponent_type(int x) {
		component_type = x;
	}

	public void setNode_id(String s) {
		node_id = s;
	}

}
