package fr.xebia.xke.business.impl;

import java.util.List;

import fr.xebia.xke.business.MobilePhoneService;
import fr.xebia.xke.domain.MobilePhone;
import fr.xebia.xke.domain.MobilePhoneRepository;

public class MobilePhoneServiceImpl implements MobilePhoneService {

	private MobilePhoneRepository mobilePhoneRepository;

	public void setMobilePhoneRepository(MobilePhoneRepository mobilePhoneRepository) {
		this.mobilePhoneRepository = mobilePhoneRepository;
	}

	public MobilePhone save(MobilePhone mobilePhone) throws Exception {
		try {
			this.mobilePhoneRepository.save(mobilePhone);
			return mobilePhone;
		} catch (Exception e) {
			throw new Exception("Could not save mobilePhone because: " + e.getCause());
		}
	}

	public void remove(MobilePhone mobilePhone) throws Exception {
		try {
			this.mobilePhoneRepository.remove(mobilePhone);
		} catch (Exception e) {
			throw new Exception("Could not delete mobilePhone because " + e.getMessage());
		}
	}

	public MobilePhone findById(long id) throws Exception {
		try {
			return this.mobilePhoneRepository.findById(id);
		} catch (Exception e) {
			throw new Exception("Could not find mobilePhone because " + e.getMessage());
		}
	}

	public List<MobilePhone> getList() throws Exception {
		try {
			return this.mobilePhoneRepository.getList();
		} catch (Exception e) {
			throw new Exception("Could not list mobilePhone because " + e.getMessage());
		}
	}
}