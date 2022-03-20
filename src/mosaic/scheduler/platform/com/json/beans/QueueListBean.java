package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class QueueListBean {
    private String queue_id;
    private QueueListMessagesBean no_messages;

        public void setQueue_id(String s)
        {
         queue_id=s;
        }
        
        public void setNo_message(QueueListMessagesBean x)
        {
         no_messages=x;
        }

	public String getQueue_id() {
		return queue_id;
	}

	public QueueListMessagesBean getNo_messages() {
		return no_messages;
	}

}
