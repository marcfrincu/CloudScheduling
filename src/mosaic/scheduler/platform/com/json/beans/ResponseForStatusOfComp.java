package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ResponseForStatusOfComp {
	private String id;
	private String jsonrpc;
	private String error;
	private ResponseForStatusOfCompResult result;
	
	public String getId() {
		return id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}
        
	public String getError() {
		return error;
	}

	public ResponseForStatusOfCompResult getResult() {
		return result;
	}
        
        public void setId(String s) {
                id = s;
        }

        public void setJsonrpc(String s) {
                jsonrpc = s;
        }
        
	public void setError(String error) {
		this.error = error;
	}

	public void setResult(ResponseForStatusOfCompResult x) {
		result = x;
	}

}
