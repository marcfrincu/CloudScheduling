package mosaic.scheduler.simulator.util.draw;

import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.PostscriptTerminal;

/**
 * Class for offering visualization facilities.
 * @author Marc Frincu
 * @since 2012
 *
 */
public class Plot {
	
	/**
	 *  Generate a GNUplot image of the predicted vs. real data
	 * @param values the vector of actual data
	 * @param predicted the vector of predicted data
	 * @param type the name of the data set
	 * @param method the method used for prediction
	 * @param filename the location including filename (without extension) of the file. Has no effect if <i>toEPS</i> is false
	 * @param toEPS true if we want the graph to be stored in an EPS file, false for direct visualization without saving
	 */
	public void plotScalingPrediction(double[] values, double[] predicted, String type, String method, String filename, boolean toEPS) {
		double[][] points = new double[values.length][3], points2 = new double[values.length][3];
		GNUPlot p = new GNUPlot();		
		PlotStyle ps = new PlotStyle();		
		ps.setStyle(Style.LINES);
		if (toEPS) {
			PostscriptTerminal pst = new PostscriptTerminal(filename + "_" + type + "-" + method + ".eps");
			pst.setEPS(true);
			pst.setColor(true);		
			p.setTerminal(pst);
		}
		
		for (int i=0; i<values.length; i++) {
			points[i][0] = i;
			points[i][1] = values[i];
			points2[i][0] = i;
			points2[i][1] = predicted[i];
		}       
		DataSetPlot s = new DataSetPlot(points);
		DataSetPlot s2 = new DataSetPlot(points2);
		s.setTitle("actual");
		s2.setTitle("predicted");
		s.setPlotStyle(ps);
		s2.setPlotStyle(ps);
		p.addPlot(s);
		p.addPlot(s2);
		p.getAxis("x").setLabel("Time [days]", "Helvetica", 14);
		p.getAxis("y").setLabel("# Visits", "Helvetica", 14);
		p.plot();
	}
}
