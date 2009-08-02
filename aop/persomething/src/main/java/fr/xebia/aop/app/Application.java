package fr.xebia.aop.app;

import java.util.Random;

public class Application implements Runnable{

	static Random r = new Random();
	private CaddyManager  manager = new CaddyManager(); 
	

	public void run() {
		for (int i =0 ; i < 10; i++) {
			manager.addItem(new Item(i,"Item#"+i,r.nextInt(100)));
		}
		manager.purchase();	
		manager.clearCaddy();
	}
	

	public static void main(String[] args) {
		Application application = new Application();
		for (int i=0; i < 5; i++) {
			
			application.run();
		}
	}
}
