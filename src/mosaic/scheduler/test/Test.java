package mosaic.scheduler.test;

import java.util.Vector;

import mosaic.scheduler.platform.algorithms.OurManyToOne;
import mosaic.scheduler.platform.resources.ComponentRequirementsList;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.resources.Partition;
import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.simulator.resources.ComponentRequirements;
import mosaic.scheduler.simulator.scaler.Scaler;



public class Test {

	/*public static int MAX_NUMBER_NODES = 30;
	public static int NO_COMPONENT_TYPES = 6;
	public static int LOAD_THRESHOLD = 100; 
	public static int TIME_SPAN = 24;
	
	public static int[][] COMPONENT_CONNECTION_TABLE = new int[][]{ {0,1,1,1,0,0},
																	{1,0,0,0,1,0},
																	{1,0,0,0,0,1},
																	{1,0,0,0,0,1},
																	{0,1,0,0,0,1},
																	{0,0,1,1,1,0},
																   };
	public static int[] COMPONENT_READ_RATE = new int[]{100,50,50,50,50,100};
	public static int[] COMPONENT_WRITE_RATE = new int[]{100,40,40,40,40,100};
	
	public static double[] NODE_UNIFORM = new double[]{0.75, 0.43, 0.09, 0.80, 0.25, 0.15, 0.18, 0.08, 0.85, 0,69, 0.12, 0.75, 0.3, 0.49, 0.12, 0.67, 0.81, 0.34, 0.54, 0.46, 0.18, 0.62, 0.77, 0.25, 0.35, 0.11, 0.99};

	public static double [][] NODE_UNRELATED = new double[][]{
															{0.8,0.63,0.09,0.23,0.52,0.27},
															{0.75,0.03,0.32,0.11,0.56,0.51},
															{0.42,0.79,0.81,0.56,0.84,0.81},
															{0.75,1,0.67,0.81,0.16,0.85},
															{0.63,0.18,0.75,0.28,0.71,0.15},
															{0.42,0.43,0.97,0.18,0.54,0.25},
															{0.06,0.08,0.72,0.01,0.54,0.68},
															{0.2,0.92,0.75,0.48,0.43,0.52},
															{0.89,0.89,0.52,0.14,0.98,0.89},
															{0.96,0.96,0.72,0.17,0.26,0.27},
															{0.14,0.71,0.52,0.86,0.17,0.85},
															{0.29,0.38,0.09,0.58,0.66,0.36},
															{0.35,0.81,0.46,0.59,0.65,0.55},
															{0.09,0.23,0.75,0.18,0.91,0.44},
															{0.39,0.73,0.07,0.42,0.36,0.18},
															{0.12,0.5,0.32,0.4,0.42,0.88},
															{0.57,0.1,0.92,0.64,0.09,0.9},
															{0.78,0.7,0.12,0.88,0.3,0.73},
															{0.67,0.61,0.85,0.94,0.89,0.78},
															{0.77,0.42,0.75,0.01,0.7,0.65},
															{0.01,0.24,0.44,0.56,0.35,0.55},
															{0.22,0.88,0.79,0.64,0.28,0.37},
															{0.2,0.76,0.61,0.72,0.45,0.85},
															{0.17,0.19,0,0.02,0.17,0.71},
															{0.38,0.61,0.57,0.66,0.76,0.67},
															{0.92,0.2,0.87,0.18,0.05,0.64},
															{0.5,0.54,0.87,0.29,0.1,0.84},
															{0,0.06,0.3,0.13,0.9,0.63},
															{0.06,0.02,0.29,0.68,0.37,0.84},
															{0.55,0.24,0.75,0.39,0.54,0.36}		
														};
	*/
	public static void main(String args[]) throws Exception {
		new Test();
	}	
	
