package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class RequestForStatusOfComp {
	private String method;
	private NodeIDBean params;
	private String id;
	private String jsonrpc;

	public String getMethod() {
		return method;
	}

	public NodeIDBean getParams() {
		return params;
	}

	public String getId() {
		return id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setMethod(String s) {
		method = s;
	}

	public void setParams(NodeIDBean s) {
		params = s;
	}

	public void setId(String s) {
		id = s;
	}

	public void setJsonrpc(String x) {
		jsonrpc = x;
	}

}
