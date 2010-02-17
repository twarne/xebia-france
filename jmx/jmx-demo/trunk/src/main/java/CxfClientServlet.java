import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.xebia.demo.jmx.webservice.HelloWorldService;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CxfClientServlet extends HttpServlet {
    HelloWorldService helloWorldService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        WebApplicationContext applicationContext = WebApplicationContextUtils
            .getRequiredWebApplicationContext(config.getServletContext());
        helloWorldService = (HelloWorldService)applicationContext.getBean("helloWorldServiceClient-ok");

        System.out.println(this.getClass() + " initialized");

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        try {
            String answer = helloWorldService.sayHi("I expect a success");
            response.getWriter().print(answer);
        } catch (Exception e) {
            System.out.println(e.toString());
            response.sendError(500);
        }
    }
}
