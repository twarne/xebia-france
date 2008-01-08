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
package fr.xebia.demo.objectgrid.ticketing.launcher;

import java.io.File;

import org.apache.log4j.Logger;

import com.ibm.ws.objectgrid.InitializationService;

import fr.xebia.demo.objectgrid.ObjectGridUtils;

/**
 * Starts an ObjectGrid Catalog Server
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ObjectGridCatalogServerLauncher {

    private final static Logger logger = Logger.getLogger(ObjectGridCatalogServerLauncher.class);

    public static void main(String[] args) {

        ObjectGridUtils.checkJvmPreRequisitesForObjectGrid();
        
        File file = new File(".");
        logger.info("use OBJECTGRID_HOME=" + file.getAbsolutePath());
        System.setProperty("OBJECTGRID_HOME", file.getAbsolutePath());
        try {
            String[] startupArgs = new String[]{"catalogServer", "-domain", "xebiaDomain", "-zone", "xebiaZone", "-traceSpec",
                    "ObjectGrid*=all=disabled"};
            InitializationService.main(startupArgs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
