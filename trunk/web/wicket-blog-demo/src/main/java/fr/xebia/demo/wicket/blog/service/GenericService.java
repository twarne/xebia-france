/*
 * Copyright 2007 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.demo.wicket.blog.service;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Example.PropertySelector;
import org.hibernate.type.Type;

/**
 * Defines the generic method (save, update, delete, search, etc) for all kind of objects
 */
public abstract class GenericService<T> implements Service<T> {

    public static final int DEFAULT_MAX_RESULTS = 200;

    private static final PropertySelector NOT_NULL_OR_EMPTY = new NotNullOrEmptyPropertySelector();

    private static final class NotNullOrEmptyPropertySelector implements PropertySelector {

        private static final Logger logger = Logger.getLogger(NotNullOrEmptyPropertySelector.class);

        private static final long serialVersionUID = 1L;

        public boolean include(Object propertyValue, String propertyName, Type type) {
            if (propertyValue == null) {
                return false;
            }
            try {
                if ((propertyValue instanceof String) && StringUtils.isEmpty((String) propertyValue)) {
                    return false;
                }
                if ((propertyValue instanceof Number) && ((Number) propertyValue).longValue() == 0) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                logger.debug("Can't introspect object", e);
                return false;
            }
        }

        private Object readResolve() {
            return NOT_NULL_OR_EMPTY;
        }
    }

    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private final ThreadLocal<EntityManager> entityManagerCache = new ThreadLocal<EntityManager>();

    protected EntityManager currentEntityManager() throws PersistenceException {
        EntityManager entityManager = entityManagerCache.get();
        // Ouvre une nouvelle Session, si ce Thread n'en a aucune
        if (entityManager == null) {
            // Retrieve an application managed entity manager
            entityManager = entityManagerFactory.createEntityManager();
            entityManagerCache.set(entityManager);
        }
        return entityManager;
    }

    protected void closeEntityManager() throws PersistenceException {
        EntityManager entityManager = entityManagerCache.get();
        entityManagerCache.set(null);
        if (entityManager != null) {
            entityManager.close();
        }
    }

    private static final Logger logger = Logger.getLogger(GenericService.class);

    /**
     * Renvoie la classe de l'objet concerné par le service pour la manipulation Hibernate
     */
    protected abstract Class<T> getObjectClass();

    /**
     * Retourne la PrimaryKey de l'objet
     */
    protected abstract Serializable getObjectId(T object);

    public int getMaxResults() {
        return DEFAULT_MAX_RESULTS;
    }

    public void save(T entity) throws ServiceException {
        try {
            EntityManager entityManager = currentEntityManager();
            entityManager.getTransaction().begin();

            entityManager.persist(entity);

            commitTransaction();
        } catch (PersistenceException e) {
            logger.error(e.getCause(), e);
            rollbackTransaction();
            throw new ServiceException("Can't update object", e);
        } finally {
            closeEntityManager();
        }
    }

    public T update(T entity) throws ServiceException {
        try {
            EntityManager entityManager = currentEntityManager();
            entityManager.getTransaction().begin();
            T loadedObject = entityManager.find(getObjectClass(), getObjectId(entity));
            T mergedEntity = merge(loadedObject, entity);
            T updatedEntity = entityManager.merge(mergedEntity);
            commitTransaction();
            return updatedEntity;
        } catch (PersistenceException e) {
            logger.error(e.getCause(), e);
            rollbackTransaction();
            throw new ServiceException("Can't update object", e);
        } finally {
            closeEntityManager();
        }
    }

    /**
     * Merge the values of the loaded object with the received object to preserve the associated objects
     */
    protected abstract T merge(T loadedObject, T object);

    public void deleteById(Serializable id) throws ServiceException {
        try {
            EntityManager entityManager = currentEntityManager();
            entityManager.getTransaction().begin();
            Object loadedEntity = entityManager.find(getObjectClass(), id);
            if (loadedEntity == null) {
                throw new ServiceException("Entity referenced by id "+id+" does not exist");
            }
            entityManager.remove(loadedEntity);
            commitTransaction();
        } catch (PersistenceException e) {
            logger.error(e.getCause(), e);
            rollbackTransaction();
            throw new ServiceException("Can't delete object", e);
        } finally {
            closeEntityManager();
        }
    }

    public void delete(T entity) throws ServiceException {
        try {
            EntityManager entityManager = currentEntityManager();
            entityManager.getTransaction().begin();

            Serializable objectId = getObjectId(entity);
            if (objectId == null) {
                throw new ServiceException("Entity has no id");
            }
            T loadedEntity = entityManager.find(getObjectClass(), objectId);
            if (loadedEntity == null) {
                throw new ServiceException("Entity referenced by id "+objectId+" does not exist");
            }
            entityManager.remove(loadedEntity);

            commitTransaction();
        } catch (PersistenceException e) {
            logger.error(e.getCause(), e);
            rollbackTransaction();
            throw new ServiceException("Can't delete object", e);
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings("unchecked")
    public T get(Serializable id) throws ServiceException {
        try {
            EntityManager entityManager = currentEntityManager();
            T object = entityManager.find(getObjectClass(), id);
            if (object == null) {
                throw new ServiceException("Object Not Found (id=" + id + ')');
            }
            return object;
        } catch (EntityNotFoundException e) {
            logger.error(e.getCause(), e);
            throw new ServiceException("Object Not Found (id=" + id + ')', e);
        } catch (PersistenceException e) {
            logger.error(e.getCause(), e);
            throw new ServiceException("Can't retreive object", e);
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> list() throws ServiceException {
        try {
            EntityManager entityManager = currentEntityManager();

            Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(getObjectClass());
            criteria.setMaxResults(getMaxResults());
            return criteria.setCacheable(true).list();
        } catch (PersistenceException e) {
            logger.error(e.getCause(), e);
            throw new ServiceException("Can't get object list from database", e);
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> search(final T exampleEntity) throws ServiceException {
        try {
            Validate.notNull(exampleEntity, "Example entity must not be null");
            logger.debug("Search: " + exampleEntity.toString());
            Session session = ((Session) currentEntityManager().getDelegate());
            Criteria criteria = session.createCriteria(getObjectClass());
            Example example = Example.create(exampleEntity);
            example.setPropertySelector(NOT_NULL_OR_EMPTY);
            example.ignoreCase();
            example.enableLike();
            logger.debug("Search example object: " + example.toString());
            criteria.add(example);
            criteria.setMaxResults(getMaxResults());
            criteria.setCacheable(true);
            return criteria.list();
       } catch (PersistenceException e) {
           logger.error(e.getCause(), e);
           throw new ServiceException("Can't search object list from database", e);
       } finally {
           closeEntityManager();
       }
    }

    protected void commitTransaction() throws ServiceException {
        try {
            EntityTransaction transaction = currentEntityManager().getTransaction();
            if (transaction.isActive()) {
                transaction.commit();
            }
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Can't commit transaction", e);
        }
    }

    protected void rollbackTransaction() throws ServiceException {
        try {
            EntityTransaction transaction = currentEntityManager().getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Can't rollback transaction", e);
        }
    }
}
