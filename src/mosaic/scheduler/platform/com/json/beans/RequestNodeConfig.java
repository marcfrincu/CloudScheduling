package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class RequestNodeConfig {
	private String method;
	private RequestNodeConfigValues[] params = null;
	private String jsonrpc;
	private String id;

	public void setId(String id) {
		this.id = id;
	}

	public void setMethod(String s) {
		method = s;
	}

	public void setParams(RequestNodeConfigValues[] x) {
		params = x;
	}

	public void setJsonrpc(String x) {
		jsonrpc = x;
	}
        
	public String getJsonrpc() {
		return jsonrpc;
	}
        
	public String getId() {
		return id;
	}
        
	public String getMethod() {
		return method;
	}

	public RequestNodeConfigValues[] getParams() {
		return params;
	}

}
