package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author Mirela
 */
public class QueueListMessagesBean {
	private double oneMin;
	private double threeMin;
	private double fiveMin;

	public void setOneMin(double x) {
		oneMin = x;
	}

	public void setThreeMin(double x) {
		threeMin = x;
	}

	public void setFiveMin(double x) {
		fiveMin = x;
	}

	public double getOneMin() {
		return oneMin;
	}

	public double getThreeMin() {
		return threeMin;
	}

	public double getFiveMin() {
		return fiveMin;
	}
}
