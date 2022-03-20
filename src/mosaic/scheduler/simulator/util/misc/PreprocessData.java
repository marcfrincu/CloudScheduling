package mosaic.scheduler.simulator.util.misc;

import java.util.ArrayList;


public class PreprocessData {

	public static double[] readHistoryFile(String fileName) throws Exception {
		ArrayList<String> data = File.readFile(fileName);
		
		double[] d = new double[data.size()];
		
		for (int i=0; i<data.size(); i++) {
			d[i] = Double.parseDouble(data.get(i));
		}
			
		return d;
	}
	
	public static void writePredictedFile(String fileName, double[] data, double[] prediction) throws Exception {
		StringBuffer buff = new StringBuffer();
		
		for (int i=0; i<data.length; i++) {
			buff.append(i);
			buff.append("\t");
			buff.append(data[i]);
			buff.append("\t");
			buff.append(prediction[i]);
			buff.append("\n");
		}
		
		File.writeFile(fileName, buff);
	}
	
	public static void writeStatsFile(String fileName, double[] sign, double[] diff) throws Exception {
		StringBuffer buff = new StringBuffer();
		
		for (int i=0; i<sign.length; i++) {
			buff.append(i);
			buff.append("\t");
			buff.append(sign[i]);
			buff.append("\t");
			buff.append(diff[i]);
			buff.append("\n");
		}
		
		File.writeFile(fileName, buff);
	}
}
