package mosaic.scheduler.platform.settings;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * Class handling the <i>system.properties</i> file for the scheduling simulator
 * 
 * @author Marc Frincu
 * @since 2012
 */
public class SystemSettings {

	public static enum WS_GENERATION_METHOD {POLYNOMIAL, PARETO, WEIBULL};
	public static enum NODE_TYPE {UNIFORM, UNRELATED, HOMOGENEOUS, CUSTOM}; 

	/**
	 * The keys of the properties in the system.properties file.
	 */
	private enum PropertyKeys {
		mosaic_provisioner_url,
		can_be_overriden,
		number_clouds,
		number_datacenters_per_cloud,
		
		max_number_nodes,
		node_type,
		node_cpu_weight,
		node_memory_weight,
		node_network_weight,
		node_variation,
		max_node_load_threshold,
		min_node_load_threshold,
		
		no_component_types,
		component_read_rate,
		component_write_rate,
		component_connection_table,
		
		time_span,
		web_server_generation_method
	};

	private int[] component_read_rate, 
				component_write_rate;
	
	private String[] web_server_generation_method_argument_list;

	private int[][]	component_connection_table; 
	private double[][] node_variation;
	
	private WS_GENERATION_METHOD web_server_generation_method;
	private NODE_TYPE node_type;
	
	private String mosaic_provisioner_url;
	
	private boolean can_be_overriden;
	
	private int max_number_nodes,
			max_node_load_threshold,
			min_node_load_threshold,
			no_component_types,
			time_span,
			number_clouds,
			number_datacenters_per_cloud;
	private double	node_cpu_weight, 
		node_memory_weight, 
		node_network_weight; 
			
	
	/**
	 * The system settings object.
	 */
	private static SystemSettings settings = null;

	/**
	 * Private constructor.
	 */
	private SystemSettings() {
	}

	/**
	 * Returns the system settings object.
	 * <p>
	 * 
	 * @return the system settings object
	 */
	public static SystemSettings getSystemSettings() {
		if (SystemSettings.settings == null) {
			SystemSettings.settings = new SystemSettings();
		}
		return SystemSettings.settings;
	}
	
