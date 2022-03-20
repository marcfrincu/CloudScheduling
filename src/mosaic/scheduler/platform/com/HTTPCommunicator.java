package mosaic.scheduler.platform.com;

import java.net.URL;
import java.util.Vector;
import mosaic.scheduler.platform.com.json.beans.*;
import mosaic.scheduler.platform.settings.SystemSettings;

import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;

/**
 * This class should be used for HTTP connections
 * @author Marc Frincu
 *
 */
public class HTTPCommunicator implements ICommunicator {

	static HTTPCommunicator com = null;
	
	private HTTPCommunicator() throws Exception {
	}
	
	public static ICommunicator getCommunicator() throws Exception {
		if (HTTPCommunicator.com == null)
			HTTPCommunicator.com = new HTTPCommunicator();
		return HTTPCommunicator.com;
	}

	@Override
	public Vector<String> addNodes(int number, String availabilityZone,
			String cloudID) throws Exception {
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(
				new URL(SystemSettings.getSystemSettings().getMosaic_provisioner_url()));

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		IRemoteMethods calc = invoker.get(transport, "methods",
				IRemoteMethods.class);

		String[] result = calc.addNodes(10, "1", "1");
		Vector<String> ids = new Vector<String>();

		for (String r : result) {
			ids.add(r);
		}

		return ids;
	}

	@Override
	public boolean applySchedule(Vector<RequestNodeConfigValues> values)
			throws Exception {
		// TODO Auto-generated method stub
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(
				new URL(SystemSettings.getSystemSettings().getMosaic_provisioner_url()));

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		IRemoteMethods calc = invoker.get(transport, "methods",
				IRemoteMethods.class);
                RequestNodeConfigValues[] array = new RequestNodeConfigValues[values.size()];
                values.toArray(array);
		boolean result = calc.applySchedule(array);// MUST PUT IN VALUES!!!
		return result;
	}

	@Override
	public Vector<NodesBean> getAllNodes() throws Exception {
		// TODO Auto-generated method stub
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(
				new URL(SystemSettings.getSystemSettings().getMosaic_provisioner_url()));

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		IRemoteMethods calc = invoker.get(transport, "methods",
				IRemoteMethods.class);

		NodesBean[] response = calc.getAllNodes();
		Vector<NodesBean> result = new Vector<NodesBean>();
		for (NodesBean r : response) {
			result.add(r);
		}
		return result;
	}

	@Override
	public ResponseCompConnParams getComponentWorkflow() throws Exception {
		// TODO Auto-generated method stub
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(
				new URL(SystemSettings.getSystemSettings().getMosaic_provisioner_url()));

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		IRemoteMethods calc = invoker.get(transport, "methods",
				IRemoteMethods.class);

		ResponseCompConnParams result = calc.getComponentWorkflow();
		return result;
	}

	@Override
	public Vector<PartitionLoadListBean> getComponentsOnGivenNode(String nodeId)
			throws Exception {
		// TODO Auto-generated method stub
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(
				new URL(SystemSettings.getSystemSettings().getMosaic_provisioner_url()));

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		IRemoteMethods calc = invoker.get(transport, "methods",
				IRemoteMethods.class);

		PartitionLoadListBean[] response = calc
				.getComponentsOnGivenNode(nodeId);
		Vector<PartitionLoadListBean> result = new Vector<PartitionLoadListBean>();
		for (PartitionLoadListBean p : response) {
			result.add(p);
		}
		return result;
	}

	@Override
	public ResponseNrOfComponentsParams getPlatformData() throws Exception {
		// TODO Auto-generated method stub
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(
				new URL(SystemSettings.getSystemSettings().getMosaic_provisioner_url()));

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		IRemoteMethods calc = invoker.get(transport, "methods",
				IRemoteMethods.class);

		ResponseNrOfComponentsParams result = calc.getPlatformData();
		return result;
	}

	@Override
	public boolean removeNodes(int number, String availabilityZone,
			String cloudID, Vector<String> idsToBeRemoved) throws Exception {
		// TODO Auto-generated method stub
		HttpJsonRpcClientTransport transport = new HttpJsonRpcClientTransport(
				new URL(SystemSettings.getSystemSettings().getMosaic_provisioner_url()));

		JsonRpcInvoker invoker = new JsonRpcInvoker();
		IRemoteMethods calc = invoker.get(transport, "methods",
				IRemoteMethods.class);
                String[] array = new String[idsToBeRemoved.size()];
                idsToBeRemoved.toArray(array);
		boolean result = calc.removeNodes(number, availabilityZone, cloudID,
				array);
		return result;
	}

}