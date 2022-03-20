package mosaic.scheduler.test;

import java.util.Vector;

import mosaic.scheduler.platform.algorithms.OurManyToOne;
import mosaic.scheduler.platform.resources.ComponentRequirementsList;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.resources.Partition;
import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.simulator.resources.ComponentRequirements;
import mosaic.scheduler.simulator.util.Runner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class TestRunner {

	private static Logger logger = Logger.getLogger(TestRunner.class.getPackage().getName());
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("logging.properties");
		
		SystemSettings.getSystemSettings().loadProperties("settings/system.properties");
		
		Runner r= new Runner();
		
		OurManyToOne  o = new OurManyToOne();
		
		Vector<Node> nodes = new Vector<Node>();
		nodes.add(new Node("DC1", "C1"));

		for (int i=0; i<SystemSettings.getSystemSettings().getNo_component_types(); i++) {
			ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();
			crl.addComponentRequirement(i, new ComponentRequirements());

			//each component type has assigned to it a number of partitions equal with the total number of nodes that can be added
			for (int j=0; j<SystemSettings.getSystemSettings().getMax_number_nodes(); j++) {
				nodes.get(0).addPartition(Partition.provide(nodes.get(0).getID()));
			}
		}
			
		r.addAlgorithm(o, nodes);
		
		r.run();
	}

}
