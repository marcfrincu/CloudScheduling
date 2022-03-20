package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class NodeResultBean {
	private String method;
	private NodeResultParmsBean params;

	public String getMethod() {
		return method;
	}

	public NodeResultParmsBean getParams() {
		return params;
	}

	public void setMethod(String s) {
		method = s;
	}

	public void setParams(NodeResultParmsBean node) {
		params = node;
	}

}
