package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ResponseForStatusOfCompResult {
	private String method;
	private ResponseForStatusOfCompParams params;

	public String getMethod() {
		return method;
	}

	public ResponseForStatusOfCompParams getParams() {
		return params;
	}

	public void setParams(ResponseForStatusOfCompParams x) {
		params = x;
	}

	public void setMethod(String s) {
		method = s;
	}

}
