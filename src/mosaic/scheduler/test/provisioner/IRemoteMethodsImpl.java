package mosaic.scheduler.test.provisioner;


import mosaic.scheduler.platform.com.IRemoteMethods;
import mosaic.scheduler.platform.com.json.beans.ComponentLoadListMinutesBean;
import mosaic.scheduler.platform.com.json.beans.ComponentsBean;
import mosaic.scheduler.platform.com.json.beans.NodeListBean;
import mosaic.scheduler.platform.com.json.beans.NodesBean;
import mosaic.scheduler.platform.com.json.beans.NodesLoadBean;
import mosaic.scheduler.platform.com.json.beans.PartitionLoadListBean;
import mosaic.scheduler.platform.com.json.beans.QueueListBean;
import mosaic.scheduler.platform.com.json.beans.QueueListMessagesBean;
import mosaic.scheduler.platform.com.json.beans.RequestNodeConfigValues;
import mosaic.scheduler.platform.com.json.beans.ResponseCompConnParams;
import mosaic.scheduler.platform.com.json.beans.ResponseNrOfComponentsParams;
/**
 *
 * @author balus.tudor
 */
public class IRemoteMethodsImpl implements IRemoteMethods {
  
    @Override
    public String[] addNodes(int number, String availabilityZone, String cloudID) throws Exception{
       //TODO: create nodes & grab their IDs
        String[] test = new String[]{"1","2"};
        return test;
    }

    @Override
    public boolean removeNodes(int number, String availabilityZone, String cloudID, String[] tobeRemovedIds) throws Exception{
      
      return true;
    }

    @Override
    public NodesBean[] getAllNodes() throws Exception{
      //SOME RANDOM VALUES FOR TESTS
      NodesBean[] test = new NodesBean[2];
      NodesBean n1= new NodesBean();
      NodesLoadBean load = new NodesLoadBean();
      load.setOneMin(1);
      load.setThreeMin(3);
      load.setFiveMin(5);
      NodesBean n2= new NodesBean();
      NodesLoadBean load1 = new NodesLoadBean();
      load1.setOneMin(2);
      load1.setThreeMin(4);
      load1.setFiveMin(6);
      
      n1.setNode_cloud_id("1");
      n1.setNode_datacenter_id("2");
      n1.setNode_id("123");
      n1.setNode_load(load);
      
      n2.setNode_cloud_id("2");
      n2.setNode_datacenter_id("1");
      n2.setNode_id("345");
      n2.setNode_load(load1);
      
      test[0]=n1;
      test[1]=n2;
     
      //END OF RANDOM VALUES FOR TEST
      
      return test;
    }

    @Override
    public PartitionLoadListBean[] getComponentsOnGivenNode(String nodeId) throws Exception{
      //SOME RANDOM VALUES FOR TESTS
      PartitionLoadListBean[] test = new PartitionLoadListBean[2];
      PartitionLoadListBean p1 = new PartitionLoadListBean();
      PartitionLoadListBean p2 = new PartitionLoadListBean();
      ComponentLoadListMinutesBean[] comp = new ComponentLoadListMinutesBean[2];
      ComponentLoadListMinutesBean comp1= new ComponentLoadListMinutesBean();
      ComponentLoadListMinutesBean comp2= new ComponentLoadListMinutesBean();
      comp1.setOneMin(1);
      comp1.setThreeMin(2);
      comp1.setFiveMin(5);
      comp2.setOneMin(2);
      comp2.setThreeMin(3);
      comp2.setFiveMin(6);
      comp[0]=comp1;
      comp[1]=comp2;
      
      p1.setComponent_type(1);
      p1.setNumber_components(1);
      p1.setPartition_index(1);
      p1.setComponent_load(comp[0]);
      p2.setComponent_type(2);
      p2.setNumber_components(2);
      p2.setPartition_index(2);
      p2.setComponent_load(comp[1]);
      
      test[0]=p1;
      test[1]=p2;
      //END OF RANDOM VALUES FOR TEST
      
      return test;
    }

    @Override
    public boolean applySchedule(RequestNodeConfigValues[] values) throws Exception{

      return true;
    }

    @Override
    public ResponseNrOfComponentsParams getPlatformData() throws Exception{
      //TODO process platform data
      //SOME RANDOM VALUES FOR TESTS
      ResponseNrOfComponentsParams test = new ResponseNrOfComponentsParams();
        NodeListBean[] nodeBean = new NodeListBean[2];
            ComponentsBean[] compBean = new ComponentsBean[2];
                ComponentsBean compBean1 = new ComponentsBean();
                ComponentsBean compBean2 = new ComponentsBean();
                compBean1.setComponent_number(1);
                compBean1.setComponent_type(1);
                compBean1.setPartition_index(1);
                compBean2.setComponent_number(2);
                compBean2.setComponent_type(2);
                compBean2.setPartition_index(2);
            compBean[0]=compBean1;
            compBean[1]=compBean2;
            ComponentsBean[] compBean3 = new ComponentsBean[2];
                ComponentsBean compBean4 = new ComponentsBean();
                ComponentsBean compBean5 = new ComponentsBean();
                compBean4.setComponent_number(3);
                compBean4.setComponent_type(3);
                compBean4.setPartition_index(3);
                compBean5.setComponent_number(4);
                compBean5.setComponent_type(4);
                compBean5.setPartition_index(4);
            compBean3[0]=compBean4;
            compBean3[1]=compBean5;
        NodeListBean nodeBean1 = new NodeListBean();
        nodeBean1.setNode_id("1");
        nodeBean1.setComponents(compBean);
        NodeListBean nodeBean2 = new NodeListBean();
        nodeBean2.setNode_id("2");
        nodeBean2.setComponents(compBean3);
        nodeBean[0]=nodeBean1;
        nodeBean[1]=nodeBean2;
        
        QueueListBean[] queue = new QueueListBean[1];
        QueueListBean qu = new QueueListBean();
            QueueListMessagesBean message = new QueueListMessagesBean();
                message.setOneMin(1);
                message.setThreeMin(3);
                message.setFiveMin(5);
        qu.setQueue_id("1");
        qu.setNo_message(message);
        queue[0]=qu;
        
      test.setNode_list(nodeBean);
      test.setQueue_list(queue);
      //END OF RANDOM VALUES FOR TEST
  
      return test;
    }

    @Override
    public ResponseCompConnParams getComponentWorkflow() throws Exception{

      ResponseCompConnParams test = new ResponseCompConnParams();
      return test;
    }
    
}
