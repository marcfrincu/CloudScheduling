package mosaic.scheduler.platform.resources;

import org.apache.log4j.Logger;

import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.test.TestRunner;

public abstract class AComponentRequirements {
	public static Logger logger = Logger.getLogger(TestRunner.class.getPackage().getName());

	protected double[] cpuUsage = new double[SystemSettings.getSystemSettings().getTime_span()==Integer.MAX_VALUE ? 24 : SystemSettings.getSystemSettings().getTime_span()]; 
	protected double[] memoryUsage = new double[SystemSettings.getSystemSettings().getTime_span()==Integer.MAX_VALUE ? 24 : SystemSettings.getSystemSettings().getTime_span()];
	protected double[] networkUsage = new double[SystemSettings.getSystemSettings().getTime_span()==Integer.MAX_VALUE ? 24 : SystemSettings.getSystemSettings().getTime_span()];

	public AComponentRequirements() {	
		this.generateCpuUsage();
		this.generateMemoryUsage();
		this.generateNetworkUsage();
	}
	
	public final double getCpuUsage(int time) {
		if ((time >= 0) && (time < SystemSettings.getSystemSettings().getTime_span())) 
			return this.cpuUsage[time]; 
		else
			return -1;
	}
	
	public final double getMemoryUsage(int time) {
		if ((time >= 0) && (time < SystemSettings.getSystemSettings().getTime_span())) 
			return this.memoryUsage[time]; 
		else
			return -1;		
	}
	
	public final double getNetworkUsage(int time) {
		if ((time >= 0) && (time < SystemSettings.getSystemSettings().getTime_span())) 
			return this.networkUsage[time]; 
		else
			return -1;
	}
	
	public abstract void generateCpuUsage();
	public abstract void generateMemoryUsage();
	public abstract void generateNetworkUsage();
	
	public abstract void setCpuUsage(int time, double usage);
	public abstract void setMemoryUsage(int time, double usage);
	public abstract void setNetworkUsage(int time, double usage);
}
