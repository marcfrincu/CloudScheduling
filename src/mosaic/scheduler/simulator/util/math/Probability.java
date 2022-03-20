package mosaic.scheduler.simulator.util.math;


import java.util.Random;

/**
 * This class contains methods for various probability distributions: Pareto,
 * Poisson, Weibull, Erlang, Lognormal, Normal, Uniform, Gamma, Beta, Hyperexponential,
 * Hypererlang and Hypergamma  (use http://www.wessa.net/ to check the correctness of the algorithms)
 * 
 * @author Marc Frincu
 * @since 2010, last update Sep 30th 2011
 */
public final class Probability {

	private Probability() {
	}
	
	/**
	 * Generates a Pareto number.
	 * @param array the list of arguments 
	 * @return a Pareto generated number
	 * @see generatePareto(double alpha, double k)
	 */
	public static double generateParetoFromArray(double[] array)
	{
		if (array.length != 2)
		{
			throw new IllegalArgumentException("Wrong number of arguments"); 
		}
		return Probability.generatePareto(array[0], array[1]);
	}

	/**
	 * Generates a Poisson number
	 * @param array the list of arguments
	 * @return a Poisson generated number
	 * @see generatePoisson(double lambda)
	 */
	public static double generatePoissonFromArray(double[] array)
	{
		if (array.length != 1)
		{
			throw new IllegalArgumentException("Wrong number of arguments"); 
		}
		return Probability.generatePoisson(array[0]);
	}
	
	/**
	 * Generates a Weibull number
	 * @param array the list of arguments
	 * @return a Weibull generated number
	 * @see generateWeibull(double alpha, double beta)
	 */
	public static double generateWeibullFromArray(double[] array)
	{
		if (array.length != 2)
		{
			throw new IllegalArgumentException("Wrong number of arguments"); 
		}
		return Probability.generateWeibull(array[0], array[1]);
	}	
	
	/**
	 * Generates an Erlang number
	 * @param array the list of arguments
	 * @return an Erlang generated number
	 * @see generateErlang(double theta)
	 */
	public static double generateErlangFromArray(double[] array)
	{
		if (array.length != 1)
		{
			throw new IllegalArgumentException("Wrong number of arguments"); 
		}
		return Probability.generateErlang(array[0]);
	}

	/**
	 * Generates a Lognormal number
	 * @param array the list of arguments
	 * @return a Lognormal generated number
	 * @see generateErlang(double theta)
	 */
	public static double generateLognormalFromArray(double[] array)
	{
		if (array.length != 3)
		{
			throw new IllegalArgumentException("Wrong number of arguments"); 
		}
		return Probability.generateLognormal(array[0], array[1], array[2]);
	}

	/**
	 * Generates a Normal number
	 * @param array the list of arguments
	 * @return a Normal generated number
	 * @see BoxMuller(double min, double max, double mean, double stdDev)
	 */
	public static double generateNormalFromArray(double[] array)
	{
		if (array.length != 4)
		{
			throw new IllegalArgumentException("Wrong number of arguments"); 
		}
		return Probability.BoxMuller(array[0], array[1], array[2], array[3]);		
	}

	/**
	 * Generates a HyperExponential number
	 * @param array the list of arguments
	 * @return a HyperExponential generated number
	 * @see generateHyperExponential(double lambda, double cov)
	 */
	public static double generateHyperExponentialFromArray(double[] array)
	{
		if (array.length != 2)
		{
			throw new IllegalArgumentException("Wrong number of arguments"); 
		}
		return Probability.generateHyperExponential(array[0], array[1]);
	}

	/**
	 * Generate a Pareto distribution random number
	 * 
	 * @param alpha
	 *            shape parameter that defines the tail of the distribution.
	 *            Must be a positive number
	 * @param k
	 *            specifies the minimal value possible
	 * @return a Pareto random number or -1 if the parameters' values are
	 *         incorrect
	 */
	public static double generatePareto(double alpha, double k) {
		if (alpha <= 0)
			return -1;
		
		return k / Math.pow(Math.random(), 1 / alpha);
	}

