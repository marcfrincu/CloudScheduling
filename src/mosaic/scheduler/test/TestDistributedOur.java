package mosaic.scheduler.test;

import java.util.Vector;

import mosaic.scheduler.platform.resources.ComponentRequirementsList;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.simulator.algorithms.DistributedOur;
import mosaic.scheduler.simulator.resources.ComponentRequirements;
import mosaic.scheduler.simulator.scaler.Scaler;
import mosaic.scheduler.simulator.util.Runner;
import mosaic.scheduler.simulator.util.math.Probability;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



public class TestDistributedOur {
	private static Logger logger = Logger.getLogger(Runner.class.getPackage().getName());

	public static int NO_NODES = 100;//100
	public static int LOOP_NO = 300;//300
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("logging.properties");
		
		SystemSettings.getSystemSettings().loadProperties("mosaic/scheduler/simulator/settings/system.properties");

		DistributedOur so = new DistributedOur();
		
		Vector<Node> nodes = new Vector<Node>();
		for (int i=0; i< TestDistributedOur.NO_NODES; i++)
			nodes.add(new Node("1", "1"));
		
		for (int i=0; i<SystemSettings.getSystemSettings().getNo_component_types(); i++) {
			ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();
			crl.addComponentRequirement(i, new ComponentRequirements());
		}
		
		Scaler scaler = new Scaler();
		scaler.assignAlgorithm(so);
		
		double time, noNewWebServers;
		int[] components = new int[]{};
		long s = System.currentTimeMillis();
		
		double percentageNotHAFinal[] = new double[TestDistributedOur.LOOP_NO];
	
		for (int t = 0; t<TestDistributedOur.LOOP_NO; t++) {
			/*time = (double)t / SystemSettings.getSystemSettings().getTime_span() - 0.5;
			noNewWebServers = 3.1 - 8.5 * time + 24.7 * Math.pow(time, 2) + 130.8
			* Math.pow(time, 3) + 107.7 * Math.pow(time, 4) - 804.2
			* Math.pow(time, 5) - 2038.5 * Math.pow(time, 6) + 1856.8
			* Math.pow(time, 7) + 4618.6 * Math.pow(time, 8);
			*/
			noNewWebServers = Probability.generatePareto(1.75, 1.68);
		
			if (t < 150) //{
				components = scaler.scaleComponents((int)noNewWebServers, nodes);
			///	System.out.println("time " + t + " " + (int)noNewWebServers);
			//	for (int i : components)
			//		System.out.print (i + " ");
			//	System.out.println();
			//}
			//else {
		//		components = new int[SystemSettings.getSystemSettings().getNo_component_types()];
		//	}
			nodes = so.executeOnce(nodes, components, t);
			//TestDistributedOur.logger.error("Time: " + t);
			System.out.println(t);

		}
		TestDistributedOur.logger.debug("Execution time: " + (System.currentTimeMillis() - s));
		
		//System.out.println(so.getNoMessagesSent()+ "\t" + so.getNoMessagesSent() / (double)TestDistributedOur.LOOP_NO);
		//System.out.println(so.getNoAdhocCreatedComponents()+ "\t" + so.getNoAdhocCreatedComponents() / (double)TestDistributedOur.LOOP_NO);
		double percentageNotHA = 0, percentageNotEmptyLoad = 0;
		Vector<Integer> noComps = new Vector<Integer>();
		
		int count=0; 
		int reliabilityOur = 0;
		for (int i=0; i<TestDistributedOur.LOOP_NO; i++) {
			percentageNotHA = 0;
			percentageNotEmptyLoad = 0;
			noComps.clear();
			for (int j=0; j< nodes.size(); j++) {
//				if (nodes.get(j).history.get(i)!=null)
		//			TestDistributedOur.logger.info("Time: " + i + "\tNode: " + j + "\tLoad: " + nodes.get(j).history.get(i).getLoad() + "\t#Service types: " + nodes.get(j).history.get(i).getNoServiceTypes() + "\tStarted search: " + nodes.get(j).history.get(i).isOverloaded()+ "\tRelay node: " + nodes.get(j).history.get(i).isRelayNode() + "\tStopped: " + nodes.get(j).history.get(i).isStopped());
				//Compute the number of nodes that do not have HA at time i
				if (nodes.get(j).history.get(i).getNoServiceTypes() != SystemSettings.getSystemSettings().getNo_component_types() && nodes.get(j).history.get(i).getLoad() > 0)
					percentageNotHA++;
				if (nodes.get(j).history.get(i).getLoad() > 0) {
					percentageNotEmptyLoad++;
					noComps.add(nodes.get(j).history.get(i).getNoServiceTypes());
				}
				if (nodes.get(j).history.get(i).getLoad() > SystemSettings.getSystemSettings().getMax_node_load_threshold()+10)
					count++;
					
			}
			percentageNotEmptyLoad = percentageNotEmptyLoad == 0 ? 1 : percentageNotEmptyLoad; 
			//compute and store the percentage of nodes without HA at time i
			percentageNotHAFinal[i] = percentageNotHA * 100 / percentageNotEmptyLoad;
			System.out.println(/*(nodes.size() - percentageNotHA) + "\t" + percentageNotHA + "\t" + */percentageNotHA * 100 / percentageNotEmptyLoad);
			if (noComps.size() > 1 && noComps.get((int)(Math.random() * (noComps.size() -1 ) + 1)) < SystemSettings.getSystemSettings().getNo_component_types())
				reliabilityOur++;
			
		}
		
		System.out.println((1-(float)reliabilityOur / TestDistributedOur.LOOP_NO));
	//	if (count == 0)	
		//System.out.println(Statistics.computeMean(percentageNotHAFinal) + "\n" + Statistics.computeStandardDeviation(percentageNotHAFinal));
	//	else
	//		System.out.println("error " + count);
	}
	
}
