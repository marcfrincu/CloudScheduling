package mosaic.scheduler.platform.algorithms;

import java.util.Vector;

import mosaic.scheduler.platform.resources.Node;


/**
 * Interface for the scheduling algorithm. It offers methods for executing the heuristics only once.
 * The algorithm should be executed only when the number of components or the characteristics of
 * the system/application's have changed (Reactive and not proactive method). It is not meant for 
 * periodic executing for reasons of efficiency.  
 * @author Marc Frincu
 * @since 2012
 */
public interface IAlgorithm {
	
	/**
	 * Method that reschedules the components on the given node vector and returns a COPY of the new node list.
	 * The algorithm reschedules components if certain thresholds are broken (e.g., node load) 
	 * @param nodes the list of nodes needing rescheduling
	 * @param componentsToBeGenerated the number of components to be generated on the existing nodes
	 * @param time the moment in time this schedule corresponds to
	 * @return the list with the (new) nodes and their assigned components after rescheduling
	 */
	public Vector<Node> executeOnce(Vector<Node> nodes, int[] componentsToBeGenerated, int time);
		
	/**
	 * Method that reschedules the components on the given node vector and returns a COPY of the new node list.
	 * In this method the new components are not placed by the algorithm but are already present. The algorithm
	 * only reschedules if certain thresholds (e.g., node load) are broken by the placement
	 * @param nodes the list of nodes needing rescheduling
	 * @param time the moment in time this schedule corresponds to
	 * @return the list with the (new) nodes and their assigned components after rescheduling
	 */
	public Vector<Node> executeOnce(Vector<Node> nodes,  int time);
}
