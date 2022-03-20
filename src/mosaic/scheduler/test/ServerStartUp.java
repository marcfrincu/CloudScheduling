package mosaic.scheduler.test;

import java.io.*;
import java.net.*;
import java.util.Vector;

import mosaic.scheduler.platform.com.json.beans.Request;
import mosaic.scheduler.platform.com.json.beans.RequestForStatusOfComp;
import mosaic.scheduler.platform.com.json.beans.RequestNodeConfig;
import mosaic.scheduler.platform.com.json.beans.ResponseCompConn;
import mosaic.scheduler.platform.com.json.beans.ResponseNrOfComponents;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * 
 * @author balus.tudor
 */
public class ServerStartUp {
	private static Logger logger = Logger.getLogger(ServerStartUp.class
			.getPackage().getName());

	private ServerSocket socket;
	private static Vector<Request> reqDB = new Vector<Request>();
	private static Vector<RequestForStatusOfComp> reqCompDB = new Vector<RequestForStatusOfComp>();
	private static Vector<RequestNodeConfig> reqNodeConfigDB = new Vector<RequestNodeConfig>();
	private static Vector<ResponseNrOfComponents> respNrCompDB = new Vector<ResponseNrOfComponents>();
	private static Vector<ResponseCompConn> respConnDB = new Vector<ResponseCompConn>();
	private static String response;

	public void serverStart() throws Exception {
		// Creating the server socket:
		socket = new ServerSocket();
		// Binding to the local socket address __ this is the one the clients
		// should be connecting to:
		InetAddress localIpAddress = InetAddress.getByName("0.0.0.0");
		int localIpPort = 20000;
		SocketAddress localSocketAddress = new InetSocketAddress(
				localIpAddress, localIpPort);
		socket.bind(localSocketAddress);

		System.out.println("Server is online. ");
		logger.info("Server is online. ");
	}

