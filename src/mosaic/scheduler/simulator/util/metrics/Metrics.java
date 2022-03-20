package mosaic.scheduler.simulator.util.metrics;

import mosaic.scheduler.simulator.util.math.Statistics;

/**
 * Class holding measurement metrics for the efficiency of scheduling algorithms
 * @author Marc Frincu
 * @since 2012
 *
 */
public class Metrics {
	
	public static final double COST_PER_CPU_HOUR = 0.085;
	public static final double PAY_PER_CLICK = 0.01;
	public static final double COST_THROUGHPUT_RATE = 0.2;
	public static final int CONCURRENT_USERS_MAX = 125; // S 4266, M 125, M2 261, L 143 
	
	public static double UCSB(double data[], double predicted[], int trim) {
				
		double delta = 0.000000001;
		double alpha = 2;
		double gamma = 1;
		double beta = 50;
		double A, C, Alog;
		//double cost = 0;
		double total = 0, serviced = 0, cpus = 0;
		for (int i=trim; i<data.length - trim; i++) {
			//A = (double) Math.min(predicted[i], data[i]) / data[i];
			//C = (int)predicted[i] / Metrics.CONCURRENT_USERS_MAX + (((int)predicted[i] % Metrics.CONCURRENT_USERS_MAX != 0) ? 1 : 0) * Metrics.COST_PER_CPU_HOUR;		
			//Alog = Math.log(1 + delta - A);		
			//cost += Math.pow(Alog, alpha) / C - gamma * C / Alog + beta;
			if (predicted[i] < 0)
				continue;
			total += data[i];
			serviced += Math.min(predicted[i], data[i]);
			cpus += (int)predicted[i] / Metrics.CONCURRENT_USERS_MAX + (((int)predicted[i] % Metrics.CONCURRENT_USERS_MAX != 0) ? 1 : 0);
		}		
		
		A = serviced / total;
		C = cpus  /*/ (data.length - 2 * trim)*/ * Metrics.COST_PER_CPU_HOUR;
		Alog = - Math.log(1 + delta - A);	
		return Math.pow(Alog, alpha) / C - gamma * C / Alog + beta;
	}
	
	public static ScaleInfo computeInfo(double data[], double predicted[], int trim) {
		double diff;
		double costUP = 0, costOP = 0, gain = 0;
		int reqNodes, match = 0, over = 0, under = 0;
		double[] forStats = new double[predicted.length - 2 * trim];
		int j = 0;
		for (int i=trim; i<data.length - trim; i++) {
			diff = data[i] - predicted[i];			
			forStats[j++] = predicted[i];
			// compute $ loss due to over provisioning
			if (diff < 0) {
				reqNodes = (int)Math.abs(diff) / Metrics.CONCURRENT_USERS_MAX + (((int)Math.abs(diff) % Metrics.CONCURRENT_USERS_MAX != 0) ? 1 : 0);
				costOP += reqNodes * Metrics.COST_PER_CPU_HOUR;
				over++;
			}
			// compute $ loss due to under provisioning
			if (diff > 0) {
				costUP += Metrics.COST_THROUGHPUT_RATE / 100 * diff * Metrics.PAY_PER_CLICK;
				under++; 
			}			
			if (diff == 0) {
				match++;
			}
			// compute $ gain due to provisioning
			gain += Metrics.COST_THROUGHPUT_RATE / 100 * Math.min(data[i], predicted[i]) * Metrics.PAY_PER_CLICK; 			
		}
		
		return new Metrics(). new ScaleInfo(costUP, 
											costOP, 
											gain, 
											match * 100. / forStats.length, 
											over * 100. / forStats.length, 
											under * 100. / forStats.length, 
											Statistics.computeMean(forStats), 
											Statistics.computeStandardDeviation(forStats),
											Statistics.computeMedian(forStats)
										);
	}
	
	public class ScaleInfo {
		private double lossUnderProvisioning;
		private double lossOverProvisioning;
		private double gain;
		private double match, overPredicted, underPredicted;
		private double average, stdDev, median;
		
		public ScaleInfo (double lossUnderProvisioning, double lossOverProvisioning, double gain, double match, double overPredicted, double underPredicted, double average, double stdDev, double median) {
			this.lossOverProvisioning = lossOverProvisioning;
			this.lossUnderProvisioning = lossUnderProvisioning;
			this.gain = gain;
			this.match = match;
			this.overPredicted = overPredicted;
			this.underPredicted = underPredicted;
			this.average = average;
			this.stdDev = stdDev;
			this.median = median;
		}

		public double getLossUnderProvisioning() {
			return lossUnderProvisioning;
		}

		public double getLossOverProvisioning() {
			return lossOverProvisioning;
		}
		
		public double getGain() {
			return gain;
		}

		public double getMatch() {
			return match;
		}

		public double getOverPredicted() {
			return overPredicted;
		}

		public double getUnderPredicted() {
			return underPredicted;
		}

		public double getAverage() {
			return average;
		}

		public double getStdDev() {
			return stdDev;
		}
		
		public double getMedian() {
			return median;
		}
	}
}
