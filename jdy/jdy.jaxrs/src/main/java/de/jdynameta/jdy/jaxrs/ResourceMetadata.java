package de.jdynameta.jdy.jaxrs;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

/**
 *
 * @author rschneider
 */
public class ResourceMetadata {
    
    @GET
    @Path("{paths: .+}")
    public Response getElement(@PathParam("paths") List<PathSegment> segments, @Context HttpServletRequest request) {
        
        return null;
    }
}
