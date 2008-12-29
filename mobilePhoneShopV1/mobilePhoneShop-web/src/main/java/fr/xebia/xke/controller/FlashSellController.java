package fr.xebia.xke.controller;

import java.util.Random;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

public class FlashSellController {
	private static FlashSellThread thread;

	private static int initialValue;
	private static int currentValue;

	public FlashSellController(int initialValueV) {
		initialValue = initialValueV;
	}
	
	public FlashSellController() {
		
	}

	public void start() {
		if (thread == null) {
			thread = new FlashSellThread();
			thread.start();
		}
	}

	public void stop() {
		thread.running = false;
		thread = null;
	}

	public static class FlashSellThread extends Thread {

		public boolean running = true;

		public void run() {
			MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
			String clientID = UUIDUtils.createUUID();

			Random random = new Random();
			currentValue = initialValue;
			// double maxChange = initialValue * 0.005;

			while (running) {
				int variation = 1;
				currentValue -= variation;

				AsyncMessage msg = new AsyncMessage();
				msg.setDestination("flashSellFeed");
				msg.setClientId(clientID);
				msg.setMessageId(UUIDUtils.createUUID());
				msg.setTimestamp(System.currentTimeMillis());
				msg.setBody(new Double(currentValue));
				msgBroker.routeMessageToService(msg, null);

				try {
					int time = random.nextInt(5);
					while(time==0){
						time = random.nextInt(5);
					}
					Thread.sleep(time*500);
				} catch (InterruptedException e) {
				}

			}
		}
	}

}