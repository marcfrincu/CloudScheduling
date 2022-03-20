package mosaic.scheduler.platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import mosaic.scheduler.platform.com.json.beans.ComponentWorkflowBean;
import mosaic.scheduler.platform.com.json.beans.ComponentsBean;
import mosaic.scheduler.platform.com.json.beans.NodeListBean;
import mosaic.scheduler.platform.com.json.beans.QueueListBean;
import mosaic.scheduler.simulator.util.Runner;

/**
 * This class represents the core of the scaling mechanism used by the mOSAIC platform
 * NOTE: Scaling is not used for the moment in moSAIC
 * @author Marc Frincu
 *
 */
public class Scaler {
	private static Logger logger = Logger.getLogger(Runner.class.getPackage().getName());

	public double[] scale(Vector<ComponentWorkflowBean> componentData, Vector<QueueListBean> queueData,  Vector<NodeListBean> nodes) {
		int[] totalNoComponents = new int[componentData.size()];
		double[] finalNoComponents = new double[componentData.size()];
		int[][] connection = new int[componentData.size()][componentData.size()];
		int[][] readRate = new int[componentData.size()][componentData.size()];
		int[][] writeRate = new int[componentData.size()][componentData.size()];
		double[][] initialMessage = new double[componentData.size()][componentData.size()];
		double[][] currentMessage = new double[componentData.size()][componentData.size()];
		double[][] previousMessage = new double[componentData.size()][componentData.size()];
		
		
		//build the component connection table & the read rate table
		for (ComponentWorkflowBean cwb : componentData) {
			String[] links = cwb.getLinked_to_Component();
			String[] reads = cwb.getRead_rate();
			String[] writes = cwb.getWrite_rate();
			for (int i=0; i< links.length; i++) {
				if (links[i].trim().length() > 0 && reads[i].trim().length() > 0)
					connection[cwb.getComponent_type()][Integer.parseInt(links[i])] = 1;
					//cwb is read by its links
					readRate[cwb.getComponent_type()][Integer.parseInt(links[i])] = Integer.parseInt(reads[i]);
					//cwb writes to its links
					writeRate[cwb.getComponent_type()][Integer.parseInt(links[i])] = Integer.parseInt(writes[i]);
			}
		}
				
		//compute total number of components available in the platform
		for (NodeListBean ncb : nodes) {
			for (ComponentsBean cb : ncb.getComponents()) {
				totalNoComponents[cb.getComponent_type()] += cb.getComponent_number();
				//finalNoComponents[cb.getComponent_type()] = totalNoComponents[cb.getComponent_type()];
			}			
		}
		
		Scaler.logger.debug("Total number of components per type");		
		for (int i=0; i<totalNoComponents.length; i++)
			Scaler.logger.debug(i + "\t" + totalNoComponents[i]);		
		
		Scaler.logger.debug("Connection table");		
		for (int i=0; i<componentData.size(); i++) {
			String s = "";
			for (int j=0; j<componentData.size(); j++)
				s += connection[i][j] + "\t";
			Scaler.logger.debug(s);
		}
		
		Scaler.logger.debug("Read/write table");		
		for (int i=0; i<componentData.size(); i++) {
			String s = "";
			for (int j=0; j<componentData.size(); j++)
				s += readRate[i][j] + "/" + writeRate[i][j] + "\t";
			Scaler.logger.debug(s);
		}
		
		//compute the initialMessage table, i.e., [i][j] contains the number of messages initially existing in the queue from i to j
		for (QueueListBean qlb : queueData) {			
			String[] endpoints = qlb.getQueue_id().split("-");
			System.out.println(qlb + " " + qlb.getNo_messages());
			initialMessage[Integer.parseInt(endpoints[0])][Integer.parseInt(endpoints[1])] = qlb.getNo_messages().getOneMin();
		}

		//this method was just a test to see whether we would be able to use a free web based NLP to compute the optimal number of components 
		//this.computeNonLinearMinimizationEquation(connection, initialMessage, readRate);
		
		double readCapacity = 0;
		boolean identical = true;
		do {
			
			for (int i=0; i< componentData.size(); i++)
				for (int j=0; j< componentData.size(); j++)
					previousMessage[i][j] = currentMessage[i][j];
			
			//for every component type i
			for (int i=0; i<componentData.size(); i++) {
				double tmpComponentNo = 0;
				//get the components j linking to it
				for (int j=0; j<componentData.size(); j++) {					
					//check link from j to i
					if (i!=j && connection[j][i] == 1) {
						currentMessage[j][i] = initialMessage[j][i] + finalNoComponents[j] * writeRate[j][i];						
						//compute the current read capacity of the component type i 
						readCapacity = finalNoComponents[i] * readRate[i][j];
						Scaler.logger.debug(j+ "->" + i + "\t" + initialMessage[j][i] + " " + finalNoComponents[j] + "*" + writeRate[j][i] + "\t" + readCapacity + " " + currentMessage[j][i]);
						//if this capacity is lower than the existing message queue size update the number of component accordingly
						if (readCapacity < currentMessage[j][i]) {
							//maximum between the number of components of type i we have so far and the needed at this link
							tmpComponentNo = Math.max(tmpComponentNo, Math.round((currentMessage[j][i] - readCapacity) / (float)readRate[i][j])); 
						}
					}
				}
				//the final number of components is the maximum between what we computed at this step and the existing ones
				finalNoComponents[i] = Math.max(tmpComponentNo, finalNoComponents[i]);
				Scaler.logger.debug(i + " " + finalNoComponents[i]);
			}
			
			//compute the stopping criterion, i.e., stop when the message matrix has not changed
			identical = true;
			for (int i=0; i<componentData.size(); i++) {
				for (int j=0; j<componentData.size(); j++) {
					if (i!=j && connection[j][i] == 1) {
						readCapacity = finalNoComponents[i] * readRate[i][j];						
						if (readCapacity < currentMessage[j][i]) {
							identical = false;
						}
					}
				}
			}
			
			Scaler.logger.debug("Read capacity vs current message table:");
			for (int i=0; i< componentData.size(); i++) {
				String s = "";
				for (int j=0; j< componentData.size(); j++) {
					s += (finalNoComponents[j] * readRate[j][i]) + "/" + currentMessage[i][j] + "\t";
				}
				Scaler.logger.debug(s);
			}

		} while (identical == false);
		
		Scaler.logger.debug("Required number of components:");
		for (int i=0; i<finalNoComponents.length; i++) {
			finalNoComponents[i] = finalNoComponents[i] - totalNoComponents[i];
			Scaler.logger.debug(i + "\t" + finalNoComponents[i]);
		}
		
		return finalNoComponents;
	}
	
