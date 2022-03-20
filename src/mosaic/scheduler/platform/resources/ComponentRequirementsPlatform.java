package mosaic.scheduler.platform.resources;

import mosaic.scheduler.platform.settings.SystemSettings;

public class ComponentRequirementsPlatform extends AComponentRequirements {

	@Override
	public void generateCpuUsage() {
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			this.cpuUsage[i] = 30;
		}
	}

	@Override
	public void generateMemoryUsage() {
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			this.memoryUsage[i] = 30;
		}		
	}

	@Override
	public void generateNetworkUsage() {
		for (int i=0; i<SystemSettings.getSystemSettings().getTime_span(); i++) {
			this.networkUsage[i] = 30;
		}
	}

	@Override
	public void setCpuUsage(int time, double usage) {
		if (time < 0 && time >= SystemSettings.getSystemSettings().getTime_span()) {
			ComponentRequirementsPlatform.logger.fatal("time should be inside the specified time span");
			return;
		}
		this.cpuUsage[time] = usage;		
	}

	@Override
	public void setMemoryUsage(int time, double usage) {
		if (time < 0 && time >= SystemSettings.getSystemSettings().getTime_span()) {
			ComponentRequirementsPlatform.logger.fatal("time should be inside the specified time span");
			return;
		}		
		this.memoryUsage[time] = usage;
	}

	@Override
	public void setNetworkUsage(int time, double usage) {
		if (time < 0 && time >= SystemSettings.getSystemSettings().getTime_span()) {
			ComponentRequirementsPlatform.logger.fatal("time should be inside the specified time span");
			return;
		}
		this.networkUsage[time] = usage;
	}
}
