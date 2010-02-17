/*
 * Copyright 2008-2009 Xebia and the original author or authors.
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
package org.apache.catalina.mbeans;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class JmxRemoteLifecycleListenerTest {
    private static class StringManager {

        public String getString(final String key, final Object... args) {
            return key + " - " + Lists.newArrayList(args);
        }

    }

    private final Logger log = LoggerFactory.getLogger(JmxRemoteLifecycleListenerTest.class);

    protected static final StringManager sm = new StringManager();

    @Test
    public void test() throws InterruptedException {

        System.setProperty("java.rmi.server.logCalls", "true");
        
        HashMap<String, Object> env = new HashMap<String, Object>();

        JMXConnectorServer csPlatform = createServer("Platform", 6969, 6979, env, ManagementFactory.getPlatformMBeanServer());
        
        Thread.sleep(Long.MAX_VALUE);
        
        System.out.println(csPlatform);
    }

    private JMXConnectorServer createServer(String serverName, int theRmiRegistryPort, int theRmiServerPort,
            HashMap<String, Object> theEnv, MBeanServer theMBeanServer) {

        // Create the RMI registry
        try {
            LocateRegistry.createRegistry(theRmiRegistryPort);
        } catch (RemoteException e) {
            log.error(sm.getString("jmxRemoteLifecycleListener.createRegistryFailed", serverName, Integer.toString(theRmiRegistryPort)), e);
            return null;
        }

        // Build the connection string with fixed ports
        StringBuilder url = new StringBuilder();
        url.append("service:jmx:rmi://localhost:");
        url.append(theRmiServerPort);
        url.append("/jndi/rmi://localhost:");
        url.append(theRmiRegistryPort);
        url.append("/jmxrmi");
        JMXServiceURL serviceUrl;
        try {
            serviceUrl = new JMXServiceURL(url.toString());
        } catch (MalformedURLException e) {
            log.error(sm.getString("jmxRemoteLifecycleListener.invalidURL", serverName, url.toString()), e);
            return null;
        }

        // Start the JMX server with the connection string
        JMXConnectorServer cs = null;
        try {
            cs = JMXConnectorServerFactory.newJMXConnectorServer(serviceUrl, theEnv, theMBeanServer);
            cs.start();
            log.info(sm.getString("jmxRemoteLifecycleListener.start", Integer.valueOf(theRmiRegistryPort), Integer
                    .valueOf(theRmiServerPort), serverName));
        } catch (IOException e) {
            log.error(sm.getString("jmxRemoteLifecycleListener.createServerFailed", serverName), e);
        }
        return cs;
    }
}
