package fr.xebia.xke.business.impl;

import fr.xebia.xke.business.FlashSellService;
import fr.xebia.xke.business.MobilePhoneService;
import fr.xebia.xke.controller.FlashSellController;
import fr.xebia.xke.domain.MobilePhone;

public class FlashSellServiceImpl implements FlashSellService {

	private MobilePhoneService mobilePhoneService;

	public void setMobilePhoneService(MobilePhoneService mobilePhoneService) {
		this.mobilePhoneService = mobilePhoneService;
	}

	public void startFlashSell(long mobilePhoneId) {
		int stock = 0;
		// Charger le mobile
		try {
			MobilePhone mobilePhone = mobilePhoneService.findById(mobilePhoneId);
			stock = mobilePhone.getStock().intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FlashSellController controller = new FlashSellController(stock);
		controller.start();
	}

	public void stopFlashSell() {
		FlashSellController controller = new FlashSellController();
		controller.stop();
	}
}
