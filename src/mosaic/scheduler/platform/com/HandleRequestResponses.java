package mosaic.scheduler.platform.com;

import mosaic.scheduler.platform.com.json.beans.NodesBean;
import mosaic.scheduler.platform.com.json.beans.PartitionLoadListBean;
import mosaic.scheduler.platform.com.json.beans.RequestRemoveNodes;
import mosaic.scheduler.platform.com.json.beans.ResponseCompConnParams;
import mosaic.scheduler.platform.com.json.beans.ResponseNrOfComponentsParams;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import mosaic.scheduler.platform.com.json.beans.*;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

/**
 * Class for sending JSON requests and processing the responses
 * 
 * @author Marc Frincu
 * 
 */
public class HandleRequestResponses {
	private static Logger logger = Logger
			.getLogger(HandleRequestResponses.class.getPackage().getName());

	// Here we could probably use only one vector of ints to check if a request
	// has received a reply or not
	private Vector<Request> reqDB = new Vector<Request>();
	private Vector<RequestRemoveNodes> reqRemoveDB = new Vector<RequestRemoveNodes>();
	private Vector<RequestForStatusOfComp> reqCompDB = new Vector<RequestForStatusOfComp>();
	private Vector<RequestNodeConfig> reqNodeConfigDB = new Vector<RequestNodeConfig>();
	private Vector<ResponseNrOfComponents> respNrCompDB = new Vector<ResponseNrOfComponents>();
	private Vector<ResponseCompConn> respConnDB = new Vector<ResponseCompConn>();

	private ServerConnection conn;

	public ServerConnection getConnection() {
		return conn;
	}

	public HandleRequestResponses() throws Exception {
		conn = new ServerConnection("localhost", 20000);
	}

	/**
	 * Method for sending a REQUEST for adding nodes to the platform
	 * 
	 * @param requestId
	 *            the request ID
	 * @param number
	 *            the number of nodes to be added
	 * @param availabilityZoneId
	 *            the availability zone where the nodes should be placed
	 * @param cloudId
	 *            the ID of the cloud where the nodes should be placed
	 * @throws Exception
	 */
	public void requestAddNode(String requestId, int number,
			String availabilityZoneId, String cloudId) throws Exception {
		Params p = new Params();
		p.setNumber(number);
		p.setAvailability_zone_id(availabilityZoneId);
		p.setCloud_id(cloudId);

		sendRequestAddRemoveNode(requestId, p, true);
	}

	/**
	 * Method for sending a REQUEST for removing nodes from the platform
	 * 
	 * @param requestId
	 *            the request ID
	 * @param number
	 *            the number of nodes to be removed
	 * @param availabilityZoneId
	 *            the availability zone from where the nodes should be removed
	 * @param cloudId
	 *            the ID of the cloud from where the nodes should be removed
	 * @param idsToBeRemoved the ID list to be removed
	 * @throws Exception
	 */
	public void requestRemoveNode(String requestId, int number,
			String availabilityZoneId, String cloudId, Vector<String> idsToBeRemoved) throws Exception {
		ParamsRemoveNodes p = new ParamsRemoveNodes();
		p.setNumber(number);
		p.setAvailability_zone_id(availabilityZoneId);
		p.setCloud_id(cloudId);		
		p.setTo_be_removed_nodes_ids((String[])idsToBeRemoved.toArray());

		sendRequestAddRemoveNode(requestId, p, false);
	}

	private void sendRequestAddRemoveNode(String requestId, Object o,
			boolean add) throws Exception {
		Object req;

		if (add) {
			req = new Request();
			((Request) req).setMethod("mosaic.provisioner.addNodes");
			((Request) req).setParams((Params) o);
			((Request) req).setJsonrpc("2.0");
			((Request) req).setId(requestId);

			// add request to list
			reqDB.add(((Request) req));
		}

		else {
			req = new RequestRemoveNodes();
			((RequestRemoveNodes) req)
					.setMethod("mosaic.provisioner.removeNodes");
			((RequestRemoveNodes) req).setParams((ParamsRemoveNodes) o);
			((RequestRemoveNodes) req).setJsonrpc("2.0");
			((RequestRemoveNodes) req).setId(requestId);

			// add request to list
			reqRemoveDB.add(((RequestRemoveNodes) req));
		}

		JSONObject json = JSONObject.fromObject(req);
		logger.debug("The JSON that was sent by the client: " + json);
		conn.sendData(json);
	}

	/**
	 * Method for processing the RESPONSE to a request for adding nodes
	 * 
	 * @param json
	 *            the received JSON object
	 */
	public Vector<String> responseAddNode(JSONObject json) {
		return receiveResponseAddRemoveNode(json, "add");
	}

	/**
	 * Method for processing the RESPONSE to a request for removing nodes
	 * 
	 * @param json
	 *            the received JSON object
	 */
	public Vector<String> responseRemoveNode(JSONObject json) {
		return receiveResponseAddRemoveNode(json, "remove");
	}

