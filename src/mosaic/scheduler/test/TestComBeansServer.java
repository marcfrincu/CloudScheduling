package mosaic.scheduler.test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * To be started before TestComBeansClient
 * 
 * @author Marc Frincu
 * 
 */
public class TestComBeansServer {
	private static Logger logger = Logger.getLogger(TestComBeansServer.class
			.getPackage().getName());

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("logging.properties");
		ServerStartUp server = new ServerStartUp();
		server.serverStart();
		server.listen();
	}
}
