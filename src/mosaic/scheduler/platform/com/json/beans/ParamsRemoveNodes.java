package mosaic.scheduler.platform.com.json.beans;

/**
 *
 * @author Mirela
 */
public class ParamsRemoveNodes
{
 private int number;
 private String availability_zone_id;
 private String cloud_id;
 private String[] to_be_removed_nodes_ids = null;
 
        public int getNumber() {
		return number;
	}

	public String getAvailability_zone_id() {
		return availability_zone_id;
	}
        
        public String getCloud_id() {
		return cloud_id;
	}
        
        public String[] getTo_be_removed_nodes_ids()
        {
         return to_be_removed_nodes_ids;
        }
        
        public void setNumber(int x) {
		number = x;
	}

	public void setAvailability_zone_id(String s) {
		availability_zone_id = s;
	}

	public void setCloud_id(String s) {
		cloud_id = s;
	}
        
        public void setTo_be_removed_nodes_ids(String[] v)
        {
         to_be_removed_nodes_ids=v;
        }
}
