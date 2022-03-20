//package mosaic.scheduler.platform.com.servlet;
//
//import java.io.IOException;
//import java.util.Vector;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import mosaic.scheduler.servlet.ISchedulerServletRemoteMethod;
//import mosaic.scheduler.servlet.ISchedulerServletRemoteMethodImpl;
//import org.apache.log4j.PropertyConfigurator;
//import org.json.rpc.server.JsonRpcExecutor;
//import org.json.rpc.server.JsonRpcServletTransport;
//
///**
// *
// * @author balus.tudor
// */
//public class SchedulerServlet extends HttpServlet {
//private static Logger logger = Logger.getLogger(SchedulerServlet.class.getName());
//private final JsonRpcExecutor executor;
//private int nrOfNotifications=0;
//// a test provisioner servlet
//private String url = "http://localhost:8084/ProvisionerServlet/ProvisionerServlet";
//
//    public SchedulerServlet() throws Exception {
//        PropertyConfigurator.configure("logging.properties");
//        executor = bind();
//    }
//
//    private JsonRpcExecutor bind() throws Exception {
//        JsonRpcExecutor executor = new JsonRpcExecutor();
//
//        ISchedulerServletRemoteMethod notifierImpl = new ISchedulerServletRemoteMethodImpl();
//        executor.addHandler("scheduler", notifierImpl, ISchedulerServletRemoteMethod.class);
//
//        return executor;
//    }
//    
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//         executor.execute(new JsonRpcServletTransport(request, response));
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//         executor.execute(new JsonRpcServletTransport(request, response));
//         nrOfNotifications++;
//    }
//
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//}