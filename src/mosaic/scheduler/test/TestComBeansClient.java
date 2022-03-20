package mosaic.scheduler.test;

import java.util.Vector;

import mosaic.scheduler.platform.com.Communicator;
import mosaic.scheduler.platform.com.ICommunicator;
import mosaic.scheduler.platform.com.json.beans.RequestNodeConfigValues;

import org.apache.log4j.PropertyConfigurator;

/**
 * To be started after TestComBeansServer
 * @author balus.tudor
 */
public class TestComBeansClient {
	


	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("logging.properties");
		
		ICommunicator getCommunicator = Communicator.getCommunicator();
				
		getCommunicator.addNodes(10, "1", "1");
		
		Vector<String> toBeRemoved = new Vector<String>();
		toBeRemoved.add("1");
		toBeRemoved.add("2");
		getCommunicator.removeNodes(10, "1", "1", toBeRemoved);
		
		getCommunicator.getAllNodes();
		getCommunicator.getComponentsOnGivenNode("1");
		
		RequestNodeConfigValues val = new RequestNodeConfigValues();
		val.setNumber_components(10);
		val.setComponent_type(1);
		val.setNode_id("1");
		val.setNumber_components(10);
		val.setPartition_index(1);
		
		RequestNodeConfigValues val1 = new RequestNodeConfigValues();		
		val1.setNumber_components(5);
		val1.setComponent_type(1);
		val1.setNode_id("2");
		val.setNumber_components(10);
		val.setPartition_index(2);
		
		RequestNodeConfigValues val2 = new RequestNodeConfigValues();		
		val2.setNumber_components(5);
		val2.setComponent_type(2);
		val2.setNode_id("1");
		val.setNumber_components(10);
		val.setPartition_index(3);
		
		Vector<RequestNodeConfigValues> values = new Vector<RequestNodeConfigValues>();
		values.add(val);
		values.add(val1);
		values.add(val2);
		
		getCommunicator.applySchedule(values);
		getCommunicator.getPlatformData();
		getCommunicator.getComponentWorkflow();		
	}	
}
