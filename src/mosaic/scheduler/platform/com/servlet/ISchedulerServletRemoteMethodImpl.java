package mosaic.scheduler.platform.com.servlet;

import mosaic.scheduler.platform.Scheduler;
import mosaic.scheduler.platform.algorithms.OurManyToOne;
import mosaic.scheduler.platform.settings.SystemSettings;
import org.apache.log4j.Logger;

/**
 * 
 * @author balus.tudor
 */
public class ISchedulerServletRemoteMethodImpl implements
		ISchedulerServletRemoteMethod {
	private static Logger logger = Logger
			.getLogger(ISchedulerServletRemoteMethodImpl.class.getPackage()
					.getName());
	private boolean processing = false;;
	private long startTime, lastNotification;

	@Override
	public boolean notifyScheduleProblem() {
		this.lastNotification = System.currentTimeMillis();

		//we only start a new scheduling process if the previous one has ended
		if (!this.processing) {
			this.processing = true;

			(new Thread() {
				@Override
				public void run() {
					try {
						SystemSettings
								.getSystemSettings()
								.loadProperties(
										"mosaic/scheduler/platform/settings/system.properties.platform");
					} catch (Exception e) {
						return;
					}

					OurManyToOne a = new OurManyToOne();
					Scheduler s = new Scheduler(a, false);

					if (s != null)
						ISchedulerServletRemoteMethodImpl.logger.debug("Scheduler successfully initialized.");
					else {
						ISchedulerServletRemoteMethodImpl.logger.debug("Scheduler was not initialized: null");
					}

					//as long as we received at least one notification after we started the scheduler we need to rerun it
					//since the problem data might have changed 
					do {
						startTime = System.currentTimeMillis();
						s.run();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} while (lastNotification > startTime);
					processing = false;
				}
			}).start();
		}
		return true;
	}
}