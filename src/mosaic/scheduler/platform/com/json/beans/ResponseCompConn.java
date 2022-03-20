package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ResponseCompConn {
	private String id;
	private String jsonrpc;
	private ResponseCompConnResult result;
	private String error;
	
	public String getError() {
		return error;
	}

	public String getId() {
		return id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public ResponseCompConnResult getResult() {
		return result;
	}
        
	public void setError(String error) {
		this.error = error;
	}

	public void setId(String s) {
		id = s;
	}

	public void setJsonrpc(String s) {
		jsonrpc = s;
	}

	public void setResult(ResponseCompConnResult x) {
		result = x;
	}

}
