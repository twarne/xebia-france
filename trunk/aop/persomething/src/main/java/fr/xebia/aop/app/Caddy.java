package fr.xebia.aop.app;

import java.util.LinkedList;
import java.util.List;

public class Caddy {

	private List<Item> items = new LinkedList<Item>();

	public void add(Item i) {
		items.add(i);
		
	}

	public List<Item> getItems() {
		return items;
	}

	public void clear() {
		items.clear();	
	}

	
}
