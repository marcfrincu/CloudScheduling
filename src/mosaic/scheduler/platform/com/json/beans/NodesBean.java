package mosaic.scheduler.platform.com.json.beans;

public class NodesBean {
	private String node_id, node_datacenter_id, node_cloud_id;
	private NodesLoadBean node_load;

	public void setNode_id(String nodeId) {
		node_id = nodeId;
	}

	public void setNode_datacenter_id(String nodeDatacenterId) {
		node_datacenter_id = nodeDatacenterId;
	}

	public void setNode_cloud_id(String nodeCloudId) {
		node_cloud_id = nodeCloudId;
	}

	public void setNode_load(NodesLoadBean nodeLoad) {
		node_load = nodeLoad;
	}

	public String getNode_id() {
		return node_id;
	}

	public NodesLoadBean getNode_load() {
		return node_load;
	}

	public String getNode_datacenter_id() {
		return node_datacenter_id;
	}

	public String getNode_cloud_id() {
		return node_cloud_id;
	}
}
