package fr.xebia.xke.business.impl;

import java.util.ArrayList;
import java.util.List;

import fr.xebia.xke.domain.MobilePhone;

public class MobilePhoneServiceNoInjection {

	// private MobilePhoneRepository mobilePhoneRepository;

	public MobilePhone save(MobilePhone mobilePhone) throws Exception {
		// Not implemented
		return null;
	}

	public void remove(MobilePhone mobilePhone) throws Exception {
		// Not implemented
	}

	public MobilePhone findById(MobilePhone mobilePhone) throws Exception {
		// Not implemented
		return null;
	}

	public List<MobilePhone> getList() throws Exception {
		List<MobilePhone> list = new ArrayList<MobilePhone>();
		MobilePhone mobilePhone = new MobilePhone();
		mobilePhone.setId(1);
		mobilePhone.setName("Pojo mobile phone");
		list.add(mobilePhone);
		MobilePhone mobilePhone2 = new MobilePhone();
		mobilePhone2.setId(2);
		mobilePhone2.setName("Another pojo mobile phone");
		list.add(mobilePhone2);
		return list;
	}
}