	public void storeNumberOfComponents(Vector<NodeListBean> components) {
		//TODO
	}
	
	/**
	 * @deprecated
	 * @param connection
	 * @param initialMessage
	 * @param readRate
	 * @return
	 */
	@SuppressWarnings("unused")
	private int[] computeNonLinearMinimizationEquation(int[][] connection, int[][] initialMessage, int[][] readRate) {
		StringBuilder sb = new StringBuilder();
		sb.append("Model 3d");
		sb.append("\n");
		sb.append("Variables");
		sb.append("\n");
		
		String s = "";
		for (int i=0; i<readRate.length; i++) {
			sb.append("x"+(i+1));
			sb.append("\n");
			if (i<readRate.length-1)
				s += "x" + (i+1) + "+";
			else
				s += "x" + (i+1);
		}
		
		sb.append("End Variables");
		sb.append("\n");
		
		sb.append("Equations");
		sb.append("\n");
		sb.append(s + " = 100");
		sb.append("\n");		
		for (int i=0; i<readRate.length; i++) {
			sb.append("x"+(i+1) + " > 1");
			sb.append("\n");
		}
		
		s = "minimize ";
		for (int i=0; i<connection[0].length; i++) {
			for (int j=0; j<connection[0].length; j++) {
				if (i!=j && connection[j][i] == 1) {
					s += Math.pow(initialMessage[j][i],2) + "-2*" + readRate[i][j] + "*x" + (i+1) + "+" + readRate[i][j] + "^2*x" + (i+1) + "^2" + "+";   
				}
			}
		}
		
		s = s.substring(0, s.length() - 1);
		
		sb.append(s);
		sb.append("\n");
		sb.append("End Equations");
		sb.append("\n");
		sb.append("End Model");
		
	//	Scaler.logger.debug("Sending NLP: " + sb.toString());
		
		HTTP_NLP http = new HTTP_NLP();
		
		String id = http.getAPMonitorPOSTID("http://www.apmonitor.com/online/view_pass.php");
		Scaler.logger.debug("Found NLP APMonitor ID: " + id);
			
		try {
			String result = http.submitNLP(sb.toString(), "http://www.apmonitor.com/online/save.php", id);
			//Scaler.logger.debug("Response HTTP for NLP request : " + result);			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String result = http.getNLPResult("http://www.apmonitor.com/online/"+ id + "/" + id + "_var.htm");

		Scaler.logger.debug("Response HTTP for NLP ("+ "http://www.apmonitor.com/online/"+ id + "/" + id + "_var.htm) : " + result);
		
		return null;
	}
		
	/**
	 * @deprecated
	 * @author ieat
	 *
	 */
	class HTTP_NLP {
		
		public String getNLPResult(String urlString) {
			return this.call(null, urlString);
		}
		
		public String submitNLP(String nlp, String urlString, String id) throws UnsupportedEncodingException {
			
			String data = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(nlp, "UTF-8");
     	    data += "&" + URLEncoder.encode("d", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
     	    data += "&" + URLEncoder.encode("f", "UTF-8") + "=" + URLEncoder.encode(id + ".apm", "UTF-8");
     	    data += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode("17201206", "UTF-8");
		
     	    return this.call(data, urlString);
     	     
		}
		
		public String getAPMonitorPOSTID(String urlString) {
			String id = null;
			
			String response = this.call(null, urlString);
			
			System.out.println(response);
			
			Pattern p = Pattern.compile("<title>(.*?)</title>");
	        Matcher m = p.matcher(response);
	        if (m.find()) {
	        	id = m.group(1);
	        	id = id.substring(id.indexOf('-') + 1, id.lastIndexOf('.')).trim();
	        }
			
			return id;
		}
		
		private String call(String data, String urlString) {
			StringBuilder sb = new StringBuilder();
			try{
				URL url = new URL(urlString);
	    	    
	    	    URLConnection conn = url.openConnection();
	    	    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	    	    conn.setRequestProperty("Accept-Charset", "UTF-8");
	    	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	    	    conn.setDoOutput(true);
	    	   
	    	    for (Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
	    	        System.out.println(header.getKey() + "=" + header.getValue());
	    	    }

	    	    
	    	    OutputStreamWriter wr = null;
	    	    if (data != null) {	    	    	
		    	    wr = new OutputStreamWriter(conn.getOutputStream());
		    	    wr.write(data);
		    	    wr.flush();
	    	    }
	    	    // Get the response
	    	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    	    String line;
	    	    while ((line = rd.readLine()) != null) {
	    	    	sb.append(line);
	    	    	sb.append("\n");
	    	    }
	    	    if (data != null)
	    	    	wr.close();
	    	    rd.close();
	    	} catch (Exception e) {}
			return sb.toString();
		}
	}
}
