package mosaic.scheduler.platform.com;

import java.io.*;
import java.net.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * This class should be used for arbitrary connections on any IP & port
 * @author balus.tudor
 */
public class ServerConnection {
	private Socket socket;
	private static Logger logger = Logger.getLogger(ServerConnection.class.getPackage().getName());

	public ServerConnection (String ipAddress, int ipPort) throws Exception {
		this.connect(ipAddress, ipPort);
	}
	
	private void connect(String ipAddress, int ipPort) throws Exception {
		// Creating the client socket:
		socket = new Socket();

		// Binding to the local socket address:
		InetAddress localIpAddress = InetAddress.getByName("0.0.0.0");
		int localIpPort = 0;
		SocketAddress localSocketAddress = new InetSocketAddress(localIpAddress, localIpPort);
		socket.bind(localSocketAddress);

		// Connecting to the remote socket address:
		InetAddress remoteIpAddress = InetAddress.getByName(ipAddress);
		
		SocketAddress remoteSocketAddress = new InetSocketAddress(remoteIpAddress, ipPort);
		socket.connect(remoteSocketAddress);

		logger.info("Connection to server established. ");
	}

	public void sendData(JSONObject json) throws Exception {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		logger.info("Sending request to server: " + json.toString());
		writer.write(json.toString());		
		writer.newLine();
		writer.flush();
	}

	public String getData() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
		String response = reader.readLine();
		logger.info("Receiving response from server" + response);
		return response;
	}

	public void clientStop() throws Exception {
		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();
	}

}
