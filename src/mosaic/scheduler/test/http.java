package mosaic.scheduler.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class http {
        public static void main(String[] args) throws IOException {
          /*  String address = "www.math.uvt.ro";
                
	        //Creating the client socket:
	        Socket socket = new Socket ();
	 
	        //Binding to the local socket address:
	        InetAddress localIpAddress = InetAddress.getByName ("0.0.0.0");
	        int localIpPort = 0;
	        SocketAddress localSocketAddress = new InetSocketAddress (localIpAddress, localIpPort);
	        socket.bind (localSocketAddress);
	 
	        //Connecting to the remote socket address:
	        InetAddress remoteIpAddress = InetAddress.getByName (address);
	        int remoteIpPort = 80;
	        SocketAddress remoteSocketAddress = new InetSocketAddress (remoteIpAddress, remoteIpPort);
	        socket.connect (remoteSocketAddress);
	 
	        //Receiving and/or sending data through inbound and outbound streams:
	        BufferedReader reader = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
	        BufferedWriter writer = new BufferedWriter (new OutputStreamWriter (socket.getOutputStream ()));
	 
	        StringBuilder request = new StringBuilder();
	        request.append("GET /picabou.html HTTP/1.1\r\n");
	        request.append("Host: " + address + "\r\n");
	        //signal that the HTTP request is done
	        request.append("\r\n");
	        
	        System.out.println(request.toString());
	        
	        writer.write (request.toString());
	        writer.newLine ();
	        // Do not forget to flush
	        writer.flush ();
	 
	        // Reading the response        
	        do {
	                String response = reader.readLine ();
	                System.out.println(response);
	        } while (reader.ready());
	        
	        //Shutting-down the inbound and outbound streams:
	        socket.shutdownInput ();
	        socket.shutdownOutput ();
	 
	        //Closing the socket:
	        socket.close ();
*/
       }
}