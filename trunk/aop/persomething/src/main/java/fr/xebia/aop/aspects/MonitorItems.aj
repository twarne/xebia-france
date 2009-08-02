package fr.xebia.aop.aspects;

import fr.xebia.aop.app.Item;
import fr.xebia.aop.app.Caddy;

public aspect MonitorItems perthis (execution(Caddy.new(..))) {
	
	private int total = 0;
	private int nb = 0;
	
	after(Item i) returning() : execution(* Caddy.add(Item)) && args(i) {
		total +=i.getPrice();
		nb ++;
	}
	
	before(): execution(* Caddy.clear()) {
		System.out.println(" Caddy items("+nb+"), mean price ("+total/nb+")");
		total = 0;
		nb = 0;
	}
}
