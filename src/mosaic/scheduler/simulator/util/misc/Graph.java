package mosaic.scheduler.simulator.util.misc;

import mosaic.scheduler.platform.settings.SystemSettings;

public class Graph {

	public double[][] adjacency = new double[SystemSettings.getSystemSettings().getMax_number_nodes()][SystemSettings.getSystemSettings().getMax_number_nodes()];
	
	public Graph() {
		for (int i=0; i< SystemSettings.getSystemSettings().getMax_number_nodes(); i++) 
			for (int j=0; j< SystemSettings.getSystemSettings().getMax_number_nodes(); j++)
				this.adjacency[i][j] = Math.random();
	}
}
