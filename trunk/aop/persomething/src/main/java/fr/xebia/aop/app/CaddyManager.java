package fr.xebia.aop.app;


public class CaddyManager {

	private CaddyDAO dao = new CaddyDAO();
	
	private Caddy caddy = new Caddy();
	
	public void addItem(Item i) {
		caddy.add(i);
	}
	
	void purchase() {
		for (Item item : caddy.getItems()) {
			 dao.persist(item);
		}
	}

	public void clearCaddy() {
		caddy.clear();
	}
	 
	
}