	public void listen() throws Exception {
		logger.info("waiting");
		final Socket client = socket.accept();
		logger.info("gotit");
		
		while (true) {
			// For each connection accepting a client socket, and:
			// Starting a new Thread for each client
					try {
						// Receiving and/or sending data;
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(client.getInputStream()));
						BufferedWriter writer = new BufferedWriter(
								new OutputStreamWriter(client.getOutputStream()));
						// Reading the request and decoding to see what kind of request it is
						
						String request = reader.readLine();
						JSONObject json = JSONObject.fromObject(request);
						logger
								.debug("The JSON that was received by the server: "
										+ json);
						int value = decode(json);
						logger.info("the case selected: " + value);
						Object req;

						switch (value) {
						case 1: {
							req = ((Request) JSONObject.toBean(json,
									Request.class));
							// <the server adds a node>

							response = "{\"id\":\""+((Request) JSONObject.toBean(json,
									Request.class)).getId()+"\"," +
										"\"jsonrpc\":\"2.0\"," +
										"\"result\":\"null\"," +
										"\"error\":\"404\"}";
							logger
									.debug("The JSON that was sent by the server: "
											+ response);
							break;
						}
						case 2: {
							req = (Request) JSONObject.toBean(json,
									Request.class);
							// <the server removes a node>
							response = "{\"id\":\""+((Request) JSONObject.toBean(json,
									Request.class)).getId()+"\"," +
										"\"jsonrpc\":2.0," +
										"\"result\":\"null\"," +
										"error:\"200\"" +
									"}";
							logger
									.debug("The JSON that was sent by the server: "
											+ response);
							break;
						}
						case 3: {
							req = (Request) JSONObject.toBean(json,
									Request.class);
							// <the server gets info on the nodes>
							response = "{\"id\": \""+((Request) JSONObject.toBean(json,
									Request.class)).getId()+"\"," +
										"\"jsonrpc\": " +
										"\"2.0\"," +
										"error:\"200\"," +
										"\"result\":{" +
											"\"method\": \"mosaic.scheduler.responseGetNodes\"," +
											"\"params\":{" +
												"\"nodes\" : [" +
													"{\"node_id\": \"1\"," +
													"\"node_load\": {" +
															"\"onemin\" : 0.9," +
															"\"threemin\" : 0.2," +
															"\"fivemin\" : 0.2" +
														"}," + 
													"\"node_datacenter_id\":\"1\"," +
													"\"node_cloud_id\":\"1\"}" +
												"]" +
											"}" +
										"}" +
									"}";
							logger
									.debug("The JSON that was sent by the server: "
											+ response);
							break;
						}
						case 4: {
							req = (RequestForStatusOfComp) JSONObject.toBean(
									json, RequestForStatusOfComp.class);
							// <the server gets the status of the components on a node>
							response = "{\"id\":\"" + ((RequestForStatusOfComp) JSONObject.toBean(
									json, RequestForStatusOfComp.class)).getId() +"\"," +
										"\"jsonrpc\": \"2.0\"," +
										"error:\"200\"," +
										"\"result\":{" +
											"\"method\":\"mosaic.scheduler.responseGetComponentsPerNode\"," +
											"\"params\": {" +
												"\"partition_load_list\":[" +
													"{\"component_type\":1," +
													"\"component_load\": {" +
															"\"oneMin\":"+ 0.8 + "," +
															"\"threeMin\":"+ 0.8 + "," +
															"\"fiveMin\":"+ 0.8 +
															"},"+
													"\"number_components\":10," +
													"\"partition_index\": 1" +
													"}" +
												"]" +
											"}" +
										"}" +
									"}";
							logger
									.debug("The JSON that was sent by the server: "
											+ response);
							break;
						}
						case 5: {
							req = ((RequestNodeConfig) JSONObject.toBean(json,
									RequestNodeConfig.class));
							// <the server applies the new node configuration after scheduling>
							response = "{\"id\":\""+((RequestNodeConfig) JSONObject.toBean(json,
									RequestNodeConfig.class)).getId()+"\"," +
										"\"jsonrpc\":\"2.0\"," +
										"\"result\":\"null\"," +
										"\"error\":\"200\"}";
							logger
									.debug("The JSON that was sent by the server: "
											+ response);
							break;
						}
						case 6: {
							req = ((Request) JSONObject.toBean(json,
									Request.class));
							// <the server selects the nr of total components>
							response = "{\"id\": \""+((Request) JSONObject.toBean(json,
									Request.class)).getId()+"\"," +
									"\"jsonrpc\": \"2.0\"," +
									"error:\"200\"," +
									"\"result\": {" +
										"\"method\":\"mosaic.scaler.responseGetPlatformData\"," +
										"\"params\":{" +
											"\"node_list\" : [" +
												"{\"node_id\": 1," +
												"\"components\":[" +
													"{\"component_type\": 1," +
													"\"component_number\": 10," +
													"\"partition_index\": 1}" +
													"]" +
												"}" +
											"]," +
											"\"queue_list\":[" +
												"{\"queue_id\": \"1_2\"," +
												"\"no_messages\":" +
													"{\"oneMin\":0.8, \"threeMin\":0.3,\"fiveMin\":0.9\"}" +
												"}" +
											"]" +
										"}" +
									"}" +
								"}";
							logger
									.debug("The JSON that was sent by the server: "
											+ response);
							break;
						}
						case 7: {
							req = (Request) JSONObject.toBean(json,
									Request.class);
							// <server gets the components connection table>
							response = "{\"id\": \""+((Request) JSONObject.toBean(json,
									Request.class)).getId()+"\"," +
										"\"jsonrpc\": \"2.0\"," +
										"\"error\":\"200\"," +
										"\"result\":{" +
											"\"method\":\"mosaic.scheduler.storeComponentWorkflow\"," +
											"\"params\": {" +
												"\"component_workflow\": [" +
													"{\"component_type\":1," +
													"\"linked_to_component\": [\"2\",\"3\"]," +
													"\"read_rate\" : [\"30\",\"20\",\"10\"]," +
													"\"write_rate\" : [\"20\",\"10\",\"10\"]" +
													"}," +
													"{\"component_type\":2," +
													"\"linked_to_component\":[\"1\",\"4\"]," +
													"\"read_rate\" : [\"30\",\"20\",\"10\"]," +
													"\"write_rate\" : [\"20\",\"10\",\"10\"]" +
													"}," +
													"{\"component_type\":3," +
													"\"linked_to_component\":[\"1\",\"4\"]," +
													"\"read_rate\" : [\"30\",\"20\",\"10\"]," +
													"\"write_rate\" : [\"20\",\"10\",\"10\"]" +
													"}," +
													"{\"component_type\":4," +
													"\"linked_to_component\":[\"2\",\"3\"]," +
													"\"read_rate\" : [\"30\",\"20\",\"10\"]," +
													"\"write_rate\" : [\"20\",\"10\",\"10\"]" +
													"}" +
												"]" +
											"}" +
										"}" +
									"}";
							logger
									.debug("The JSON that was sent by the server: "
											+ response);
							break;
						}
						}

						// Write the response
						writer.write(response);
						writer.newLine();
						// Do not forget to flush!
						writer.flush();
						
					} catch (Exception e) {
						ServerStartUp.logger.error(e.getMessage());
						System.exit(0);
					}
		}
		//client.close();
	}

	// this method takes the string received from the client and decodes it to
	// find out which type of request was sent
	public int decode(JSONObject json) {
		int value = 0;
		if (json == null)
			return -1;
		
		ServerStartUp.logger.debug(json.toString());
		String s;
		try {
			s = (String) json.get("method");
		} catch (JSONException jse) {
			return -1;
		}
		
		s = s.trim();

		if (s.equals("mosaic.provisioner.addNodes"))
			value = 1;
		else if (s.equals("mosaic.provisioner.removeNodes"))
			value = 2;
		else if (s.equals("mosaic.provisioner.getNodes"))
			value = 3;
		else if (s.equals("mosaic.provisioner.getComponentsPerNode"))
			value = 4;
		else if (s.equals("mosaic.provisioner.applySchedule"))
			value = 5;
		else if (s.equals("mosaic.provisioner.getPlatformData"))
			value = 6;
		else if (s.equals("mosaic.provisioner.getComponentWorkflow"))
			value = 7;

		return value;
	}

}