package de.muething;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.xml.sax.SAXException;

import de.muething.models.ExternalProxyDescription;
import de.muething.models.Report;
import de.muething.models.ReportGenerator;
import de.muething.proxying.ManagedProxy;
import de.muething.proxying.ProxyManager;

/**
 * Root resource (exposed at "proxy" path)
 */
@Path("proxy")
public class ProxyAPI {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ExternalProxyDescription create() {
    	try {
			ManagedProxy proxy = ProxyManager.INSTANCE.getNewManagedProxy();
			
			return new ExternalProxyDescription(proxy);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return null;
    }
    

    @POST @Path("/{proxyIdentifier}/newSession")
    @Produces(MediaType.APPLICATION_JSON)
    public ExternalProxyDescription startSessionForProxy(@PathParam("proxyIdentifier") String proxyIdentifier) {
		ManagedProxy proxy = ProxyManager.INSTANCE.newSessionWithProxy(proxyIdentifier);
        return new ExternalProxyDescription(proxy);
    }
    
    @POST @Path("/{proxyIdentifier}/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public String stopProxy(@PathParam("proxyIdentifier") String proxyIdentifier) {
    	

        return "unimplemented";
    }
    
    @GET @Path("/{proxyIdentifier}/report")
    @Produces(MediaType.APPLICATION_JSON)
    public Report getReport(@PathParam("proxyIdentifier") String proxyIdentifier) {
        return ReportGenerator.getReportFor(proxyIdentifier);
    }
}
