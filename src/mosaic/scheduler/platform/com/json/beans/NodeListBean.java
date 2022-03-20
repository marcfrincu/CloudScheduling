package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class NodeListBean {
	private String node_id;
	private ComponentsBean[] components;

	public String getNode_id() {
		return node_id;
	}

	public ComponentsBean[] getComponents() {
		return components;
	}

	public void setNode_id(String nodeId) {
		node_id = nodeId;
	}

	public void setComponents(ComponentsBean[] components) {
		this.components = components;
	}	
}
