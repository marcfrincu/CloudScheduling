package mosaic.scheduler.platform.com.json.beans;

import mosaic.scheduler.platform.com.json.beans.ParamsRemoveNodes;

/**
 *
 * @author Mirela
 */
public class RequestRemoveNodes
{
 private String method;
	private ParamsRemoveNodes params = new ParamsRemoveNodes();
	private String id;
	private String jsonrpc;

	public String getMethod() {
		return method;
	}

	public ParamsRemoveNodes getParams() {
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

	public void setParams(ParamsRemoveNodes parms) {
		params = parms;
	}

	public void setId(String s) {
		id = s;
	}

	public void setJsonrpc(String x) {
		jsonrpc = x;
	}   
}
