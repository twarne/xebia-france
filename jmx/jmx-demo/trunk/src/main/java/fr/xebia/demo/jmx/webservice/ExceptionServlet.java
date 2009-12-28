package fr.xebia.demo.jmx.webservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class ExceptionServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {

        String pathInfo = request.getPathInfo();

        String errorCodeAsString = StringUtils.substringAfter(pathInfo, "/");
        if (StringUtils.contains(errorCodeAsString, "/")) {
            errorCodeAsString = StringUtils.substringBefore(errorCodeAsString, "/");
        }

        int errorCode;
        try {
            errorCode = Integer.parseInt(errorCodeAsString);
        } catch (NumberFormatException e) {
            errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        response.setStatus(errorCode);
    }
}
