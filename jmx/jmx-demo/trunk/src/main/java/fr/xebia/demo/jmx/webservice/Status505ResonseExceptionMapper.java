package fr.xebia.demo.jmx.webservice;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;

public class Status505ResonseExceptionMapper implements ResponseExceptionMapper<Status505Exception> {
    @Override
    public Status505Exception fromResponse(Response response) {
        if (response.getStatus() == 505) {
            return new Status505Exception();
        }
        return null;
    }
}
