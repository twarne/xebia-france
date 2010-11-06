/*
 * Copyright 2008-2009 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.xebia.exercice;

import fr.xebia.exercice.servloc.ServiceLocator;
import fr.xebia.exercice.servloc.WebControllerVersionServLoc;
import fr.xebia.exercice.spring.WebControllerVersionSpring;
import fr.xebia.exercice.statik.WebControllerVersionStatic;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import fr.xebia.exercice.procedural.WebControllerVersionProcedurale;

class Main {
    public static void main(String[] args) {

        // problème si j'inverse la version spring et la version procedurale
//        runVersionSpring();
//        runVersionProcedurale();
//        runVersionStatic();
        runVersionServLoc();
    }

    public static void runVersionProcedurale() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("/applicationContext-versionProcedurale.xml", Main.class);
        WebControllerVersionProcedurale webController = applicationContext.getBean(WebControllerVersionProcedurale.class);
        double valoPortfolio = webController.valorisePortfolio(1);
        System.out.println("valoPortfolio = " + valoPortfolio);
    }

    public static void runVersionSpring() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("/applicationContext-versionSpring.xml", Main.class);
        WebControllerVersionSpring webController = applicationContext.getBean(WebControllerVersionSpring.class);
        double valoPortfolio = webController.valorisePortfolio(1);
        System.out.println("valoPortfolio = " + valoPortfolio);
    }

    public static void runVersionServLoc() {
        WebControllerVersionServLoc webController = ServiceLocator.getBean(WebControllerVersionServLoc.class);
        double valoPortfolio = webController.valorisePortfolio(1);
        System.out.println("valoPortfolio = " + valoPortfolio);
    }

    public static void runVersionStatic() {
        WebControllerVersionStatic webController = new WebControllerVersionStatic();
        double valoPortfolio = webController.valorisePortfolio(1);
        System.out.println("valoPortfolio = " + valoPortfolio);
    }
}
