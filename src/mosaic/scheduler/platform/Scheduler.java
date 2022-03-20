package mosaic.scheduler.platform;

import java.util.Vector;

import mosaic.scheduler.platform.algorithms.IAlgorithm;
import mosaic.scheduler.platform.algorithms.util.AlgorithmUtil;
import mosaic.scheduler.platform.com.HTTPCommunicator;
import mosaic.scheduler.platform.com.json.beans.ComponentWorkflowBean;
import mosaic.scheduler.platform.com.json.beans.NodesBean;
import mosaic.scheduler.platform.com.json.beans.PartitionLoadListBean;
import mosaic.scheduler.platform.com.json.beans.RequestNodeConfigValues;
import mosaic.scheduler.platform.resources.Component;
import mosaic.scheduler.platform.resources.ComponentRequirementsList;
import mosaic.scheduler.platform.resources.ComponentRequirementsPlatform;
import mosaic.scheduler.platform.resources.Node;
import mosaic.scheduler.platform.resources.Partition;
import mosaic.scheduler.platform.settings.SystemSettings;
import mosaic.scheduler.simulator.util.Runner;

import org.apache.log4j.Logger;

/**
 * This class represents the core of the scheduling module of the mOSAIC platform
 * @author Marc Frincu
 *
 */
public class Scheduler {
	private static Logger logger = Logger.getLogger(Runner.class.getPackage().getName());

	Vector<ComponentWorkflowBean> componentWorkflow = null;
	Vector<NodesBean> crtNodes;
	Vector<PartitionLoadListBean> crtComponents;
	Runner.AlgorithmContainer container = null;
	private boolean isProactive = false;
	
	/**
	 * Default constructor
	 * @param algorithm the algorithm class used for scheduling
	 * @param isProactive true if the scheduler is used as a stand alone module, false if started as a service from a web page through an HTTP request 
	 */
	public Scheduler(IAlgorithm algorithm, boolean isProactive) {
		this.container = new Runner(). new AlgorithmContainer(algorithm, new Vector<Node>());

		//grab component workflow
		try {                   
			ComponentWorkflowBean[] cwbl = HTTPCommunicator.getCommunicator().getComponentWorkflow().getComponent_workflow();
			this.componentWorkflow = new Vector<ComponentWorkflowBean>();
			for (ComponentWorkflowBean cwb : cwbl)
				this.componentWorkflow.add(cwb);
		} catch (Exception e) {
			Scheduler.logger.error("Error when retrieving component workflow: " + e.getMessage());
                        e.printStackTrace();
			return;
		}
		if (this.componentWorkflow == null) {
			Scheduler.logger.info("Component workflow not retrieved: null");
			return;
		}
		
		try {
			//override the number of component types, the read/write rate and the connection table
			SystemSettings.getSystemSettings().setNo_component_types(this.componentWorkflow.size()+1);//+1 if component type 0 does NOT exist else eliminate it
			
			ComponentRequirementsList crl  = ComponentRequirementsList.getComponentsRequirement();
			for (int ct=0; ct<SystemSettings.getSystemSettings().getNo_component_types(); ct++) {
				crl.addComponentRequirement(ct, new ComponentRequirementsPlatform());
			}
			
			int[] readRate = new int[SystemSettings.getSystemSettings().getNo_component_types()];
			int[] writeRate = new int[SystemSettings.getSystemSettings().getNo_component_types()];
			int[][] connectionTable = new int[SystemSettings.getSystemSettings().getNo_component_types()][SystemSettings.getSystemSettings().getNo_component_types()];
			for (ComponentWorkflowBean cwb : this.componentWorkflow) {
				//TODO fix in the future: compute average read/write rate not just take the 1st value
				readRate[cwb.getComponent_type()] = Integer.parseInt(cwb.getRead_rate()[0]);
				writeRate[cwb.getComponent_type()] = Integer.parseInt(cwb.getWrite_rate()[0]);
				for (String id : cwb.getLinked_to_Component()) {
						if (id.trim().length() > 0)
							connectionTable[cwb.getComponent_type()][Integer.parseInt(id.trim())] = 1;
				}				
			}
			//update the connection table in system settings
			SystemSettings.getSystemSettings().setComponent_connection_table(connectionTable);
		} catch(Exception e) {
			Scheduler.logger.error("Error when updating the component workflow properties (number of components, read/write rate and connection table): " + e.getMessage());
                        e.printStackTrace();
			return;			
		}
	}
 	