	/**
	 * Generate a Poisson distributed random number
	 * 
	 * @param lambda
	 *            represents the number of occurrences of an event to be
	 *            expected in the given interval. Must be a positive number
	 * @return a Poisson random number or -1 if the parameter value is incorrect
	 */
	public static double generatePoisson(double lambda) {
		if (lambda < 0)
			return -1;

		if (lambda < 30) {
			final double L = Math.exp(-lambda);
			System.out.println(L);
			int k = 0;
			double p = 1;
			do {
				k++;
				p = p * Math.random();
			} while (p > L);
			return k - 1;
		}
		else {
			// accept-reject method based on a normal approximation
			final double c = 0.767 - 3.36 / lambda;
			final double beta = Math.PI / Math.sqrt(3 * lambda);
			final double alpha = beta * lambda;
			final double k = Math.log(c) - lambda - Math.log(beta);
			
			double u, v, x, y, n, lhs, rhs;
			while (true) {
				u = Math.random();
				x = (alpha  - Math.log((1 - u) / u)) / beta;
				n = Math.floor(x + 0.5);
				if (n < 0)
					continue;
				v = Math.random();
				y = alpha - beta * x;
				lhs = y + Math.log(v / Math.pow(1 + Math.exp(y), 2));
				rhs = k + n * Math.log(lambda) - Mathematical.LogFactorial((int)Math.round(n));
				if (lhs <= rhs)
					return n;
			}
		}
	}

	/**
	 * Generate a Weibull distributed random number
	 * 
	 * @param alpha
	 *            the shape parameter. Must be a positive value
	 * @param beta
	 *            the scale parameter. Must be a positive value
	 * @return a Weibull random number or -1 if the parameters' values are
	 *         incorrect
	 */
	public static double generateWeibull(double alpha, double beta) {
		if (alpha <= 0 || beta <= 0)
			return -1;
		
		return beta * Math.pow(-Math.log(Math.random()), 1 / alpha);
	}

	/**
	 * generates an Erlang distributed random number
	 * 
	 * @param theta
	 *            a scale parameter that determines how quickly the probability
	 *            decays. Must be a non-negative number
	 * @return an Erlang random number or -1 if the parameter value is incorrect
	 */
	public static double generateErlang(double theta) {
		if (theta < 0)
			return -1;

		return theta * Math.log(Math.random());
	}

	/**
	 * Generates a Lognormal distributed random number
	 * 
	 * @param n
	 *            a normal variate
	 * @param niu
	 *            the mean in log space
	 * @param sigma
	 *            the standard deviation in log space
	 * @return a Lognormal random number
	 */
	public static double generateLognormal(double n, double niu, double sigma) {
		return Math.exp(niu + sigma * n);
	}


	/** Generates a Beta distributed random number
	 *
	 * @param alpha
	 *		the shape parameter
	 * @param beta
	 *		the shape parameter
	*/
	public static double generateBeta(double alpha, double beta) {
		double x = Probability.generateGamma(alpha, 1);
		double y = Probability.generateGamma(beta, 1);

		return  x * (x + y);
	}

	/**
	 * Generates a Gamma distributed random number based on the algorithm described in "A Convenient Way of Generating Gamma Random Variables Using Generalized Exponential Distribution" by Debasis Kundu & Rameshwar D. Gupta
	 * and on an "An Easily Programmed Algorithm for Generating Gamma Random Variables" by A. C. Atkinson
	 *
	 * @param alpha
	 *		the shape of the distribution
	 * @param lambda
	 *		the scale of the distribution - this argument is considered to be equal to 1 and ignored
	*/
	public static double generateGamma(double alpha, double lambda) {
		double u, v, x;
		
		if (alpha < 1) {			
			double d = 1.0334 - 0.0766 * Math.pow(10, -2.2942 * alpha);
			double a = Math.pow(2, alpha) * Math.pow(1 - Math.exp(-1 * d / 2), alpha);
			double b = alpha * Math.pow(d, alpha - 1) * Math.exp(-1 * d);
			double c = a + b;
			boolean repeat;
	
			do {
				repeat = false;
				u = Math.random();
				if (u <= a / (a + b)) 
					x = -2 * Math.log(1 - Math.pow(c * u, 1 / alpha) / 2);
				else
					x = -1 * Math.log(c * (1 - u) / (alpha * Math.pow(d, alpha - 1)));
	
				v = Math.random();
				if (x <= d && (v <= (Math.pow(x, alpha - 1) * Math.exp(-x / 2.0)) / (Math.pow(2, alpha - 1) * Math.pow(1 - Math.exp(-1 * x / 2), alpha -1)))) 
					repeat = true;
				if (x > d && v <= Math.pow(d / x, 1 - alpha))
					repeat = true;
			} while (repeat);
			return x;
		}
		else {
			double t = alpha -1;
			double niu = (Math.sqrt(1 + 4 * t) - 1) / 2 * t;			
			double k_1 = t * Math.pow(1 - niu, t) + 1 / niu * Math.exp(-1 * niu * t);
			double r = 1 / k_1 * t * Math.pow(1 - niu, t);

			boolean cont = false;
			do {
				cont = false;
				u = Math.random();
				v = Probability.generateExponential(1);
				if (u > r) {
					x = -1 * 1 / niu * Math.log(niu * (1 - u)) * k_1;
					if ((t * Math.log(t / ((1 - niu) * x)) + (1 - niu) * x -t) <= v)
						return x;
					else
						cont = true;
				}
				else {
					x = u * k_1 * Math.pow(1 - niu, -t);
					if ((t * Math.log(2) - x) > v)
						cont = true;
					if ((t * Math.log(t) - t - t * Math.log(x) + x) <= v)
						return x;
				}
			} while (cont);
		}
		return Double.NaN;
			
	}

