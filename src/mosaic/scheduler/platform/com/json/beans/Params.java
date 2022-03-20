package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class Params {
	private static int number;
	private static String availability_zone_id;
	private static String cloud_id;

	public int getNumber() {
		return number;
	}

	public String getAvailability_zone_id() {
		return availability_zone_id;
	}

	public String getCloud_id() {
		return cloud_id;
	}

	public void setNumber(int x) {
		number = x;
	}

	public void setAvailability_zone_id(String s) {
		availability_zone_id = s;
	}

	public void setCloud_id(String s) {
		cloud_id = s;
	}

}