	/**
	 * This method loads the properties from the property file
	 * "system.properties" previously added to the class path either explicit or
	 * implicit by being part of a .jar file added to the CLASSPATH.
	 * <p>
	 * 
	 * @param propertiesFilePath
	 *            path to properties file
	 * @throws Exception 
	 */
	public void loadProperties(String propertiesFilePath) throws Exception {

		Logger logger = Logger.getLogger(SystemSettings.class.getPackage()
				.getName());
		
		Properties props = new Properties();

		URL url = SystemSettings.class.getClassLoader().getResource(
				propertiesFilePath);

		if (url == null) {
			throw new MissingResourceException(
					"Unable to load the properties file." + " File not found: "
							+ url, null, null);
		}
		try {
			props.load(url.openStream());
		} catch (IOException e) {
			throw new MissingResourceException(
					"The properties file cannot be accessed.", null, null);
		}

		validateFile(props);

		// read the properties
		this.can_be_overriden = Boolean.parseBoolean(props.getProperty(PropertyKeys.can_be_overriden.toString().trim()));
		this.mosaic_provisioner_url = props.getProperty(PropertyKeys.mosaic_provisioner_url.toString().trim());
		this.number_clouds = Integer.parseInt(props.getProperty(PropertyKeys.number_clouds.toString()).trim());
		this.number_datacenters_per_cloud = Integer.parseInt(props.getProperty(PropertyKeys.number_datacenters_per_cloud.toString()).trim());		
		this.max_number_nodes = Integer.parseInt(props.getProperty(PropertyKeys.max_number_nodes.toString()).trim());
		this.no_component_types = Integer.parseInt(props.getProperty(PropertyKeys.no_component_types.toString()).trim());
		this.max_node_load_threshold = Integer.parseInt(props.getProperty(PropertyKeys.max_node_load_threshold.toString()).trim());
		this.min_node_load_threshold = Integer.parseInt(props.getProperty(PropertyKeys.min_node_load_threshold.toString()).trim());
		this.node_cpu_weight = Double.parseDouble(props.getProperty(PropertyKeys.node_cpu_weight.toString()).trim());
		this.node_memory_weight = Double.parseDouble(props.getProperty(PropertyKeys.node_memory_weight.toString()).trim());
		this.node_network_weight = Double.parseDouble(props.getProperty(PropertyKeys.node_network_weight.toString()).trim());
		try {
			this.time_span = Integer.parseInt(props.getProperty(PropertyKeys.time_span.toString()));
		}
		catch (NumberFormatException nfe) {
			if (props.getProperty(PropertyKeys.time_span.toString()).trim().toLowerCase().compareTo("infinity") == 0) {
				this.time_span = Integer.MAX_VALUE;
			}
			else {
				logger.debug(props.getProperty(PropertyKeys.time_span.toString()).trim().toLowerCase());
				logger.error("Value in time_span must be integer value or \"infinity\" string");
				System.exit(0);
			}
				
		}
		
		this.component_read_rate = new int[this.no_component_types];
		int i = 0;
		for (String s : props.getProperty(PropertyKeys.component_read_rate.toString()).split(",")) {
			try {
				this.component_read_rate[i++] = Integer.parseInt(s.trim());
			}
			catch(NumberFormatException nfe) {
				logger.error("Value in component_read_rate not number");
				System.exit(0);
			}
		}
		this.component_write_rate = new int[this.no_component_types];
		i = 0;
		for (String s : props.getProperty(PropertyKeys.component_write_rate.toString()).split(",")){
			try {
				this.component_write_rate[i++] = Integer.parseInt(s.trim());
			}
			catch(NumberFormatException nfe) {
				logger.error("Value in component_write_rate not number");
				System.exit(0);
			}
		}		
		
		String line[] = props.getProperty(PropertyKeys.component_connection_table.toString()).split(";");
		this.component_connection_table = new int[this.no_component_types][this.no_component_types];
		i = 0;
		int j = 0;
		for (String l : line) {
			j = 0;
			for (String s : l.split(","))
				this.component_connection_table[i][j++] = Integer.parseInt(s.trim());
			i++;
		}
		
		line = props.getProperty(PropertyKeys.web_server_generation_method.toString()).split(";");
				
		try {
			this.web_server_generation_method = SystemSettings.WS_GENERATION_METHOD.valueOf(line[0].split(";")[0].trim());
		} catch (IllegalArgumentException iae) {
			logger.error("Invalid web_server_generation_method. Value needs to be one of: POLYNOMIAL, WEIBULL, PARETO");
			System.exit(0);
		}

		
		
		// validate values
		if (this.max_number_nodes < -1) {
			logger.warn("Invalid value max_number_nodes. Must be >= -1");
		}
		
		if (this.component_read_rate.length != this.no_component_types || this.component_read_rate.length != this.no_component_types) {
			logger.error("Invalid value component_read_rate=no_component_types and component_write_rate=no_component_types");
			System.exit(0);
		}
		
		if (this.max_node_load_threshold < 0 || this.max_node_load_threshold > 100)
			logger.warn("Invalid value node_load_threshold. Must be in 0, 100 range");
		
		if ((this.node_cpu_weight + this.node_network_weight + this.node_memory_weight -1) > 0.0001)
			logger.warn("node_cpu_weight + node_network_weight + node_memory_weight must be 1");
		
		if (this.component_connection_table.length != this.no_component_types) {
			logger.error("Invalid component_connection_table size. Number of lines must be equal to no_component_types");
			System.exit(0);
		}
		
		for (int[] s : this.component_connection_table) {
			if (s.length != this.no_component_types) {
				logger.error("Invalid component_connection_table size. Number of columns must be equal to no_component_types");
				System.exit(0);
			}
		}
		
		if (this.web_server_generation_method != WS_GENERATION_METHOD.POLYNOMIAL) {
			this.web_server_generation_method_argument_list = line[1].split(",");
		
			for (String s : this.web_server_generation_method_argument_list) {
				try {
					Double.parseDouble(s);
				}
				catch (NumberFormatException nfe) {
					logger.error("Invalid web_server_generation_method_argument_list. Values need to be doubles or integers");
					System.exit(0);
				}
			}
		}
		
		try {
			this.node_type = SystemSettings.NODE_TYPE.valueOf(props.getProperty(PropertyKeys.node_type.toString()));			
		} catch (IllegalArgumentException iae) {
			logger.error("Invalid node_type. Value needs to be one of: UNIFORM, UNRELATED, HOMOGENEOUS");
			System.exit(0);
		}
		
		String[] node_variation_string = props.getProperty(PropertyKeys.node_variation.toString()).split(";");
		
		if (node_variation_string.length == 1) {
			// check if it's uniform, homogeneous or will be given by user in the application
			try {
				int n = Integer.parseInt(node_variation_string[0].trim());
				if (n != -1 && n != -2) {
					logger.warn("Invalid node_variation. Only -1 (HOMOGENEOUS NODES) and -2 (CUSTOM NODES) are accepted as correct numerical values");
				}
					
				
			}
			catch (NumberFormatException nfe) {
				// uniform nodes
				if (this.node_type == SystemSettings.NODE_TYPE.HOMOGENEOUS) { 
					logger.error("For given node_variation node_type should not be HOMOGENEOUS");
					System.exit(0);
				}						
				this.node_variation = new double[1][this.max_number_nodes];
				i = 0;
				for (String s : node_variation_string[0].split(",")) {
					if (i>=this.max_number_nodes)
						break;
					try {
						this.node_variation[0][i++] = Double.parseDouble(s.trim());
					} 
					catch (NumberFormatException nfe1) {
						logger.error("Invalid node_variation. Values need to be doubles or integers");
						System.exit(0);
					}						
				}
				if (i != this.max_number_nodes) 
					logger.error("Elements in node_variation not equal with max_number_nodes");
			}
		}
		else 			
			if (node_variation_string.length == this.max_number_nodes) { // unrelated
				this.node_variation = new double[this.max_number_nodes][this.no_component_types];
				for (i=0; j<node_variation_string.length; j++) {
					j = 0;
					for (String s : node_variation_string[i].split(",")) {
						try {
							this.node_variation[i][j++] = Double.parseDouble(s.trim());
						}
						catch (NumberFormatException nfe) {
							logger.error("Invalid node_variation elements. Values need to be doubles or integers");
							System.exit(0);
						}
					}
					if (j != this.no_component_types) {
						logger.error("Invalid node_variation line length. Values need to be equal to node_variation");
						System.exit(0);
					}
				}
			}
			else {
				logger.error("Lines in node_variation need to be equal with max_number_nodes");
				System.exit(0);
			}
		
	}

