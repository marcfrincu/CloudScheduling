package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class Request {
	private String method;
	private Params params = new Params();
	private String id;
	private String jsonrpc;

	public String getMethod() {
		return method;
	}

	public Params getParams() {
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

	public void setParams(Params parms) {
		params = parms;
	}

	public void setId(String s) {
		id = s;
	}

	public void setJsonrpc(String x) {
		jsonrpc = x;
	}

}
