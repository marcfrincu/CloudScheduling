package mosaic.scheduler.simulator.util;

import java.util.UUID;
import java.util.Vector;

import mosaic.scheduler.platform.algorithms.IAlgorithm;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.simulator.scaler.Scaler;
import mosaic.scheduler.simulator.util.math.Probability;

import org.apache.log4j.Logger;



public class Runner {
	private static Logger logger = Logger.getLogger(Runner.class.getPackage().getName());
	
	private Vector<Runner.AlgorithmContainer> algorithms;

	private int totalNoWebServers = 0;

	public Runner() {
		this.algorithms = new Vector<Runner.AlgorithmContainer>();
	}
	
	/**
	 * Adds an algorithm to the Runner
	 * @param algorithm
	 * @param nodes
	 */
	public void addAlgorithm (IAlgorithm algorithm, Vector<Node> nodes) {
		this.algorithms.add(new AlgorithmContainer(algorithm, Runner.cloneNodes(nodes)));		
	}
		
	/**
	 * This method executes the algorithms added to the <i>Runner</i> during the time span specified in the system.properties file
	 */
	public void run() {
		long start, end;
		int[] noComponents;
		double noNewWebServers = 0;
		Scaler scaler = new Scaler();
		
		for (int t=0; t<SystemSettings.getSystemSettings().getTime_span(); t++) {
			Runner.logger.info("Starting time iterarion: " + (t+1));

			switch (SystemSettings.getSystemSettings().getWeb_server_generation_method()) {
				case POLYNOMIAL: 
					final double time = t/(double)SystemSettings.getSystemSettings().getTime_span() - 0.5;
					noNewWebServers = 3.1 - 8.5 * time + 24.7 * Math.pow(time, 2) + 130.8
					* Math.pow(time, 3) + 107.7 * Math.pow(time, 4) - 804.2
					* Math.pow(time, 5) - 2038.5 * Math.pow(time, 6) + 1856.8
					* Math.pow(time, 7) + 4618.6 * Math.pow(time, 8);
					break;
				case PARETO:
					try {
						noNewWebServers = Probability.generatePareto(Double.parseDouble(SystemSettings.getSystemSettings().getWeb_server_generation_method_argument_list()[0]),
																Double.parseDouble(SystemSettings.getSystemSettings().getWeb_server_generation_method_argument_list()[1])
																);
					}
					catch (NumberFormatException nfe) {
						System.exit(0);
					}
					break;
				case WEIBULL:
					try {
						noNewWebServers = Probability.generateWeibull(Double.parseDouble(SystemSettings.getSystemSettings().getWeb_server_generation_method_argument_list()[0]),
																Double.parseDouble(SystemSettings.getSystemSettings().getWeb_server_generation_method_argument_list()[1])
																);
					}
					catch (NumberFormatException nfe) {
						System.exit(0);
					}

					break;
				default: 						
					System.exit(0);
			}
						
			this.totalNoWebServers += noNewWebServers;
			Runner.logger.info("Generated now: " + (int)noNewWebServers + " / total: " + this.totalNoWebServers + " method: " + SystemSettings.getSystemSettings().getWeb_server_generation_method().toString());						
			
			for (AlgorithmContainer ac : this.algorithms) {
				Runner.logger.info("Scaling components for algorithm: " + ac.uuid);
				scaler.assignAlgorithm(ac.algorithm);
				noComponents = scaler.scaleComponents((int)noNewWebServers, ac.nodes);
				
				String s = "";
				for (int i=0; i<SystemSettings.getSystemSettings().getNo_component_types(); i++) {
					s += noComponents[i] + "\t";
				}
				Runner.logger.info("Number of required components: " + s);
				
				Runner.logger.info("Executing algorithm: " + ac.uuid);												
				start = System.currentTimeMillis();			
				ac.nodes = ac.algorithm.executeOnce(ac.nodes, noComponents, t);
				end = System.currentTimeMillis();				
				Runner.logger.info("Time [ms]: " + (end - start));
								
				if (ac.nodes.size() > 1) {
					if (ac.nodes.get(0 + (int)(Math.random() * ((ac.nodes.size() - 1 - 0) + 1))).history.get(t).getNoServiceTypes() < SystemSettings.getSystemSettings().getNo_component_types()) {
						ac.reliability++;
					}
				}
			}
		}		
	}
	

	public float[][] getResultsLoad() {
		float[][] r = new float[SystemSettings.getSystemSettings().getTime_span()][this.algorithms.size()];
		
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			int j = 0;
			for (AlgorithmContainer alg : this.algorithms) {
				r[i][j++] = alg.nodes.get(j).history.get(i) == null ? -1 : alg.nodes.get(j).history.get(i).getLoad();
			}
		}		
		
		return r;
	}

	public float[][] getResultsCost() {
		float[][] r = new float[SystemSettings.getSystemSettings().getTime_span()][this.algorithms.size()];
		
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			int j = 0;
			for (AlgorithmContainer alg : this.algorithms) {
				r[i][j++] = alg.nodes.get(j).history.get(i) == null ? 0 : alg.nodes.get(j).history.get(i).getCost();
			}
		}		
		
		return r;
	}

	public float[][] getResultsAvailability() {
		float[][] r = new float[SystemSettings.getSystemSettings().getTime_span()][this.algorithms.size()];
		
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			int j = 0;
			for (AlgorithmContainer alg : this.algorithms) {
				r[i][j++] = alg.nodes.get(j).history.get(i) == null ? -1 : alg.nodes.get(j).history.get(i).getNoServiceTypes();
			}
		}		
		
		return r;
	}
	
	public static Vector<Node> cloneNodes(Vector<Node> nodes) {
		Vector<Node> ns = new Vector<Node>();
		for (Node n : nodes) {
			ns.add(n.clone());
		}		
		return ns;
	} 
	
	public class AlgorithmContainer {
		public IAlgorithm algorithm;	
		public Vector<Node> nodes;
		public int reliability = 0;
		public UUID uuid;
		
		public AlgorithmContainer(IAlgorithm algorithm, Vector<Node> nodes) {
			this.algorithm = algorithm;
			this.nodes = nodes;
			this.uuid = UUID.randomUUID();
		}
		
		
	}
	
}
