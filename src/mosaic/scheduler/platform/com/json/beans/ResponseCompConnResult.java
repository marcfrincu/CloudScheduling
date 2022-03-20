package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ResponseCompConnResult {
	private String method;
	private ResponseCompConnParams params;

	public String getMethod() {
		return method;
	}

	public ResponseCompConnParams getParams() {
		return params;
	}

	public void setMethod(String s) {
		method = s;
	}

	public void setParams(ResponseCompConnParams x) {
		params = x;
	}

}
