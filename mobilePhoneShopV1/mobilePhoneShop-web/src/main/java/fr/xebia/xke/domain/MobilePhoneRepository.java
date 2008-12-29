package fr.xebia.xke.domain;

import java.util.List;

public interface MobilePhoneRepository {

	MobilePhone save(MobilePhone mobilePhone);

	void remove(MobilePhone mobilePhone);

	MobilePhone findById(long id) throws Exception;

	List<MobilePhone> getList();

}
