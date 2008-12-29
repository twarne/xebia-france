package fr.xebia.xke.domain.hibernate;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fr.xebia.xke.domain.MobilePhone;
import fr.xebia.xke.domain.MobilePhoneRepository;

public class MobilePhoneHibernateDao extends HibernateDaoSupport implements MobilePhoneRepository {

	public MobilePhone findById(long id) throws Exception {
		MobilePhone mobilePhone = (MobilePhone) getHibernateTemplate().get(MobilePhone.class, id);

		if (mobilePhone == null) {
			throw new Exception("Could not find a mobilePhone with id " + id);
		}
		return mobilePhone;
	}

	@SuppressWarnings("unchecked")
	public List<MobilePhone> getList() {
		return (List<MobilePhone>) getHibernateTemplate().loadAll(MobilePhone.class);
	}

	public void remove(MobilePhone mobilePhone) {
		getHibernateTemplate().delete(mobilePhone);
	}

	public MobilePhone save(MobilePhone mobilePhone) {
		getHibernateTemplate().saveOrUpdate(mobilePhone);
		return mobilePhone;
	}

}
