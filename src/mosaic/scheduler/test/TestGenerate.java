package mosaic.scheduler.test;

import mosaic.scheduler.simulator.util.math.Probability;

public class TestGenerate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double n;
		for (int i=0; i<170; i++) {
			//while ((n = Probability.generateLognormal(Probability.BoxMuller(0, 1, 0.5, 0.5), 12.18, 0.18)) <= 0); //large
			//while ((n = Probability.generateLognormal(Probability.BoxMuller(0, 1, 0.5, 0.5), 5.54, 0.54)) <= 0); //medium2
			//n = Probability.generateWeibull(5.94,212056.53);//large
			//n = Probability.generateWeibull(5.12,230.59); //medium
			//while (Double.isNaN(n = Probability.generateGamma(2.14, 0.11))); // small
			n = Probability.generatePoisson(197468.93); // large
			//n = Probability.generatePoisson(211.48); // medium
			
				System.out.println(n);
				
		}
	}

}