	private Vector<String> receiveResponseAddRemoveNode(JSONObject json, String s) {
		logger.debug("The JSON that was received by the client: " + json);
		Response resp = null;
		ResponseNodeRemove resp1 = null;
		boolean ok = false;
		if (s.equals("add")) {
			resp = (Response) JSONObject.toBean(json, Response.class);
			ok = this.checkResponse(resp);
		} else {
			resp1 = (ResponseNodeRemove) JSONObject.toBean(json,
					ResponseNodeRemove.class);
			ok = this.checkResponse(resp1);
		
		}
		if (!ok) {
			logger.error("Failed to " + s + " nodes");
			return null;
		}
		else {
			logger.error("Nodes " + s + " successfully");
			if (s.equals("add")) {
				String ids = "";
				if (resp != null)
					for (String id : resp.getResult().getCreated_nodes_ids())
						ids += id + " ";
				logger.error("Nodes' IDs: " + ids);
				Vector<String> repl = new Vector<String>();
				for (String ss : resp.getResult().getCreated_nodes_ids())
					repl.add(ss);
				return repl;
			}
			else {
				return new Vector<String>();
			}
		}
	}

	/**
	 * Method for sending a REQUEST query on info on the current nodes
	 * 
	 * @param requestID
	 *            the ID of the request
	 * @throws Exception
	 */
	public void requestGetNodes(String requestID) throws Exception {
		Request req = new Request();
		req.setMethod("mosaic.provisioner.getNodes");

		req.setId(requestID);
		req.setParams(null);
		req.setJsonrpc("2.0");

		// add request to list
		reqDB.add(req);

		JSONObject json = JSONObject.fromObject(req);
		logger.debug("The JSON that was sent by the client: " + json);
		conn.sendData(json);
	}

	/**
	 * Method for processing the RESPONSE to a request for nodes' info
	 * 
	 * @param json
	 *            the received JSON object
	 */
	@SuppressWarnings("unchecked")
	public Vector<NodesBean> responseGetNodes(JSONObject json) {
		logger.debug("The JSON that was received by the client: " + json);
		Map<String, Class> beanMap = new HashMap<String, Class>();
		beanMap.put("nodes", NodesBean.class);
		NodeResponse resp = (NodeResponse) JSONObject.toBean(json,
				NodeResponse.class, beanMap);		
		if (this.checkResponse(resp)) {
			logger.info("Nodes retrieved");
			Vector<NodesBean> repl = new Vector<NodesBean>();
			for (NodesBean ss : resp.getResult().getParams().getNodes())
				repl.add(ss);
			return repl;
		}
		logger.info("Error retrieving nodes");
		return null;
	}

	/**
	 * Method for sending a query REQUEST for the status of the components on a
	 * given node
	 * 
	 * @param nodeId
	 *            the node ID
	 * @throws Exception
	 */
	public void requestComponentsPerNode(String requestId, String nodeId)
			throws Exception {
		RequestForStatusOfComp req = new RequestForStatusOfComp();
		req.setMethod("mosaic.provisioner.getComponentsPerNode");
		NodeIDBean nib = new NodeIDBean();
		nib.setNode_id(nodeId);
		req.setParams(nib);
		req.setJsonrpc("2.0");
		req.setId(requestId);

		// add request to list
		reqCompDB.add(req);

		JSONObject json = JSONObject.fromObject(req);
		logger.debug("The JSON that was sent by the client: " + json);
		conn.sendData(json);
	}

	/**
	 * Method for processing the RESPONSE to a request for the status of a
	 * node's components
	 * 
	 * @param json
	 *            the received JSON object
	 */
	public Vector<PartitionLoadListBean> responseComponentsPerNode(JSONObject json) {
		logger.debug("The JSON that was received by the client: " + json);
		Map<String, Class> beanMap = new HashMap<String, Class>();
		beanMap.put("partition_load_list", PartitionLoadListBean.class);
		ResponseForStatusOfComp resp = (ResponseForStatusOfComp) JSONObject
				.toBean(json, ResponseForStatusOfComp.class, beanMap);
		if (this.checkResponse(resp)) {
			logger.info("Partion for message: " + resp.getId() + " retrieved");
			Vector<PartitionLoadListBean> repl = new Vector<PartitionLoadListBean>();
			for (PartitionLoadListBean ss : resp.getResult().getParams().getPartition_load_list())
				repl.add(ss);
			return repl;
		}
		logger.info("Partion for message: " + resp.getId() + " not retrieved");
		return null;
	}

	/**
	 * Method for sending a REQUEST to apply a new schedule
	 * 
	 * @param array
	 *            the vector of RequestNodeConfigValues containing information
	 *            on the components for every node
	 * @throws Exception
	 */
	public void requestApplySchedule(String requestId,
			Vector<RequestNodeConfigValues> array) throws Exception {
		RequestNodeConfig req = new RequestNodeConfig();
		req.setParams((RequestNodeConfigValues[])array.toArray());
		req.setMethod("mosaic.provisioner.applySchedule");
		req.setJsonrpc("2.0");
		req.setId(requestId);

		// add request to list
		reqNodeConfigDB.add(req);

		JSONObject json = JSONObject.fromObject(req);
		logger.debug("The JSON that was sent by the client: " + json);
		conn.sendData(json);
	}

