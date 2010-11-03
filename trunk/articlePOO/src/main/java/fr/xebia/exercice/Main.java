package fr.xebia.exercice;

import fr.xebia.exercice.spring.WebControllerVersionSpring;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import fr.xebia.exercice.procedural.WebControllerVersionProcedurale;

class Main {
    public static void main(String[] args) {

        runVersionProcedurale();
        runVersionSpring();
    }

    public static void runVersionProcedurale() {

        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("/applicationContext-versionProcedurale.xml", Main.class);

        WebControllerVersionProcedurale webControllerVersionProcedurale =
                applicationContext.getBean(WebControllerVersionProcedurale.class);

        double valoPortfolio = webControllerVersionProcedurale.valorisePortfolio(1);
        System.out.println("valoPortfolio = " + valoPortfolio);
    }

    public static void runVersionSpring() {

        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("/applicationContext-versionSpring.xml", Main.class);

        WebControllerVersionSpring webController =
                applicationContext.getBean(WebControllerVersionSpring.class);

        double valoPortfolio = webController.valorisePortfolio(1);
        System.out.println("valoPortfolio = " + valoPortfolio);
    }
}
