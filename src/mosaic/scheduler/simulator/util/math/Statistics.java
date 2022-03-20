package mosaic.scheduler.simulator.util.math;

/**
 * This class contains methods for computing different statistical information:
 * standard deviation, mean, median
 * 
 * @author Marc Frincu
 * @since 2010
 */
public class Statistics {

	private Statistics() {

	}

	/**
	 * Computes the standard deviation of a given vector
	 * 
	 * @param values
	 *            the values
	 * @return the standard deviation or -1 in case the vector is null or empty
	 */
	public static double computeStandardDeviation(double[] values) {
		if (values.length == 0) {
			return -1;
		}

		double sum = 0;
		double mean = Statistics.computeMean(values);

		for (double v : values) {
			sum += Math.pow(v - mean, 2);
		}

		return Math.sqrt(sum / values.length);
	}

	/**
	 * Computes the mean (average) of a vector
	 * 
	 * @param values
	 *            the values
	 * @return the mean or -1 in case the vector is null or empty
	 */
	public static double computeMean(double[] values) {
		if (values.length == 0) {
			return -1;
		}
		double sum = 0;

		for (Double v : values) {
			sum += v.doubleValue();
		}

		return sum / values.length;
	}

	/**
	 * Computes the median of a vector
	 * 
	 * @param values
	 *            the values
	 * @return the median or -1 if the vector is null or is empty
	 */
	public static double computeMedian(double[] values) {
		if (values.length == 0) {
			return -1;
		}

		if (values.length % 2 == 0) {
			double a = values[values.length / 2];
			double b = values[values.length / 2 - 1];
			return (a + b) / 2;
		} else {
			return values[values.length / 2]; // not +1 since the index starts from 0
		}
	}

	/**
	 * Returns the index of the element that is closest to the median.
	 * 
	 * @param values
	 *            the values
	 * @return the index of the element or -1 if the vector is null or empty
	 */
	public static int findMedianPositionIndex(double[] values) {
		if (values.length == 0) {
			return -1;
		}

		if (values.length % 2 == 0) {
			return values.length / 2 - 1;
		} else {
			return values.length / 2;
		}
	}
}
