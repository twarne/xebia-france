<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.lang.reflect.InvocationHandler"%>
<%@page import="java.lang.reflect.Proxy"%>
<%@page import="java.net.ProxySelector"%>
<%@page import="java.net.URI"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page
	import="org.apache.cxf.configuration.security.AuthorizationPolicy"%>
<%@page import="org.apache.cxf.endpoint.Client"%>
<%@page import="org.apache.cxf.frontend.ClientProxy"%>
<%@page import="org.apache.cxf.transport.Conduit"%>
<%@page import="org.apache.cxf.transport.http.HTTPConduit"%>
<%@page
	import="org.apache.cxf.transports.http.configuration.HTTPClientPolicy"%>
<%@page import="org.apache.cxf.ws.addressing.AttributedURIType"%>
<%@page import="org.apache.cxf.ws.addressing.EndpointReferenceType"%>
<%@page
	import="org.springframework.beans.factory.BeanIsAbstractException"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%
    String uid = request.getParameter("uid");

    WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    for (String springBeanName : applicationContext.getBeanDefinitionNames()) {
        try {
            Object bean = applicationContext.getBean(springBeanName);
            try {
                if (!Proxy.isProxyClass(bean.getClass())) {
                    continue;
                }

                InvocationHandler invocationHandler = Proxy.getInvocationHandler(bean);

                if (!(invocationHandler instanceof ClientProxy)) {
                    continue;
                }

                out.println("<h1>" + springBeanName + "</h1>");
                ClientProxy clientProxy = (ClientProxy) invocationHandler;

                Client client = clientProxy.getClient();

                out.println("<h2>Conduit</h2>");
                Conduit conduit = client.getConduit();
                if (conduit instanceof HTTPConduit) {
                    HTTPConduit httpConduit = (HTTPConduit) clientProxy.getClient().getConduit();

                    out.println("<h3>Target</h3>");
                    EndpointReferenceType endpointReferenceType = httpConduit.getTarget();
                    AttributedURIType address = endpointReferenceType.getAddress();
                    out.println("address: " + address == null ? null : address.getValue() + "<br/>");
                    if (address != null && address.getOtherAttributes() != null && address.getOtherAttributes().size() > 0) {
                        out.println("address.attributes: " + address.getOtherAttributes() + "<br/>");
                    }
                    if (endpointReferenceType.getAny() != null && endpointReferenceType.getAny().size() > 0) {
                        out.println("any: " + endpointReferenceType.getAny() + "<br/>");
                    }
                    if (endpointReferenceType.getOtherAttributes() != null && endpointReferenceType.getOtherAttributes().size() > 0) {
                        out.println("otherAttributes: " + endpointReferenceType.getOtherAttributes() + "<br/>");
                    }
                    if (endpointReferenceType.getMetadata() != null) {
                        out.println("metadata: " + endpointReferenceType.getMetadata() + "<br/>");
                    }

                    out.println("<h3>Http Client policy</h3>");
                    HTTPClientPolicy clientPolicy = httpConduit.getClient();

                    out.println("accept: " + clientPolicy.getAccept() + "<br/>");
                    out.println("acceptEncoding: " + clientPolicy.getAcceptEncoding() + "<br/>");
                    out.println("acceptLanguage: " + clientPolicy.getAcceptLanguage() + "<br/>");
                    out.println("allowChunking: " + clientPolicy.isAllowChunking() + "<br/>");
                    out.println("autoRedirect: " + clientPolicy.isAutoRedirect() + "<br/>");
                    out.println("browserType: " + clientPolicy.getBrowserType() + "<br/>");
                    out.println("cacheControl: " + clientPolicy.getCacheControl() + "<br/>");
                    out.println("chunkingThreshold: " + clientPolicy.getChunkingThreshold() + "<br/>");
                    out.println("connection: " + clientPolicy.getConnection() + "<br/>");
                    out.println("connectionTimeout: " + clientPolicy.getConnectionTimeout() + "<br/>");
                    out.println("contentType: " + clientPolicy.getContentType() + "<br/>");
                    out.println("cookie: " + clientPolicy.getCookie() + "<br/>");
                    out.println("decoupledEndpoint: " + clientPolicy.getDecoupledEndpoint() + "<br/>");
                    out.println("elementType: " + clientPolicy.getElementType() + "<br/>");
                    out.println("host: " + clientPolicy.getHost() + "<br/>");
                    out.println("maxRetransmits: " + clientPolicy.getMaxRetransmits() + "<br/>");

                    out.println("receiveTimeout: " + clientPolicy.getReceiveTimeout() + "<br/>");
                    out.println("referer: " + clientPolicy.getReferer() + "<br/>");
                    out.println("required: " + (clientPolicy.isSetRequired() ? clientPolicy.getRequired() : null) + "<br/>");

                    out.println("<h3>Proxy</h3/>");
                    URI uri = new URI(address.getValue());
                    List<java.net.Proxy> proxies = ProxySelector.getDefault().select(uri);
                    boolean defaultProxyConfigured = proxies.size() > 0 && !java.net.Proxy.NO_PROXY.equals(proxies.get(0));
                    if (clientPolicy.isSetProxyServer()) {
                        out.println("proxyServer: " + clientPolicy.getProxyServer() + "<br/>");
                        out.println("proxyServerPort: "
                                + (clientPolicy.isSetProxyServerPort() ? clientPolicy.getProxyServerPort() : null) + "<br/>");
                        out.println("proxyServerType: " + clientPolicy.getProxyServerType() + "<br/>");
                        if(defaultProxyConfigured) {
                            out.println("<strong>Default JVM proxy " + proxies + "is ignored</strong>");
                        }
                    } else if (defaultProxyConfigured) {
                        out.println("<strong>use default JVM proxy : " + proxies + " !</strong><br/>");
                    } else {
                        out.println("DIRECT<br/>");
                    }

                    AuthorizationPolicy authorizationPolicy = httpConduit.getAuthorization();
                    if (authorizationPolicy != null) {
                        out.println("<h3>authorization</h3>");
                        out.println("userName: " + authorizationPolicy.getUserName() + "<br/>");
                        String passwordSize = authorizationPolicy.getPassword() == null ? null : "" + authorizationPolicy.getPassword().length();
                        out.println("passwordSize: " + passwordSize + "</br>");
                        out.println("authorizationType: " + authorizationPolicy.getAuthorizationType());
                    }

                }

                clientProxy.getClient().getEndpoint();
            } catch (Exception e) {
                out.print("<pre>");
                e.printStackTrace(new PrintWriter(out));
                out.print("</pre>");
            }

        } catch (BeanIsAbstractException e) {
            // skip
        }
        out.println("<hr/>");
        out.flush();
    }
%>