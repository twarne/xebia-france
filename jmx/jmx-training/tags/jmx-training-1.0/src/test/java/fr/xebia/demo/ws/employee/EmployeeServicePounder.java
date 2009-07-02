/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.demo.ws.employee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EmployeeServicePounder {
    public static void main(String[] args) {
        try {
            stressTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
    private static void stressTest() throws InterruptedException {
        final EmployeeService employeeService = buildEmployeeService();
        
        int injectorsCount = 10;
        
        ExecutorService executorService = Executors.newFixedThreadPool(injectorsCount);
        final Random random = new Random();
        
        for (int i = 0; i < injectorsCount; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        try {
                            employeeService.getEmployee(random.nextInt(1000));
                            StressTestUtils.incrementProgressBarSuccess();
                        } catch (EmployeeNotFoundException e) {
                            StressTestUtils.incrementProgressBarFailure("x");
                        } catch (RuntimeException e) {
                            StressTestUtils.incrementProgressBarFailure("*");
                        }
                        try {
                            URL url = new URL("http://localhost:8080/jmx-training/employee.jsp?id=" + random.nextInt(1000));
                            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                            int responseCode = httpURLConnection.getResponseCode();
                            // go to end of stream to ease connection recycling
                            httpURLConnection.getContentLength();
                            if (HttpURLConnection.HTTP_OK == responseCode) {
                                StressTestUtils.incrementProgressBarSuccess();
                            } else {
                                StressTestUtils.incrementProgressBarFailure("#");
                            }
                        } catch (IOException e) {
                            StressTestUtils.incrementProgressBarFailure("#");
                            e.printStackTrace();
                        }
                    }
                }
            };
            executorService.execute(runnable);
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        System.out.println("bye");
    }
    
    private static EmployeeService buildEmployeeService() {
        String username = "admin";
        String password = "admin";
        String url = "http://localhost:8080/jmx-training/services/employeeService";
        
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(EmployeeService.class);
        factory.setAddress(url);
        
        EmployeeService employeeService = (EmployeeService)factory.create();
        Client client = ClientProxy.getClient(employeeService);
        
        HTTPConduit httpConduit = (HTTPConduit)client.getConduit();
        HTTPClientPolicy httpClient = httpConduit.getClient();
        
        httpClient.setConnectionTimeout(10000);
        httpClient.setReceiveTimeout(10000);
        
        AuthorizationPolicy authorization = httpConduit.getAuthorization();
        authorization.setUserName(username);
        authorization.setPassword(password);
        return employeeService;
    }
}
