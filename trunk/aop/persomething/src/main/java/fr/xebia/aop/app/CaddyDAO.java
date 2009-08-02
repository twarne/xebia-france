package fr.xebia.aop.app;

public class CaddyDAO {

	public void persist(Item item) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
	}

}
