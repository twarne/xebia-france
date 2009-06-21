<%
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    response.setContentType("text/xml;charset=UTF-8");
%><soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
	<soap:Body>
		<soap:Fault>
			<faultcode>soap:Server</faultcode>
			<faultstring>the hello world service exception</faultstring>
			<detail>
			<ns1:HelloWorldServiceException
				xmlns:ns1="http://webservice.jmx.demo.xebia.fr/" />
			<stackTrace xmlns="http://schemas.xmlsoap.org/soap/envelope/" />
			</detail>
		</soap:Fault>
	</soap:Body>
</soap:Envelope>