	/**
	 * Method for processing the RESPONSE to a request for applying a new
	 * schedule
	 * 
	 * @param json
	 * @return true if the schedule was applied or false otherwise
	 */
	public boolean responseApplySchedule(JSONObject json) {
		logger.debug("The JSON that was received by the client: " + json);
		// even though this is the the response for the send new schedule,
		// the ResponseNodeRemove is used because they have exactly the
		// same structure (even after the revision).
		ResponseNodeRemove resp = (ResponseNodeRemove) JSONObject.toBean(json,
				ResponseNodeRemove.class);
		if (this.checkResponse(resp)) {
			logger.info("Schedule applied");
			return true;
		}
		logger.info("Schedule not applied due to errors");
		return false;
	}

	/**
	 * Method for sending a REQUEST query on info on the current platform
	 * configuration
	 * 
	 * @param requestID
	 *            the ID of the request
	 * @throws Exception
	 */
	public void requestGetPlatformData(String requestID) throws Exception {
		Request req = new Request();
		req.setMethod("mosaic.provisioner.getPlatformData");

		req.setId(requestID);
		req.setParams(null);
		req.setJsonrpc("2.0");

		// add request to list
		reqDB.add(req);

		JSONObject json = JSONObject.fromObject(req);
		logger.debug("The JSON that was sent by the client: " + json);
		conn.sendData(json);
	}

	/**
	 * Method for processing the RESPONSE to a request for querying the
	 * information available on existing platform configuration
	 * 
	 * @param json
	 *            the received JSON object
	 * @return 
	 */
	public ResponseNrOfComponentsParams responseGetPlatformData(JSONObject json) {
		logger.debug("The JSON that was received by the client: " + json);
		ResponseNrOfComponents resp = (ResponseNrOfComponents) JSONObject
				.toBean(json, ResponseNrOfComponents.class);
		if (this.checkResponse(resp)) {
			logger.info("Platform data retrieved successfully");
			return resp.getResult().getParams();
		}
		else {
			logger.info("Error retrieving platform data");
			return null;
		}
			
	}

	/**
	 * Method for sending a REQUEST query on info on the component connection
	 * table
	 * 
	 * @param requestID
	 *            the ID of the request
	 * @throws Exception
	 */
	public void requestComponentWorkflow(String requestID) throws Exception {
		Request req = new Request();
		req.setMethod("mosaic.provisioner.getComponentWorkflow");

		req.setId(requestID);
		req.setParams(null);
		req.setJsonrpc("2.0");

		// add request to list
		reqDB.add(req);

		JSONObject json = JSONObject.fromObject(req);
		logger.debug("The JSON that was sent by the client: " + json);
		conn.sendData(json);
	}

	/**
	 * method for processing the RESPONSE to a request for the component
	 * connection table
	 * 
	 * @param json
	 *            the received JSON object
	 * @return 
	 */
	public ResponseCompConnParams responseComponentWorkflow(JSONObject json) {
		logger.debug("The JSON that was received by the client: " + json);
		
		Map<String, Class> beanMap = new HashMap<String, Class>();
		beanMap.put("component_workflow", ComponentWorkflowBean.class);
		
		ResponseCompConn resp = (ResponseCompConn) JSONObject.toBean(json,
				ResponseCompConn.class, beanMap);
		if (this.checkResponse(resp)) {
			logger.info("Component workflow retrieved successfully");
			return resp.getResult().getParams();			
		}
		else {
			logger.info("Error retrieving component workflow");
			return null;						
		}
	}

	private boolean checkResponse(Response resp) {
		return this.checkRsp(resp.getId(), resp.getError());
	}

	private boolean checkResponse(ResponseNodeRemove resp) {
		return this.checkRsp(resp.getId(), resp.getError());
	}

	private boolean checkResponse(ResponseCompConn resp) {
		return this.checkRsp(resp.getId(), resp.getError());
	}

	private boolean checkResponse(ResponseForStatusOfComp resp) {
		return this.checkRsp(resp.getId(), resp.getError());
	}

	private boolean checkResponse(ResponseNrOfComponents resp) {
		return this.checkRsp(resp.getId(), resp.getError());
	}

	private boolean checkResponse(NodeResponse resp) {
		return this.checkRsp(resp.getId(), resp.getError());
	}

	private boolean checkRsp(String id, String error) {
		Scanner input = new Scanner(error);
		try {
			int code = input.nextInt();
			if (code == 200) {
				logger.debug("Response OK (200)");
				for (int i = 0; i < reqDB.size(); i++) {
					Request req = new Request();
					req = reqDB.get(i);

					if (req.getId().equals(id))
						reqDB.remove(i);
				}
				return true;
			} else {
				logger.error("An error occured: " + code + " for message: " + id);
				return false;
			}
		} catch (InputMismatchException ime) {
			logger.error("An error occured: '" + error + "' for message: " + id);
			return false;
		}
	}
}
