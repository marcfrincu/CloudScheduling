package mosaic.scheduler.test;

import mosaic.scheduler.simulator.scaler.ScalingMethods;
import mosaic.scheduler.simulator.util.draw.Plot;
import mosaic.scheduler.simulator.util.math.Mathematical;
import mosaic.scheduler.simulator.util.metrics.Metrics;
import mosaic.scheduler.simulator.util.misc.PreprocessData;

public class TestScaler {

	static enum COMPARE_METHOD {LSM, UCSB};

	
	public static void main(String[] args) throws Exception {
		
		/*
		 * TESTBED
		 * dim medium traffic page - 102kB
		 * dim medium2 traffic page - 49kB
		 * dim small traffic page - 3kB
		 * dim large traffic page -89kB
		 * Amazon EC2 small US East Virginia spot instance Linux - $0.085
		 * 		1.7GB RAM, 1 CPU Core 160GB HDD, 1Gb/s 
		 * Net: $0.12/GB
		 * 
		 * => concurrent users:
		 * 			Theoretical speed at 1Gb	at 100Mb
		 * medium:	1,285.01960784314			125.490196078431
		 * medium2:	2,674.9387755102			261.224489795918
		 * small:	43,690.6666666667			4,266.66666666667
		 * large:	1,472.7191011236			143.820224719101
		 * 
		 * PPM: $0.01 - $25
		 * CTR: 0.2-0.3% , 2% very good rate
		 * 
		 */
		
		
		Plot plot = new Plot();
		String[] types = new String[]{"poisson"};
		
		String method=COMPARE_METHOD.UCSB.toString();
		
		for (String type : types) {
			
			double data[] = PreprocessData.readHistoryFile("results/synthetic/" + type + ".txt");
			double prediction[] = new double[data.length];
	
			double ucsbMax = Double.MIN_VALUE, predictionFinal[] = new double[data.length], min = Double.MAX_VALUE, max = Double.MIN_VALUE;
			double lsmMin = Double.MAX_VALUE;
			
			Metrics.ScaleInfo li = null, li2;
			int wS=0, th=0;
			boolean exists = false;
					
				for (int windowSize = 2; windowSize < data.length/4; windowSize++) {			
					prediction = new double[data.length];
					prediction = TestScaler.testLR(data, prediction, type, plot, windowSize);
					
					if (method == COMPARE_METHOD.UCSB.toString()) {
						double ucsb = Metrics.UCSB(data, prediction, windowSize);
						li2 = Metrics.computeInfo(data, prediction, windowSize);
						
						if ( ucsb >= ucsbMax)
							if (li2.getLossOverProvisioning() <= ((li == null ) ? Double.MAX_VALUE : li.getLossOverProvisioning())) {
							//if (li2.getGain() > li2.getLossOverProvisioning() +  li2.getLossUnderProvisioning() && li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning() > max) {
								ucsbMax = ucsb;	
								//max = li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning();
								max = li2.getLossOverProvisioning();
								li = Metrics.computeInfo(data, prediction, windowSize);
								wS = windowSize;
								predictionFinal = prediction;
								exists = true;
							}
					}
					if (method == COMPARE_METHOD.LSM.toString()) {
						double lsm = Mathematical.LSM(data, prediction);
						if (lsm < lsmMin) {
							lsmMin = lsm;
							predictionFinal = prediction;
							exists = true;
						}
							
					}
				}				
				/*if (!exists)
					for (int windowSize = 2; windowSize < data.length/4; windowSize++) {		
						prediction = new double[data.length];
						prediction = TestScaler.testLR(data, prediction, type, plot, windowSize);
						
						double ucsb = Metrics.UCSB(data, prediction, windowSize);
						li2 = Metrics.computeInfo(data, prediction, windowSize);
						if ( ucsb > ucsbMax) 
							if (li2.getGain() < li2.getLossOverProvisioning() +  li2.getLossUnderProvisioning() && Math.abs(li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning()) < min) {
								ucsbMax = ucsb;	
								min = Math.abs(li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning());
								li = Metrics.computeInfo(data, prediction, windowSize);
								wS = windowSize;
								predictionFinal = prediction;
							}
					}
				*/
				//PreprocessData.writePredictedFile("results/procesate-out/" + type + "-" + method + "-LR.txt", data, predictionFinal);
				plot.plotScalingPrediction(data, predictionFinal, type, method, "LR", true);
		
				li = Metrics.computeInfo(data, predictionFinal, wS);
				
				System.out.println(method + "\t " + type + "\t" + ((method == COMPARE_METHOD.UCSB.toString()) ? ucsbMax : lsmMin) + "\t" + 
						wS + "\t" + 
						th + "\t" + 
						li.getLossOverProvisioning() + "\t" + 
						li.getLossUnderProvisioning() + "\t" + 
						li.getGain() + "\t" + 
						(li.getGain() - li.getLossOverProvisioning() - li.getLossUnderProvisioning()) + "\t" + 
						li.getUnderPredicted() + "\t" + 
						li.getMatch() + "\t" + 
						li.getOverPredicted() + "\t" + 
						li.getAverage() + "\t" + 
						li.getStdDev() + "\t" + 
						li.getMedian());
				li = null;
				exists = false;

				prediction = TestScaler.testL4(data, prediction, type, plot);
				li = Metrics.computeInfo(data, prediction, 4);
				predictionFinal = prediction;
				
				
			//	PreprocessData.writePredictedFile("results/procesate-out/" + type + "-" + method + "-AR1.txt", data, predictionFinal);
				plot.plotScalingPrediction(data, predictionFinal, type, method, "L4", true);
				
				ucsbMax = Double.MIN_VALUE;
				lsmMin = Double.MAX_VALUE;
			
				for (int windowHistory = 3; windowHistory < data.length/4; windowHistory++) 
					for (int windowAdaptation = 1; windowAdaptation < 3; windowAdaptation++) {
						//data = PreprocessData.readHistoryFile(type + ".txt");
						prediction = new double[data.length];
						prediction = TestScaler.testAR1(data, prediction, type, plot, windowHistory, windowAdaptation);
						if (method == COMPARE_METHOD.UCSB.toString()) {
							double ucsb = Metrics.UCSB(data, prediction, windowHistory);
							li2 = Metrics.computeInfo(data, prediction, windowHistory);
							if ( ucsb >= ucsbMax)
								if (li2.getLossOverProvisioning() <= ((li == null ) ? Double.MAX_VALUE : li.getLossOverProvisioning())) { 
								//if (li2.getGain() > li2.getLossOverProvisioning() +  li2.getLossUnderProvisioning() && li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning() > max) {					ucsbMax = ucsb;
									ucsbMax = ucsb;	
									//max = li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning();
									max = li2.getLossOverProvisioning();
									li = Metrics.computeInfo(data, prediction, windowHistory);
									wS = windowHistory;
									th = windowAdaptation;
									predictionFinal = prediction;
									exists = true;
								}
						}
						if (method == COMPARE_METHOD.LSM.toString()) {
							double lsm = Mathematical.LSM(data, prediction);
							if (lsm < lsmMin) {
								lsmMin = lsm;
								predictionFinal = prediction;
								exists = true;
							}
								
						}
	
					}	
				/*if (!exists)
					for (int windowHistory = 3; windowHistory < data.length/4; windowHistory++) 
						for (int windowAdaptation = 1; windowAdaptation < 3; windowAdaptation++) {
							//data = PreprocessData.readHistoryFile(type + ".txt");
							prediction = new double[data.length];
							prediction = TestScaler.testAR1(data, prediction, type, plot, windowHistory, windowAdaptation);
							double ucsb = Metrics.UCSB(data, prediction, windowHistory);
							li2 = Metrics.computeInfo(data, prediction, windowHistory);
							if ( ucsb > ucsbMax) 
								if (li2.getGain() < li2.getLossOverProvisioning() +  li2.getLossUnderProvisioning() && Math.abs(li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning()) < min) {
									ucsbMax = ucsb;	
									min = Math.abs(li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning());
									li = Metrics.computeInfo(data, prediction, windowHistory);
									wS = windowHistory;
									th = windowAdaptation;
									predictionFinal = prediction;
								}
						}	
					*/
				//PreprocessData.writePredictedFile("results/procesate-out/" + type + "-" + method + "-AR1.txt", data, predictionFinal);
				plot.plotScalingPrediction(data, predictionFinal, type, method, "AR1", true);

				li = Metrics.computeInfo(data, predictionFinal, wS);
				
				System.out.println(method + "\t " + type + "\t" + ((method == COMPARE_METHOD.UCSB.toString()) ? ucsbMax : lsmMin) + "\t" + 
						wS + "\t" + 
						th + "\t" + 
						li.getLossOverProvisioning() + "\t" + 
						li.getLossUnderProvisioning() + "\t" + 
						li.getGain() + "\t" + 
						(li.getGain() - li.getLossOverProvisioning() - li.getLossUnderProvisioning()) + "\t" + 
						li.getUnderPredicted() + "\t" + 
						li.getMatch() + "\t" + 
						li.getOverPredicted() + "\t" + 
						li.getAverage() + "\t" + 
						li.getStdDev() + "\t" + 
						li.getMedian());
				li = null;
				exists = false;

				ucsbMax = Double.MIN_VALUE;
				lsmMin = Double.MAX_VALUE;
				
				for (int windowSize = 2; windowSize < data.length/4; windowSize++) 
					for (int threshold = 1; threshold < 10; threshold++) {
						//data = PreprocessData.readHistoryFile(type + ".txt");
						prediction = new double[data.length];
						prediction = TestScaler.testPB(data, prediction, type, plot, windowSize, threshold);
						if (method == COMPARE_METHOD.UCSB.toString()) {
							double ucsb = Metrics.UCSB(data, prediction, windowSize);
							li2 = Metrics.computeInfo(data, prediction, windowSize);
							if ( ucsb >= ucsbMax)
								if (li2.getLossOverProvisioning() <= ((li == null ) ? Double.MAX_VALUE : li.getLossOverProvisioning())) { 
								//if (li2.getGain() > li2.getLossOverProvisioning() +  li2.getLossUnderProvisioning() && li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning() > max) {
									ucsbMax = ucsb;	
									//max = li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning();
									max = li2.getLossOverProvisioning();
									li = Metrics.computeInfo(data, prediction, windowSize);
									wS = windowSize;
									th = threshold;
									predictionFinal = prediction;
									exists = true;
							}
		//					System.out.println(windowSize + " " + threshold);
						}					
						if (method == COMPARE_METHOD.LSM.toString()) {
							double lsm = Mathematical.LSM(data, prediction);
							if (lsm < lsmMin) {
								lsmMin = lsm;
								predictionFinal = prediction;
								exists = true;
							}							
						}
					}
				/*
				if (!exists) {
					for (int windowSize = 2; windowSize < data.length/4; windowSize++) 
						for (int threshold = 1; threshold < 10; threshold++) {
							//data = PreprocessData.readHistoryFile(type + ".txt");
							prediction = new double[data.length];
							prediction = TestScaler.testPB(data, prediction, type, plot, windowSize, threshold);
							double ucsb = Metrics.UCSB(data, prediction, windowSize);
							li2 = Metrics.computeInfo(data, prediction, windowSize);
							if ( ucsb > ucsbMax) 
								if (li2.getGain() < li2.getLossOverProvisioning() +  li2.getLossUnderProvisioning() && Math.abs(li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning()) < min) {
									ucsbMax = ucsb;	
									min = Math.abs(li2.getGain() - li2.getLossOverProvisioning() - li2.getLossUnderProvisioning());
									li = Metrics.computeInfo(data, prediction, windowSize);
									wS = windowSize;
									th = threshold;
									predictionFinal = prediction;
								}
			//				System.out.println(windowSize + " " + threshold);
						}			
				}
					*/			
				//PreprocessData.writePredictedFile("results/procesate-out/" + type + "-" + method + "-PB.txt", data, predictionFinal);
				plot.plotScalingPrediction(data, predictionFinal, type, method, "PB", true);
				
				li = Metrics.computeInfo(data, predictionFinal, wS);
				
				System.out.println(method + "\t " + type + "\t" + ((method == COMPARE_METHOD.UCSB.toString()) ? ucsbMax : lsmMin) + "\t" + 
						wS + "\t" + 
						th + "\t" + 
						li.getLossOverProvisioning() + "\t" + 
						li.getLossUnderProvisioning() + "\t" + 
						li.getGain() + "\t" + 
						(li.getGain() - li.getLossOverProvisioning() - li.getLossUnderProvisioning()) + "\t" + 
						li.getUnderPredicted() + "\t" + 
						li.getMatch() + "\t" + 
						li.getOverPredicted() + "\t" + 
						li.getAverage() + "\t" + 
						li.getStdDev() + "\t" + 
						li.getMedian());

		}
		
				
		//prediction = PreprocessData.readHistoryFile(type + "-pred-NN.txt");
		//predictionFinal = TestScaler.testNN(data, prediction, type, plot);
				
		//double ucsb = Metrics.UCSB(data, prediction, 10);
		//li = Metrics.computeInfo(data, prediction, 10);
		
		/*System.out.println(ucsbMax + "\t" + 
							wS + "\t" + 
							th + "\t" + 
							li.getLossOverProvisioning() + "\t" + 
							li.getLossUnderProvisioning() + "\t" + 
							li.getGain() + "\t" + 
							(li.getGain() - li.getLossOverProvisioning() - li.getLossUnderProvisioning()) + "\t" + 
							li.getUnderPredicted() + "\t" + 
							li.getMatch() + "\t" + 
							li.getOverPredicted() + "\t" + 
							li.getAverage() + "\t" + 
							li.getStdDev() + "\t" + 
							li.getMedian());
		*/					
		//System.out.println(ucsbMax + "\t" + "\t" + li.getLossOverProvisioning() + "\t" + li.getLossUnderProvisioning() + "\t" + li.getGain() + "\t" + (li.getGain() - li.getLossOverProvisioning()));
		//plot.plot(data, predictionFinal, type, "PB", false);
		//PreprocessData.writePredictedFile("results/procesate-out/" + type + "-" + method + "-LR.txt", data, predictionFinal);

	/*	double diff[] = new double[data.length];
		double sign[] = new double[data.length];		
		for (int i=0;i<data.length;i++) {
			diff[i] = prediction[i] - data[i];
			
			sign[i] = 1;
			if (diff[i] < 0) {
				sign[i] = -1;
				diff[i] *= sign[i];
			}		
		}
		PreprocessData.writePredictedFile(type + "-PB-diff.txt", sign, diff);
		*/
	}	
	