	public Test() throws Exception {
		Vector<Node> nodes = new Vector<Node>();
		//start with one node
		nodes.add(new Node("DC1", "C1"));
		
		/*DecimalFormat df = new DecimalFormat("#.##");
		for (int i=0;i<Test.MAX_NUMBER_NODES; i++) {
			System.out.print("{");
		
			for (int j=0;j<Test.NO_COMPONENT_TYPES-1; j++) {
				System.out.print(df.format(Math.random()) + ",");
			}
			System.out.print(df.format(Math.random()));
			System.out.println("},");
		}
		System.exit(0);
		*/
		for (int i=0; i<SystemSettings.getSystemSettings().getNo_component_types(); i++) {
			ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();
			crl.addComponentRequirement(i, new ComponentRequirements());

			//each component type has assigned to it a number of partitions equal with the total number of nodes that can be added
			for (int j=0; j<SystemSettings.getSystemSettings().getMax_number_nodes(); j++) {
				nodes.get(0).addPartition(Partition.provide(nodes.get(0).getID()));
			}
		}
		
	//	RoundRobin rr = new RoundRobin();
		OurManyToOne  o = new OurManyToOne();
				
		Vector<Node> rrNodes = Test.cloneNodes(nodes);//AAlgorithm.deepCopyNodes(nodes);
		
		long rrTime = 0, ourTime = 0, start;		
		
		Scaler scaler = new Scaler();
		scaler.assignAlgorithm(o);
		
		int reliabilityRR = 0, reliabilityOur = 0;
		
		int total = 0;
		double noNewWebServers = 0;
		int[] noComponents;
		int sum = 0;
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			// Generate arrival rate using an 8 degree extrapolation polynomial
			// t in [-0.5,0.5] corresponding to the 8:30-18:00 time interval
			final double t = i/(double)SystemSettings.getSystemSettings().getTime_span() - 0.5;
						
			noNewWebServers = 3.1 - 8.5 * t + 24.7 * Math.pow(t, 2) + 130.8
					* Math.pow(t, 3) + 107.7 * Math.pow(t, 4) - 804.2
					* Math.pow(t, 5) - 2038.5 * Math.pow(t, 6) + 1856.8
					* Math.pow(t, 7) + 4618.6 * Math.pow(t, 8);
						
			//noNewWebServers = Probability.generateWeibull(0.17, 0.60) ;
			//noNewWebServers = Probability.generatePareto(1.75, 1.68);
			//noNewWebServers *= 10;
			//noNewWebServers = 5000;
			
			noComponents = scaler.scaleComponents((int)noNewWebServers, nodes);

			System.out.println("Generated components at step " + i + " for " + (int)noNewWebServers + " new Web Servers");
			
			for (int k=0; k<noComponents.length; k++) {
				System.out.print(noComponents[k] + "\t");
				sum += noComponents[k];
			}
			System.out.println(" Total until now: " + sum);
			
			total += noNewWebServers;
			//apply the algorithm
			start = System.currentTimeMillis();			
			//rr.executeOnce(rrNodes, noComponents, i);
			rrTime += System.currentTimeMillis() - start;
			
			start = System.currentTimeMillis();			
			nodes = o.executeOnce(nodes, noComponents, i);
			ourTime += System.currentTimeMillis() - start;
			
			if (nodes.size() > 1) {
				if (nodes.get(0 + (int)(Math.random() * ((nodes.size() - 1 - 0) + 1))).history.get(i).getNoServiceTypes() < SystemSettings.getSystemSettings().getNo_component_types()) {
					reliabilityOur++;
				}
			}
				
			if (rrNodes.size() > 1) {
				if (rrNodes.get(0 + (int)(Math.random() * ((rrNodes.size() - 1 - 0) + 1))).history.get(i).getNoServiceTypes() < SystemSettings.getSystemSettings().getNo_component_types()) {
					reliabilityRR++;
				}
			}			
		//	System.out.println(((i+6)%24) + "\t" + (int)noNewWebServers + " " + (ourTime - System.currentTimeMillis()));
		}

		/*
		 * For statistics & graphics 
		 */
		
		//this.showResultsAvailability(nodes);
	
		//this.showResultsCost(nodes);
		
		//!!!! IMPORTANT the history field needs to be managed by the one implementing the algorithm. see Our.java or RoundRobin.java for examples
		this.showResultsLoad(nodes);
	
		//this.showResultsAvailability(rrNodes);

		//this.showResultsCost(rrNodes);
		
		//this.showResultsLoad(rrNodes);

		System.out.println("RELIABILITY " + (1-(float)reliabilityOur / SystemSettings.getSystemSettings().getTime_span()) + "\t" + (1-(float)reliabilityRR / SystemSettings.getSystemSettings().getTime_span()));
		System.out.println("TOTAL COMPONENTS "  + total);
		System.out.println(ourTime/(float)SystemSettings.getSystemSettings().getTime_span() + " " + rrTime/(float)SystemSettings.getSystemSettings().getTime_span());
		//System.out.println(o.computePlatformCost(nodes) + " " + rr.computePlatformCost(rrNodes));
		
	}
	
	private void showResultsLoad(Vector<Node> nodes) {
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			for (int j=0; j< nodes.size(); j++) {
				System.out.print(i + "\t" + j + "\t");
				System.out.println((nodes.get(j).history.get(i) == null ? -1 : (nodes.get(j).history.get(i).getLoad()) + "\t" + nodes.get(j).history.get(i).getNumberServicesPerType()));
			}
			System.out.println();
		}
		
		int noOverloaded;
		int noOverLimit;
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			noOverloaded = 0;
			noOverLimit = 0;
			for (int j=0; j< nodes.size(); j++) {
				 if (nodes.get(j).history.get(i) != null && nodes.get(j).history.get(i).getLoad() > SystemSettings.getSystemSettings().getMax_node_load_threshold()) {
					 noOverloaded++;
				 }
				 if (nodes.get(j).history.get(i) != null && nodes.get(j).history.get(i).getLoad() > 100) {
					 noOverLimit++;
				 }
			}
			System.out.println(noOverLimit + "\t" + noOverloaded);
		}
	}

	private void showResultsCost(Vector<Node> nodes) {
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			for (int j=0; j< nodes.size(); j++) {
				System.out.print(i + "\t" + j + "\t");
				System.out.println((nodes.get(j).history.get(i) == null ? 0 : nodes.get(j).history.get(i).getCost()) + "\t");
			}
			System.out.println();
		}
	}

	private void showResultsAvailability(Vector<Node> nodes) {
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			for (int j=0; j< nodes.size(); j++) {
				System.out.print(i + "\t" + j + "\t");
				System.out.println((nodes.get(j).history.get(i) == null ? 0 : nodes.get(j).history.get(i).getNoServiceTypes()) + "\t");
			}
			System.out.println();
		}
	}

	public static Vector<Node> cloneNodes(Vector<Node> nodes) {
		Vector<Node> ns = new Vector<Node>();
		for (Node n : nodes) {
			ns.add(n.clone());
		}		
		return ns;
	} 
}
