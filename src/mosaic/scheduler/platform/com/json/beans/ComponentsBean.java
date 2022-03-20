package mosaic.scheduler.platform.com.json.beans;

/**
 * 
 * @author balus.tudor
 */
public class ComponentsBean {
	private int component_type;
	private int component_number;
	private int partition_index;

        public void setComponent_type(int x)
        {
         component_type=x;
        }
        
        public void setComponent_number(int x)
        {
         component_number=x;
        }
         
        public void setPartition_index(int x)
        {
         partition_index=x;
        }
        
	public int getComponent_type() {
		return component_type;
	}

	public int getComponent_number() {
		return component_number;
	}

	public int getPartition_index() {
		return partition_index;
	}
}