	public static double[] testLR(double data[], double prediction[], String type, Plot plot, int windowSize) throws Exception {
			
		//int windowSize = 10;
		
		double[] data2 = new double[data.length];
		for (int i=0;i<data.length-windowSize;i++) {
			data2[i] = data[i];
		}
		for (int i=0;i<data.length-windowSize;i++) {
			prediction[i+windowSize] = ScalingMethods.predictLR(data2, windowSize, i);
			//data2[i+windowSize] = prediction[i+windowSize];
		}				
		//PreprocessData.writePredictedFile(type + "-LR.txt", data, prediction);
		return prediction;
	}
	
	public static double[] testL4(double data[], double prediction[], String type, Plot plot) throws Exception {
		for (int i=0;i<data.length-4;i++) {
			prediction[i+4] = ScalingMethods.predictLast4(data, i);
		}
		//PreprocessData.writePredictedFile(type + "-L4.txt", data, prediction);
		return prediction;
	}
	
	public static double[] testAR1(double data[], double prediction[], String type, Plot plot, int windowHistory, int windowAdaptation) throws Exception {
		//int windowHistory = 3, windowAdaptation=3;
		for (int i=windowHistory;i<data.length-windowAdaptation;i++) {
			prediction[i] = ScalingMethods.predictAR1(data, i, windowHistory, windowAdaptation);
		}
		//PreprocessData.writePredictedFile(type + "-AR1.txt", data, prediction);
		return prediction;
	}
	
	public static double[] testPB(double data[], double prediction[], String type, Plot plot, int windowSize, int threshold) throws Exception {
		//int windowSize = 4;
		//int threshold = 40;
		for (int i=windowSize; i<data.length - windowSize; i++) {
			prediction[i] = ScalingMethods.predictPattern(data, i, windowSize, threshold);
		}
		//PreprocessData.writePredictedFile(type + "-PB.txt", data, prediction);
		//plot.plot(data, prediction, type, "PB", false);
		return prediction;
	}
	
	public static double[] testNN(double data[], double prediction[], String type, Plot plot) throws Exception {
		PreprocessData.writePredictedFile(type + "-NN.txt", data, prediction);
		return prediction;
	}
}