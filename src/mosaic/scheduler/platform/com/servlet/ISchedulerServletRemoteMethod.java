package mosaic.scheduler.platform.com.servlet;

/**
 * Interface that holds methods to be invoked by a client that communicates with the scheduler. 
 * For the moment it is used only by the monitor that issues warnings that start the scheduler
 * @author Marc Frincu
 *
 */
public interface ISchedulerServletRemoteMethod {
	public boolean notifyScheduleProblem();
}
