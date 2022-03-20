package mosaic.scheduler.simulator.scaler;

import java.util.Vector;

import mosaic.scheduler.platform.algorithms.IAlgorithm;
import mosaic.scheduler.platform.algorithms.util.AlgorithmUtil;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.settings.SystemSettings;



/**
 * Class offering scaling functionality
 * @author Marc Frincu
 *
 */
public final class Scaler {

	IAlgorithm alg = null;
	
	public void assignAlgorithm(IAlgorithm alg) {
		this.alg = alg;
	}
	
	/**
	 * Scales the number of components according to the request rate. The request rate is simulated by varying the number of web servers
	 * @param noNewWebServers
	 * @param nodes
	 * @return
	 */
	public int[] scaleComponents(int noNewWebServers, Vector<Node> nodes){
		int[] components = new int[SystemSettings.getSystemSettings().getNo_component_types()];
		
		components[0] = noNewWebServers;
		int totalRead, totalWrite, cnI;
		
		for (int i=0;i<SystemSettings.getSystemSettings().getNo_component_types(); i++) {
			totalRead = 0;
			totalWrite = 0;
			for (int j=0; j<SystemSettings.getSystemSettings().getNo_component_types(); j++) {				
				if (i!=j && SystemSettings.getSystemSettings().getComponent_connection_table()[j][i] == 1) {
					//cnJ = this.alg.gatherComponents(nodes, j).size();
					//cnI = this.alg.gatherComponents(nodes, i).size();
					totalWrite += (components[j] /*+ cnJ*/) * SystemSettings.getSystemSettings().getComponent_write_rate()[j];
					totalRead += (components[i] /*+ cnI*/) * SystemSettings.getSystemSettings().getComponent_read_rate()[i];
				}
			}
			if (totalRead < totalWrite) {						
				components[i] += Math.round((totalWrite - totalRead) / (float)SystemSettings.getSystemSettings().getComponent_read_rate()[i]);
			}
		}
		for (int i=0;i<SystemSettings.getSystemSettings().getNo_component_types(); i++) {
			cnI = AlgorithmUtil.gatherComponents(nodes, i).size();
			components[i] = components[i] - cnI; 
			/**
			 * negative value means we need to remove the absolute value
			 * positive value means we need to add the value
			 */
		}
		return components;
	}	
}
