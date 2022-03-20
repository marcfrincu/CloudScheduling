package mosaic.scheduler.test;

import java.util.Vector;

import mosaic.scheduler.platform.Scaler;
import mosaic.scheduler.platform.com.json.beans.ComponentWorkflowBean;
import mosaic.scheduler.platform.com.json.beans.ComponentsBean;
import mosaic.scheduler.platform.com.json.beans.NodeListBean;
import mosaic.scheduler.platform.com.json.beans.QueueListBean;
import mosaic.scheduler.platform.com.json.beans.QueueListMessagesBean;
import mosaic.scheduler.platform.settings.SystemSettings;

import org.apache.log4j.PropertyConfigurator;

public class TestPlatformScaler {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("logging.properties");

		SystemSettings
				.getSystemSettings()
				.loadProperties(
						"mosaic/scheduler/platform/settings/system.properties.platform");

		Scaler s = new Scaler();

		Vector<ComponentWorkflowBean> componentWorkflow;
		Vector<NodeListBean> crtNodes;
		Vector<QueueListBean> queue;
		
		// component workflow
		componentWorkflow = new Vector<ComponentWorkflowBean>();
		ComponentWorkflowBean cwb = new ComponentWorkflowBean();
		cwb.setComponent_type(0);
		cwb.setLinked_to_Component(new String[]{"1","2"});
		cwb.setRead_rate(new String[]{"20", "20"});
		cwb.setWrite_rate(new String[]{"10", "10"});
		componentWorkflow.add(cwb);
		componentWorkflow.add(cwb);
		cwb = new ComponentWorkflowBean();
		cwb.setComponent_type(1);
		cwb.setLinked_to_Component(new String[]{"0","3"});
		cwb.setRead_rate(new String[]{"10", "10"});
		cwb.setWrite_rate(new String[]{"10", "5"});
		componentWorkflow.add(cwb);
		cwb = new ComponentWorkflowBean();
		cwb.setComponent_type(2);
		cwb.setLinked_to_Component(new String[]{"0","3"});
		cwb.setRead_rate(new String[]{"10", "10"});
		cwb.setWrite_rate(new String[]{"5", "5"});
		componentWorkflow.add(cwb);
		cwb = new ComponentWorkflowBean();
		cwb.setComponent_type(3);
		cwb.setLinked_to_Component(new String[]{"1", "2", "4"});
		cwb.setRead_rate(new String[]{"10", "5", "5"});
		cwb.setWrite_rate(new String[]{"5", "5", "10"});
		componentWorkflow.add(cwb);
		cwb = new ComponentWorkflowBean();
		cwb.setComponent_type(4);
		cwb.setLinked_to_Component(new String[]{"3"});
		cwb.setRead_rate(new String[]{"5"});
		cwb.setWrite_rate(new String[]{"5"});
		componentWorkflow.add(cwb);
		// node list
		crtNodes = new Vector<NodeListBean>();
		
		Vector<ComponentsBean> components = new Vector<ComponentsBean>();
		ComponentsBean cb = new ComponentsBean();
		cb = new ComponentsBean();
		cb.setComponent_type(0);
		cb.setComponent_number(1);
		cb.setPartition_index(0);
		components.add(cb);
		cb = new ComponentsBean();
		cb.setComponent_type(1);
		cb.setComponent_number(1);
		cb.setPartition_index(0);
		components.add(cb);
		cb = new ComponentsBean();
		cb.setComponent_type(2);
		cb.setComponent_number(1);
		cb.setPartition_index(0);
		components.add(cb);
		cb = new ComponentsBean();
		cb.setComponent_type(3);
		cb.setComponent_number(1);
		cb.setPartition_index(0);
		components.add(cb);
		cb = new ComponentsBean();
		cb.setComponent_type(4);
		cb.setComponent_number(1);
		cb.setPartition_index(0);
		components.add(cb);
		
		//Nodes and queues
		NodeListBean nlb = new NodeListBean();
		ComponentsBean[] csb = new ComponentsBean[components.size()];
		for (int i=0;i<components.size();i++) {
			csb[i] = components.get(i);
		}		
		nlb.setComponents(csb);
		nlb.setNode_id("1");
		crtNodes.add(nlb);
		queue = new Vector<QueueListBean>();
		QueueListBean clb = new QueueListBean();
		clb.setQueue_id("0-1");
		QueueListMessagesBean qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(40);
		qlmb.setThreeMin(40);
		qlmb.setFiveMin(40);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("0-2");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(60);
		qlmb.setThreeMin(60);
		qlmb.setFiveMin(60);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("1-0");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(20);
		qlmb.setThreeMin(20);
		qlmb.setFiveMin(20);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("1-3");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(20);
		qlmb.setThreeMin(20);
		qlmb.setFiveMin(20);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("2-0");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(20);
		qlmb.setThreeMin(20);
		qlmb.setFiveMin(20);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("2-3");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(20);
		qlmb.setThreeMin(20);
		qlmb.setFiveMin(10);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("3-1");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(10);
		qlmb.setThreeMin(10);
		qlmb.setFiveMin(10);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("3-2");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(5);
		qlmb.setThreeMin(5);
		qlmb.setFiveMin(5);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("3-4");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(5);
		qlmb.setThreeMin(5);
		qlmb.setFiveMin(5);
		clb.setNo_message(qlmb);
		queue.add(clb);
		clb = new QueueListBean();
		clb.setQueue_id("4-3");
		qlmb = new QueueListMessagesBean();
		qlmb.setOneMin(15);
		qlmb.setThreeMin(15);
		qlmb.setFiveMin(15);
		clb.setNo_message(qlmb);
		queue.add(clb);
		
		s.scale(componentWorkflow, queue, crtNodes);

	}
}
