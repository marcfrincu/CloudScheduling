package mosaic.scheduler.platform.resources;

import java.util.UUID;
import java.util.Vector;

/**
 * Class for holding information about an application component
 * @author Marc Frincu
 *
 */
public final class Component  {
	/**
	 * Unique component ID
	 */
	private String ID = null;
	/**
	 * Vector indicating the IDs of other components this one links to
	 */
	private Vector<String> linkedTo = null; 
	/**
	 * The type of the component in case more than one types exist
	 */
	private int type = 0;
	/**
	 * Variable indicating whether the component can be collocated or not
	 */
	private boolean collocated = false;
	
	/**
	 * Creates a new component
	 * @param isCollocated true if it should be collocated or false otherwise
	 * @param type the type of the component
	 */
	public Component(boolean isCollocated, int type) {
		this.collocated = isCollocated;
		this.type = type;
		this.linkedTo = new Vector<String>();
		this.ID = UUID.randomUUID().toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Component clone() {
		Component c = new Component(this.collocated, this.type);
		c.linkedTo = (Vector<String>)c.linkedTo.clone();
		return c;
	}
	
	public void addConnection(String ID) {
		this.linkedTo.add(ID);
	}
	
	public Vector<String> getConnections() {
		return this.linkedTo;
	}
	
	public int getType(){
		return this.type;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public boolean getIsCollocated() {
		return this.collocated;
	}
}
