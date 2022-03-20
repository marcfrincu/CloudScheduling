package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ResponseNrOfComponentsParams {
	private NodeListBean[] node_list;
	private QueueListBean[] queue_list;
	
	public NodeListBean[] getNode_list() {
		return node_list;
	}
	public void setNode_list(NodeListBean[] nodeList) {
		node_list = nodeList;
	}
	public QueueListBean[] getQueue_list() {
		return queue_list;
	}
	public void setQueue_list(QueueListBean[] queueList) {
		queue_list = queueList;
	}
	
	
}
