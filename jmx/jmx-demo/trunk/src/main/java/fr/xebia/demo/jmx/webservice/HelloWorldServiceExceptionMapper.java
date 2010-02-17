package fr.xebia.demo.jmx.webservice;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;

@Provider
public class HelloWorldServiceExceptionMapper implements ExceptionMapper<HelloWorldServiceException> {

    @Override
    public Response toResponse(HelloWorldServiceException exception) {
        return Response.status(533)
            .entity("this is an HelloWorldServiceException handled by an ExceptionMapper").build();
    }

}
