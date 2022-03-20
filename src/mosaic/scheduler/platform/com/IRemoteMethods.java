package mosaic.scheduler.platform.com;

import mosaic.scheduler.platform.com.json.beans.*;

/**
 * Interface for the remote methods called through JSON RPC. These are implemented by the mosaic.provisioner and called by the scheduler
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
