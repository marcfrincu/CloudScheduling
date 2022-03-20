package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ComponentWorkflowBean {
	private int component_type;
	private String[] read_rate, write_rate;
	private String[] linked_to_component;

	public void setComponent_type(int x) {
		component_type = x;
	}

	public void setLinked_to_Component(String[] s) {
		linked_to_component = s;
	}

	public void setRead_rate(String[] s) {
		read_rate = s;
	}

	public void setWrite_rate(String[] s) {
		write_rate = s;
	}

	public int getComponent_type() {
		return component_type;
	}

	public String[] getLinked_to_Component() {
		return linked_to_component;
	}

	public String[] getRead_rate() {
		return read_rate;
	}

	public String[] getWrite_rate() {
		return write_rate;
	}

}
