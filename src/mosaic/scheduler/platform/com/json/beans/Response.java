package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class Response {
	private String id;
	private String jsonrpc;
	private ResponseResult result;
	private String error;

	public void setId(String s) {
		this.id = s;
	}

	public void setJsonrpc(String s) {
		this.jsonrpc = s;
	}

	public void setResult(ResponseResult res) {
		this.result = res;
	}

	public void setError(String s) {
		this.error = s;
	}

	public String getId() {
		return id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public ResponseResult getResult() {
		return result;
	}

	public String getError() {
		return error;
	}

}
