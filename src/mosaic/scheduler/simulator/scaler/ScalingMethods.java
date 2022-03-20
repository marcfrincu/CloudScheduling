package mosaic.scheduler.simulator.scaler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mosaic.scheduler.simulator.util.math.Statistics;


/**
 * Class containing several prediction methods used for simulating scaling
 * @author Marc Frincu
 * @since 2011
 * This class provides several methods for forecasting component scaling
 *
 */
public class ScalingMethods {

	/**
	 * Predicts the next value based an a simple linear regression model
	 * @param values the historical values
	 * @param windowSize the size of the window
	 * @param windowIndex the index where the window needs to start from
	 * @return the predicted value
	 * @throws Exception
	 */
	public static double predictLR(double[] values, int windowSize, int windowIndex) throws Exception {
		
		if (windowSize > values.length)
			throw new Exception("Window size bigger than array size");
		
		if (windowIndex >= values.length || windowIndex < 0)
			throw new Exception("Window index out of bounds");
		
		if (windowIndex+windowSize > values.length)
			throw new Exception("Window out of bounds");
		
		double x_ = 0, y_ = 0;
		
		for (int i=windowIndex;i<windowIndex+windowSize; i++) {
			y_ += values[i];
			x_ += i+1;
		}
		
		y_ /= windowSize;
		x_ /= windowSize;
		
		double sxx = 0, sxy = 0;
		
		for (int i=windowIndex;i<windowIndex+windowSize; i++) {
			sxx += Math.pow(i+1 - x_, 2);
			sxy += (i+1 - x_)*(values[i] - y_);
		}		
		
		double b = sxy / sxx;
		double a = y_ - b * x_;
		
		return a + b * (windowIndex+windowSize);		
	}
	
	/**
	 * Predicts the next value based on the average of the last 4 values
	 * @param values the historical values
	 * @param windowIndex the index where the window needs to start from
	 * @return the predicted value
	 * @throws Exception
	 */
	public static double predictLast4(double[] values, int windowIndex) throws Exception {
		
		if (windowIndex+4 >= values.length) 
			throw new Exception("Window index out of bounds");
		
		double avg = 0;
		
		for (int i=windowIndex; i<windowIndex+4; i++) {
			avg += values[i];
		}
		
		return avg/4;	
	}
	
	/**
	 * Predicts the next value based an an auto-regressive model of degree 1
	 * @param values the historical data
	 * @param windowIndex the index where the window needs to start from
	 * @param windowHistory
	 * @param windowAdaptation
	 * @return the predicted value
	 * @throws Exception
	 */
	public static double predictAR1(double[] values, int windowIndex, int windowHistory, int windowAdaptation) throws Exception {
		
		if (windowIndex - windowHistory < 0)
			throw new Exception("Window history out of bounds. Needs to be positive");			
	
		if (windowIndex + windowAdaptation >= values.length) 
			throw new Exception("Window adaptation out of bounds.");
		
		double avg = 0;		
		for (int i=windowIndex - windowHistory; i<windowIndex; i++) {
			avg += values[i];
		}		
		avg /= windowHistory;
		
		// compute the autocorrelation function of lag 1
		double stddev = ScalingMethods.stddev(values, windowIndex, windowHistory, avg);
		
		double p = 0;		
		for (int i=windowIndex - windowHistory; i<windowIndex; i++) {
			p += (values[i] - avg) * (values[i+1] - avg) / Math.pow(stddev, 2);
		}		
		p *= 1.0 / windowIndex;
		
		if (stddev == 0)
			return values[0];
		
		// compute the predicted values
		double predicted[] = new double[windowAdaptation+1];
		double sum = 0;
		int k=0;
		predicted[k] = values[windowIndex];
		for (int i=windowIndex; i<windowIndex+windowAdaptation; i++) {
			predicted[++k] = avg + p * (predicted[k-1] - avg);
			sum += predicted[k];
		}
		
		return sum / windowAdaptation;
		
	}
	
	/**
	 * Predicts the next value based on a pattern method
	 * @param values the historical data
	 * @param index the index where the window needs to start from
	 * @param windowSize
	 * @param distThreshold the maximum admissible threshold
	 * @return the predicted value
	 * @throws Exception
	 */
	public static double predictPattern(double[] values, int index, int windowSize, double distThreshold) throws Exception {
		if (windowSize > values.length)
			throw new Exception("Window size bigger than array size");
		
		if (values.length-windowSize < 0)
			throw new Exception("Window out of bounds");
		
		PatternPrediction pp = new ScalingMethods(). new PatternPrediction();
		
		double[] pattern = new double[windowSize];
		
		for (int i=0; i<windowSize; i++) {
			pattern[i] = values[index - windowSize + i];
		}
		
		pp.KMPApprox(values, pattern, index, windowSize);
		
		List<Map.Entry<Integer, Double>> matches = pp.getResults();
		
		int i = 0;
		double predicted = 0;
		for (Map.Entry<Integer, Double> match : matches) {
			if ((match.getValue() <= distThreshold) && (match.getKey() + windowSize < values.length)) {				
				predicted +=values[match.getKey() + windowSize - 1];
				i++;
			}
		}

		if (i==0)
			//return 197739;//large
			//return 350;//medium
			return 220;//medium2
			//return 9.6;//small
		return predicted / (i+0.0000000000001);
	}
	
