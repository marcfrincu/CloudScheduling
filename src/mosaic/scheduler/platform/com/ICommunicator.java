package mosaic.scheduler.platform.com;



import java.util.Vector;

import mosaic.scheduler.platform.com.json.beans.NodesBean;
import mosaic.scheduler.platform.com.json.beans.PartitionLoadListBean;
import mosaic.scheduler.platform.com.json.beans.RequestNodeConfigValues;
import mosaic.scheduler.platform.com.json.beans.ResponseCompConnParams;
import mosaic.scheduler.platform.com.json.beans.ResponseNrOfComponentsParams;

/**
 * Interface that wraps the JSON RPC calls issued from the scheduler
 * @author Marc Frincu
 *
 */
public interface ICommunicator {

	/**
	 * Method for adding nodes to a cloud
	 * @param number the number of nodes to be added
	 * @param availabilityZone the availability zone ID of the data center
	 * @param cloudID the cloud ID or URL
	 * @return the list of added node IDs
	 * @throws Exception
	 */
	public Vector<String> addNodes(int number, String availabilityZone, String cloudID) throws Exception;
	/**
	 * Method for removing nodes from a cloud
	 * @param number the number of nodes to be removed
	 * @param availabilityZone the 
	 * @param cloudID
	 * @param idsToBeRemoved the list of node IDs that need to be removed 
	 * @return true if the removal was successful, false otherwise
	 * @throws Exception
	 */
	public boolean removeNodes(int number, String availabilityZone, String cloudID, Vector<String> idsToBeRemoved) throws Exception;
	/**
	 * Method for retrieving the list of nodes
	 * @return the list of nodes
	 * @throws Exception
	 */
	public Vector<NodesBean> getAllNodes() throws Exception;
	/**
	 * Method for retrieving the partition list including components hosted on them from a given node
	 * @param nodeId the node ID
	 * @return the partition list
	 * @throws Exception
	 */
	public Vector<PartitionLoadListBean> getComponentsOnGivenNode(String nodeId) throws Exception;
	/**
	 * Method that requests the new schedule to be applied
	 * @param values the new schedule configuration
	 * @return true if the schedule was applied, false otherwise
	 * @throws Exception
	 */
	public boolean applySchedule(Vector<RequestNodeConfigValues> values) throws Exception;
	/**
	 * Method for retrieving the platform data, including the list of nodes and queues
	 * @return
	 * @throws Exception
	 */
	public ResponseNrOfComponentsParams getPlatformData() throws Exception;
	/**
	 * Method for retrieving information on the component workflow, including component type, linkage to other types, read/write rate
	 * @return
	 * @throws Exception
	 */
	public ResponseCompConnParams getComponentWorkflow() throws Exception;
}