package mosaic.scheduler.test.provisioner;



import mosaic.scheduler.platform.com.json.beans.NodesBean;
import mosaic.scheduler.platform.com.json.beans.PartitionLoadListBean;
import mosaic.scheduler.platform.com.json.beans.RequestNodeConfigValues;
import mosaic.scheduler.platform.com.json.beans.ResponseCompConnParams;
import mosaic.scheduler.platform.com.json.beans.ResponseNrOfComponentsParams;

/**
 * Interface for the remote methods called through JSON RPC. These are stored on the mosaic.provisioner.
 * @author Marc Frincu
 *
 */
public interface IRemoteMethods {
	
	public String[] addNodes(int number, String availabilityZone, String cloudID) throws Exception;
	public boolean removeNodes(int number, String availabilityZone, String cloudID, String[] tobeRemovedIds) throws Exception;
	public NodesBean[] getAllNodes() throws Exception;
	public PartitionLoadListBean[] getComponentsOnGivenNode(String nodeId) throws Exception;
	public boolean applySchedule(RequestNodeConfigValues[] values) throws Exception;
	public ResponseNrOfComponentsParams getPlatformData() throws Exception;
	public ResponseCompConnParams getComponentWorkflow() throws Exception;
        
       
}