	private static double stddev(double[] values, int windowIndex, int windowHistory, double avg) {
		double[] vals = new double[windowHistory];
		int j = 0;
		for (int i=windowIndex - windowHistory; i<windowIndex;i++) {
			vals[j++] = values[i];
		}
		
		return Statistics.computeStandardDeviation(vals);
	}
	
	/**
	 * This class makes pattern based prediction. The algorithm relies on the KMP
	 * algorithm and is extracted from the INRIA Report:
	 * "Forecasting for Cloud computing on-demand resources based on pattern matching"
	 * @author Marc Frincu
	 * @since 2012
	 */
	private class PatternPrediction {
		
		private static final double ACCEPT_INSTANCE_ERROR = 1.5;
		private static final double ACCEPT_CUMUL_ERROR = 1;
		
		private Map<Integer, Double> results = new Hashtable<Integer, Double>();
		
		private double distance(double patternElement, double patternScale, double dataElement, double dataScale) {
			return patternElement * dataScale - dataElement * patternScale;
		}
		
		private double cummulativeDistance(double[] p, double[] t, int dataOffset) {
			double patternScale = p[0];
			double dataScale = t[dataOffset];
			int length = p.length;
			double distance = 0;
			for (int i=0; i<length; i++) {
				distance += Math.abs(dataScale * p[i] - patternScale * t[i + dataOffset]);
			}
			return distance;
		}
		
		private int[] calculatePrefixApproximation(double[] p) {
			int m = p.length;
			int[] pi = new int[m];
			int k = -1;
			double scaleK = p[0];
			double scaleQ = p[1];
			double dist, maxDistance;
			
			for (int q=1; q<m; q++) {
				dist = this.distance(p[k+1], scaleK, p[q], scaleQ);
				maxDistance = PatternPrediction.ACCEPT_INSTANCE_ERROR * scaleQ * p[k+1];
				while (k > -1 && dist > maxDistance) {
					k = pi[k];
					dist = this.distance(p[k+1], scaleK, p[q], scaleQ);
					scaleQ = p[q - (k+1)];
				}
				if (dist <= PatternPrediction.ACCEPT_INSTANCE_ERROR * scaleQ * p[k+1]) {
					k++;
				}
				pi[q] = k;
			}
			return pi;
		}
		
		public void KMPApprox(double[] t, double[] p, int index, int windowSize) {
			int n = t.length;
			int m = p.length;
			int[] pi = this.calculatePrefixApproximation(p);
			int q = -1;
			double scaleP = p[0];
			double scaleT = t[0];
			double dist, maxDist, patternSum = 0;
			
			for (int i=0; i<m; i++) {
				patternSum += p[i];
			}
			
			for (int i=0; i<n; i++) {
				//if (i < index - windowSize || i >= index) {
					dist = this.distance(p[q+1], scaleP, t[i], scaleT);
					maxDist = PatternPrediction.ACCEPT_INSTANCE_ERROR * scaleT * p[q+1];
					while (q > -1 && dist > maxDist) {
						dist = this.distance(p[q+1], scaleP, t[i], scaleT);
						q = pi[q];
						scaleT = t[i - (q+1)];
						maxDist = PatternPrediction.ACCEPT_INSTANCE_ERROR * scaleT * p[q+1];
					}
					if (dist < maxDist) {
						q++;
					}
					if (q == m-1) {
						dist = this.cummulativeDistance(p, t, i - m + 1);
						maxDist = PatternPrediction.ACCEPT_CUMUL_ERROR * patternSum * scaleT;
						if (dist <= maxDist) {
							this.storeSolution(dist / scaleT, i - m + 1);
						}
						q = pi[q];
						scaleP = p[q+1];
						scaleT = t[i - (q + 1)];
					}
				//}
			}
		}
		
		private void storeSolution(double x, int index) {
			this.results.put(index, x);
		}
				
		public List<Map.Entry<Integer, Double>> getResults() {
			List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(this.results.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
				public int compare(Map.Entry<Integer, Double> e1, Map.Entry<Integer, Double> e2) {
					Double v1 = (Double)e1.getValue();
					Double v2 = (Double)e2.getValue();
					return v1.compareTo(v2);
				}
			}
			);
			return list;
		}
	}
}
