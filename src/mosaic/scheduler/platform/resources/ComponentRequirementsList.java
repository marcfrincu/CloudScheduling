package mosaic.scheduler.platform.resources;

import java.util.Hashtable;


public class ComponentRequirementsList {
	Hashtable<Integer, AComponentRequirements> reqs = null;
	
	private static ComponentRequirementsList cr = null;
	
	private ComponentRequirementsList() {
		this.reqs = new Hashtable<Integer, AComponentRequirements>();
	}
	
	public static ComponentRequirementsList getComponentsRequirement() {
		if (cr == null) {
			cr = new ComponentRequirementsList();
		}
		return cr;
	}
	
	public void addComponentRequirement(int componentType, AComponentRequirements cr) {
		this.reqs.put(componentType,  cr);
	}
	
	
	public AComponentRequirements getComponentRequirements(int componentType) {
                return this.reqs.get(componentType);
	}
	
}
