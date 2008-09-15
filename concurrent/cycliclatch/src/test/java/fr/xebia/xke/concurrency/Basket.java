package fr.xebia.xke.concurrency;

public interface Basket {
	
	void put(String data);
	
	void start();
	
	void stop();

	int getSize();

	int getResetCount();
}
