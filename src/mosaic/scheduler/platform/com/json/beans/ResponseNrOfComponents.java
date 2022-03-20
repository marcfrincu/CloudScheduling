package mosaic.scheduler.platform.com.json.beans;


/**
 * 
 * @author balus.tudor
 */
public class ResponseNrOfComponents {
	private String id;
	private String jsonrpc;
	private ResponseNrOfComponentsRes result = new ResponseNrOfComponentsRes();
	private String error;
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setId(String s) {
		id = s;
	}

	public void setJsonrpc(String s) {
		this.jsonrpc = s;
	}

	public void setResult(ResponseNrOfComponentsRes x) {
		result = x;
	}

	public String getId() {
		return id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}
	
	public ResponseNrOfComponentsRes getResult() {
		return result;
	}
}
