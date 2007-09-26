/*
 * Copyright 2007 Xebia and the original author or authors.
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
package fr.xebia.demo.objectgrid.launcher;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import com.ibm.ws.objectgrid.InitializationService;

/**
 * 
 * Starts an ObjectGrid container named "xebiaContainer1".
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ObjectGridContainer1Launcher {

    private final static Logger logger = Logger.getLogger(ObjectGridContainer1Launcher.class);

    public static void main(String[] args) {

        if (System.getProperty("java.vm.vendor").indexOf("IBM") == -1) {
            System.err.println("Startup failure. JVM must be an IBM 5+ JVM. Exit");
            return;
        }
        float javaVersion = Float.parseFloat(System.getProperty("java.specification.version").substring(0, 3));
        if (javaVersion < 1.5f) {
            System.err.println("Startup failure. JVM must be an IBM 5+ JVM. Exit");
            return;
        }

        File file = new File(".");
        logger.info("use OBJECTGRID_HOME=" + file.getAbsolutePath());
        System.setProperty("OBJECTGRID_HOME", file.getAbsolutePath());
        try {
            URL objectgridFileUrl = ObjectGridContainer1Launcher.class.getResource("objectgrid.xml");
            URL deploymentPolicyFileUrl = ObjectGridContainer1Launcher.class.getResource("deploymentdescriptor.xml");
            logger.info("objectgridFile: " + objectgridFileUrl);
            logger.info("deploymentPolicyFile: " + deploymentPolicyFileUrl);

            String[] startupArgs = new String[]{"xebiaContainer1", "-objectgridFile", objectgridFileUrl.getFile(), "-deploymentPolicyFile",
                    deploymentPolicyFileUrl.getFile(), "-catalogServiceEndPoints", "localhost:2809", "-traceSpec",
                    "ObjectGrid*=all=disabled"};
            InitializationService.main(startupArgs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
