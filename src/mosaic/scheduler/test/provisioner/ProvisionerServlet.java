//package mosaic.scheduler.test.provisioner;
//
//
//import java.io.IOException;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import mosaic.scheduler.platform.com.IRemoteMethods;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//import org.json.rpc.server.JsonRpcExecutor;
//import org.json.rpc.server.JsonRpcServletTransport;
//
///**
// *
// * @author balus.tudor
// */
///*public class ProvisionerServlet extends HttpServlet {
//private static Logger logger = Logger.getLogger(ProvisionerServlet.class.getName());
//private final JsonRpcExecutor executor;
//
//    public ProvisionerServlet() {
//        PropertyConfigurator.configure("logging.properties");
//        executor = bind();
//    }
//
//    private JsonRpcExecutor bind() {
//        JsonRpcExecutor executor = new JsonRpcExecutor();
//        
//        logger.info("Reached the provisioner, now calculating what result to send back. ");
//        IRemoteMethods decisionImpl = new IRemoteMethodsImpl();
//        executor.addHandler("methods", decisionImpl, IRemoteMethods.class);
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
//    }
//
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }
//}
//*/