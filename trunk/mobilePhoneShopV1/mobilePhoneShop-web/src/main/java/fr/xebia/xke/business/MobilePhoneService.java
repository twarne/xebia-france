package fr.xebia.xke.business;

import java.util.List;

import fr.xebia.xke.domain.MobilePhone;

public interface MobilePhoneService {
	
	void remove(MobilePhone mobilePhone) throws Exception;

	MobilePhone save(MobilePhone mobilePhone) throws Exception;

	MobilePhone findById(long id) throws Exception;

	List<MobilePhone> getList() throws Exception;
}
