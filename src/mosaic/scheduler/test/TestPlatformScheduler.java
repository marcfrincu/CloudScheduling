package mosaic.scheduler.test;

import mosaic.scheduler.platform.Scheduler;
import mosaic.scheduler.platform.algorithms.OurManyToOne;
import mosaic.scheduler.platform.settings.SystemSettings;

import org.apache.log4j.PropertyConfigurator;

/**
 * Test class for the mOSAIC scheduler
 * @author Marc Frincu
 *
 */
public class TestPlatformScheduler {
	static{
		try {
			SystemSettings.getSystemSettings().loadProperties("mosaic/scheduler/platform/settings/system.properties.platform");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("logging.properties");
		
		//OurOnetoOne a = new OurOnetoOne();
		OurManyToOne a = new OurManyToOne();
		Scheduler s = new Scheduler(a, false);
			
		s.run();
	}

}