	/**
	 * Logs an error message and throws a MissingResourceException.
	 * <p>
	 * 
	 * @param mssg
	 *            the message
	 * @param e
	 *            any exception that may have caused the error
	 */
	private void error(String mssg, Exception e) {
		Logger logger = Logger.getLogger(SystemSettings.class.getPackage()
				.getName());

		logger.error(mssg, e);
		throw new MissingResourceException(mssg, null, null);
	}

	/**
	 * Validates the properties file.
	 * <p>
	 * 
	 * @param props
	 *            the properties object
	 * @return <code>true</code> if all entries are valid, <code>false</code>
	 *         otherwise
	 */
	private boolean validateFile(Properties props) {
		String loc = SystemSettings.class.getSimpleName()
				+ ".validateFile() - ";

		String keyName, keyValue;

		PropertyKeys[] properties = PropertyKeys.values();
		for (PropertyKeys pk : properties) {
			keyName = pk.name();
			keyValue = props.getProperty(keyName);
			if (((keyValue == null) || (keyValue.trim().compareTo("") == 0))) {
				error(loc + "Missing or illegal value in settings file"
						+ " for key: " + keyName, null);
				return false;
			}
		}
		return true;
	}

	public int[] getComponent_read_rate() {
		return component_read_rate;
	}

	public int[] getComponent_write_rate() {
		return component_write_rate;
	}

	public String[] getWeb_server_generation_method_argument_list() {
		return web_server_generation_method_argument_list;
	}

	public int[][] getComponent_connection_table() {
		return component_connection_table;
	}

	public double[][] getNode_variation() {
		return node_variation;
	}

	public WS_GENERATION_METHOD getWeb_server_generation_method() {
		return web_server_generation_method;
	}

	public int getMax_number_nodes() {
		return max_number_nodes;
	}

	public NODE_TYPE getNode_type() {
		return node_type;
	}

	public double getNode_cpu_weight() {
		return node_cpu_weight;
	}

	public double getNode_memory_weight() {
		return node_memory_weight;
	}

	public double getNode_network_weight() {
		return node_network_weight;
	}

	public double getMax_node_load_threshold() {
		return max_node_load_threshold;
	}
	
	public double getMin_node_load_threshold() {
		return min_node_load_threshold;
	}

	public int getNo_component_types() {
		return no_component_types;
	}

	public int getTime_span() {
		return time_span;
	}	
	
	public int getNumber_clouds() {
		return number_clouds;
	}

	public int getNumber_datacenters_per_cloud() {
		return number_datacenters_per_cloud;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		throw new UnsupportedOperationException();
	}

	public String getMosaic_provisioner_url() {
		return mosaic_provisioner_url;
	}

	public boolean isCan_be_overriden() {
		return can_be_overriden;
	}

	public void setComponent_read_rate(int[] componentReadRate) {
		if (can_be_overriden)
			component_read_rate = componentReadRate;
	}

	public void setComponent_write_rate(int[] componentWriteRate) {
		if (can_be_overriden)
			component_write_rate = componentWriteRate;
	}

	public void setComponent_connection_table(int[][] componentConnectionTable) {
		if (can_be_overriden)
			component_connection_table = componentConnectionTable;
	}

	public void setNo_component_types(int noComponentTypes) {
		if (can_be_overriden)
			no_component_types = noComponentTypes;
	}
	
	
	
}