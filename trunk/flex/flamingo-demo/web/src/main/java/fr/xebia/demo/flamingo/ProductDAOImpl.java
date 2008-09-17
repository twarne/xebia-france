package fr.xebia.demo.flamingo;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateCallback; 
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.HibernateException;
import org.hibernate.Query; 
import org.hibernate.Session;


public class ProductDAOImpl extends HibernateDaoSupport implements ProductDAO {

    public List<Product> getResultList() {

        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                return session.createQuery("from Product").list();
            }
        });
    }

    public void save(Product entity) {
        getHibernateTemplate().save(entity);   
    }

    public void update(Product entity) {
        getHibernateTemplate().update(entity);
    }

    public void remove(Product entity) {
        getHibernateTemplate().delete(entity);
    }
}