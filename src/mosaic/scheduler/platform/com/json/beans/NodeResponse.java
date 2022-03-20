package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class NodeResponse {
	private String id;
	private String jsonrpc;
	private NodeResultBean result;
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

	public NodeResultBean getResult() {
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

	public void setResult(NodeResultBean node) {
		result = node;
	}

}