	/**
	 * Method for executing the scheduling policy. This runs indefinitely if the <i>isProactive</i> argument of the constructor is set to true
	 * @throws Exception 
	 */
	public void run() {
		Component component = null;
		Partition partition = null;
		Node node = null;
		
		/*
		 * hack to be able to assign partitions properly by using the partition-index from the JSON
		 * we create all nodes on a single dummy node and then when we have a new node and partition in the JSON we 
		 * move a partition with it's index from the dummy node to it
		 */
		
		Node dummyNode = new Node("dummyDC", "dummyC1");
		for (int i=0; i<SystemSettings.getSystemSettings().getNo_component_types(); i++) {
			//each component type has assigned to it a number of partitions equal with the total number of nodes that can be added
			for (int j=0; j<SystemSettings.getSystemSettings().getMax_number_nodes(); j++) {
				dummyNode.addPartition(Partition.provide(dummyNode.getID()));
			}
		}
		Scheduler.logger.debug("Created " + 
				SystemSettings.getSystemSettings().getNo_component_types() * SystemSettings.getSystemSettings().getMax_number_nodes() + 
				" dummy partitions on our dummy node");
		
		do {			
			//TODO scale components based on traffic at each of the component
			//NOTE: for now we do not scale in mOSAIC
				
			this.container.nodes.clear();
			//grab node list
			try {
				this.crtNodes = HTTPCommunicator.getCommunicator().getAllNodes();
			} catch (Exception e) {
				Scheduler.logger.error("Error when retrieving allocated nodes: " + e.getMessage());
				return;
			}
			if (this.crtNodes == null) {
				Scheduler.logger.error("No nodes retrieved: null");
				return;
			}
			Scheduler.logger.info("Grabed " + this.crtNodes.size() + " nodes for scheduling");
			
			for (NodesBean nb : this.crtNodes) {
				//get node's component list
				try {
					this.crtComponents = HTTPCommunicator.getCommunicator().getComponentsOnGivenNode(nb.getNode_id());
				} catch (Exception e) {
					Scheduler.logger.error("Error when retrieving component's for node: " + nb.getNode_id() + " : " + e.getMessage());
					return;
				}
				if (this.crtComponents == null) {
					Scheduler.logger.error("No components for node retrieved: null");
					continue;
				}
				Scheduler.logger.info("Grabed " + this.crtComponents.size() + " partitions for node with ID: " + nb.getNode_id());
				
				//compute the average load of every component type based on input data
				//TODO the number of components to be set in case it is permitted form the settings file
				double[] averageComponentLoad = new double[SystemSettings.getSystemSettings().getNo_component_types()];
				double[] noComponentsPerType =  new double[SystemSettings.getSystemSettings().getNo_component_types()];
				for (PartitionLoadListBean p : this.crtComponents) {
					averageComponentLoad[p.getComponent_type()] += p.getComponent_load().getOneMin();
					noComponentsPerType[p.getComponent_type()]++;
				}
								
				for (int i=0; i<averageComponentLoad.length; i++) {
					averageComponentLoad[i] = (noComponentsPerType[i] != 0) ? averageComponentLoad[i]/noComponentsPerType[i] : 0;
					//we set the time to 0 since we are only interested for 'this' moment
					ComponentRequirementsList.getComponentsRequirement().getComponentRequirements(i).setCpuUsage(0, averageComponentLoad[i]);
				}
				
				//add node and components attached to it			
				node = new Node(nb.getNode_id(), nb.getNode_datacenter_id(), nb.getNode_cloud_id());
				Scheduler.logger.info("Added new node with ID: " + node.getID());
				Scheduler.logger.info("Assigning partitions it...");
				for (PartitionLoadListBean plbl : crtComponents) {
					//create partition by relocating the one with the correct index from the dummy node
					partition = dummyNode.getPartition(plbl.getPartition_index());
					Scheduler.logger.debug("Grabbed partition with index: " + 
							plbl.getPartition_index() + " from dummy node to relocate on real node with ID: " + nb.getNode_id());
					
					dummyNode.removePartition(partition);
					partition.assignToNode(node.getID());
					
					Scheduler.logger.info("Assigned partition to node with ID: " + node.getID());
					//generate components
					Scheduler.logger.info("Assigning components to partition...");
					for (int i=0; i<plbl.getNumber_components(); i++) {
						component = new Component(false, plbl.getComponent_type());
						partition.addComponent(component);
						Scheduler.logger.info("Added component of type : " + 
								plbl.getComponent_type() + " to partition with index: " + plbl.getPartition_index());
					}
					node.addPartition(partition);					
				}
				
				this.container.nodes.add(node);
				Scheduler.logger.debug("Added node with ID: " + node.getID() + " to node list");
			}
	
			//add connections to components based on the input data
			for (Component c : AlgorithmUtil.gatherComponents(this.container.nodes)) {		
				for (ComponentWorkflowBean cwb : this.componentWorkflow) {
					if (cwb.getComponent_type() == c.getType()) {
						for (String id : cwb.getLinked_to_Component()) {
							if (id.trim().length() > 0)
								c.addConnection(AlgorithmUtil.getRandomComponent(Integer.parseInt(id), this.container.nodes));									
						}					
					}
				}
			}
	
			Scheduler.logger.info("Running algorithm...");
			//run the algorithm
			//this.container.nodes = this.container.algorithm.executeOnce(this.container.nodes, new int[]{1,7,19},0);/*use this if scaling is enabled*/
			this.container.nodes = this.container.algorithm.executeOnce(this.container.nodes, 0);/*use this if scaling is disabled*/
			Scheduler.logger.info("Algorithm executed");

			//compute the number of nodes to be added or removed
			int countAdd = 0, countRemove = 0;
			Vector<String> idsToBeRemoved = new Vector<String>();
			for (Node n : this.container.nodes) {
				if (n.status == Node.NODE_STATUS.NEW)
					countAdd++;
				if (n.status == Node.NODE_STATUS.TO_BE_REMOVED) {
					countRemove++;
					idsToBeRemoved.add(n.getID());
				}
			}
				
			Scheduler.logger.debug("Nodes to be added : " + countAdd);
			Scheduler.logger.debug("Nodes to be removed : " + countRemove);
			
			//TODO fix this in the future: for now assume a single cloud/availability zone
			try {
				HTTPCommunicator.getCommunicator().addNodes(countAdd, "1", "1");
			} catch (Exception e1) {
				Scheduler.logger.error("Failed to transmit message for adding new nodes");
			}
			try {
				HTTPCommunicator.getCommunicator().removeNodes(countRemove, "1", "1", idsToBeRemoved);
			} catch (Exception e1) {
				Scheduler.logger.error("Failed to transmit message for removing new nodes");
                                e1.printStackTrace();
			}
			
			Scheduler.logger.debug("Creating schedule message");			
			Vector<RequestNodeConfigValues> schedule = new Vector<RequestNodeConfigValues>();
            RequestNodeConfigValues rncv = null;
            Partition p = null;
            //TODO the message creation for the schedule has NOT been tested
            Vector<Partition> parts = AlgorithmUtil.gatherPartitions(this.container.nodes);              
            for (int i=0;i<parts.size(); i++) {
            	//we generate MAX_NODES * NO_COMPONENT_TYPES partitions
                //so if we have MAX_NODES we end up with one partition per COMPONENT_TYPE
                //COMPONENT_TYPE=0 starts from 0 end goes until MAX_NODES-1
                //COMPONENT_TYPE=1 starts from MAX_NODES and goes until 2*MAX_NODES-1
                p = parts.get(i);
                rncv = new RequestNodeConfigValues();
                rncv.setNode_id(p.getAssignedNodeID());
                rncv.setPartition_index(i);
                rncv.setNumber_components(p.getAssignedComponents().size());
   	            rncv.setComponent_type(i / SystemSettings.getSystemSettings().getMax_number_nodes());
           }
           try {
        	   HTTPCommunicator.getCommunicator().applySchedule(schedule);
           } catch (Exception ex) {
        	   Scheduler.logger.error("Failed to transmit schedule: " + ex.getMessage());                
           }
                        
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) { }
		} while (this.isProactive);
	}
}
