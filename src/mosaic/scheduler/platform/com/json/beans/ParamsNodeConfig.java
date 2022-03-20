package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ParamsNodeConfig {
	private static int number_components;
	private static int component_type;
	private static int node_id;

	public int[] getNodeConfig() {
		int[] a = new int[3];
		a[0] = number_components;
		a[1] = component_type;
		a[2] = node_id;
		return a;
	}

	public void setNodeConfig(int x, int y, int z) {
		number_components = x;
		component_type = y;
		node_id = z;
	}

}
