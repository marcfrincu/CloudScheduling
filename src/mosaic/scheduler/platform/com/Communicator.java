package mosaic.scheduler.platform.com;

import java.util.UUID;
import java.util.Vector;

import mosaic.scheduler.platform.com.json.beans.NodesBean;
import mosaic.scheduler.platform.com.json.beans.PartitionLoadListBean;
import mosaic.scheduler.platform.com.json.beans.RequestNodeConfigValues;
import mosaic.scheduler.platform.com.json.beans.ResponseCompConnParams;
import mosaic.scheduler.platform.com.json.beans.ResponseNrOfComponentsParams;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

/**
 * This class should be used for arbitrary connections on any IP & port
 * @author Marc Frincu
 *
 */
public class Communicator implements ICommunicator {
	private static Logger logger = Logger.getLogger(Communicator.class.getPackage().getName());

	HandleRequestResponses hrr = null;
	static ICommunicator com = null;
	private JSONObject json;

	private Communicator() throws Exception {
		this.hrr = new HandleRequestResponses();	
	}
	
	public static ICommunicator getCommunicator() throws Exception {
		if (Communicator.com == null)
			Communicator.com = new Communicator();
		return Communicator.com;
	}
	
	public Vector<String> addNodes(int number, String availabilityZone, String cloudID) throws Exception {
		hrr.requestAddNode(UUID.randomUUID().toString(), number, availabilityZone, cloudID);
		String s = hrr.getConnection().getData();
		Communicator.logger.info("The received response: " + s);
		this.json = JSONObject.fromObject( s );
		return hrr.responseAddNode(this.json);
	}

	public boolean removeNodes(int number, String availabilityZone, String cloudID, Vector<String> tobeRemovedIds) throws Exception {
		hrr.requestRemoveNode(UUID.randomUUID().toString(), number, availabilityZone, cloudID, tobeRemovedIds);
		String s = hrr.getConnection().getData();
		Communicator.logger.info("The received response: " + s);
		this.json = JSONObject.fromObject( s );
		return (hrr.responseRemoveNode(this.json) == null) ? false : true;
	}
	
	public Vector<NodesBean> getAllNodes() throws Exception {
		hrr.requestGetNodes(UUID.randomUUID().toString());
		String s = hrr.getConnection().getData();
		Communicator.logger.info("The received response: " + s);
		this.json = JSONObject.fromObject( s );
		return hrr.responseGetNodes(this.json);
	}
	
	public Vector<PartitionLoadListBean> getComponentsOnGivenNode(String nodeId) throws Exception {
		hrr.requestComponentsPerNode(UUID.randomUUID().toString(), nodeId);
		String s = hrr.getConnection().getData();
		Communicator.logger.info("The received response: " + s);
		this.json = JSONObject.fromObject( s );
		return hrr.responseComponentsPerNode(this.json);
	}
	
	public boolean applySchedule(Vector<RequestNodeConfigValues> values) throws Exception {
		hrr.requestApplySchedule(UUID.randomUUID().toString(), values);
		// <server sends back a Response as a JSON>
		String s = hrr.getConnection().getData();
		this.json = JSONObject.fromObject(s);
		return hrr.responseApplySchedule(this.json);
	}
	
	public ResponseNrOfComponentsParams getPlatformData() throws Exception {
		hrr.requestGetPlatformData("123");
		String s = hrr.getConnection().getData();
		this.json = JSONObject.fromObject(s);
		//TODO process platform data
		return hrr.responseGetPlatformData(this.json);
	}
	
	public ResponseCompConnParams getComponentWorkflow() throws Exception {
		hrr.requestComponentWorkflow("123");
		String s = hrr.getConnection().getData();
		this.json = JSONObject.fromObject(s);
		return hrr.responseComponentWorkflow(this.json);
	}
}