	/**
	 * Generate an exponentially distributed variable with a given rate
	 * @param rate a positive value
	 * @return
	 */
	public static double generateExponential(double rate) {
		if (rate <= 0)
			return Double.NaN;
		
		return  - Math.log(Math.random()) / rate;
	}
	
	/**
	 * Apply the polar form of the BoxMuller Transform to map the two uniform
	 * random numbers to a pair of numbers from a normal distribution. It is
	 * good for speed because it does not call math functions many times.
	 * Another way would be to simply: y1 = sqrt( - 2 * log(x1) ) * cos( 2 * pi
	 * * x2 )
	 * 
	 * @param min
	 *            the minimum number that should be generated
	 * @param max
	 *            the maximum number that should be generated
	 * @param mean
	 *            the mean number
	 * @param stdDev
	 *            the standard deviation
	 * @return
	 * @return the random generated number
	 */
	public static double BoxMuller(double min, double max, double mean,
			double stdDev) {
		Random rand = new Random();
		double x1, x2, w, y;
		do {
			do {
				x1 = 2.0 * rand.nextDouble() - 1.0;
				x2 = 2.0 * rand.nextDouble() - 1.0;
				w = x1 * x1 + x2 * x2;
			} while (w >= 1.0);

			w = Math.sqrt((-2.0 * Math.log(w)) / w);
			y = x1 * w;

			/*
			 * Multiply the BoxMuller value by the standard deviation and add
			 * the mean
			 */
			y = y * stdDev + mean;
		} while (!(min <= y && y <= max));
		return y;
	}

	/**
	 * Generate a uniform random variable based on the following formula: x_n =
	 * 7^5 * x_(n-1) mod (2^31 - 1). From: R. Jain,
	 * "The Art of Computer Systems Performance Analysis," John Wiley & Sons,
	 * 1991. (Page 443, Figure 26.2)
	 * 
	 * @param seed
	 *            the initial seed
	 * @return a random uniform number
	 */
	public static double generateUniform(int seed) {
		// Multiplier
		final long a = 16807;
		// Modulus
		final long m = 2147483647;
		// m div a
		final long q = 127773;
		// m mod a
		final long r = 2836;
		// Random integer value
		long x = (long) Math.random();
		// x divided by q
		long x_div_q;
		// x modulo q
		long x_mod_q;
		// New x value
		long x_new;

		// Set the seed if argument is non-zero and then return zero
		if (seed > 0) {
			x = seed;
			// return 0.0;
		}

		// RNG using integer arithmetic
		x_div_q = x / q;
		x_mod_q = x % q;
		x_new = (a * x_mod_q) - (r * x_div_q);
		if (x_new > 0)
			x = x_new;
		else
			x = x_new + m;

		// Return a random value between 0.0 and 1.0
		return (double) x / m;
	}

	/**
	 * Generates a HyperExponential distributed random number using the Morse
	 * method. From: Simulating Computer Systems Systems, Techniques and Tools
	 * by M. H. MacDougall (1987)
	 * 
	 * @param lambda
	 *            the arrival rate in customers per second
	 * @param cov
	 *            the desired coefficient of variation. Should be greater than 1
	 * @return a HyperExponential random number or -1 if the parameters' values
	 *         are incorrect
	 */
	public static double generateHyperExponential(double lambda, double cov) {

		if (cov <= 1)
			return -1;

		final double z1 = Math.random();
		final double z2 = Math.random();
		final double x = 1.0 / lambda;
		double temp = cov * cov;
		final double p = 0.5 * (1.0 - Math.sqrt((temp - 1.0) / (temp + 1.0)));

		if (z1 > p)
			temp = x / (1.0 - p);
		else
			temp = x / p;

		return -0.5 * temp * Math.log(z2);
	}

	public static double generateHyperGamma() {
		// TODO
		throw new UnsupportedOperationException();
	}

	public static double generateHyperErlang() {
		// TODO
		throw new UnsupportedOperationException();
	}
}