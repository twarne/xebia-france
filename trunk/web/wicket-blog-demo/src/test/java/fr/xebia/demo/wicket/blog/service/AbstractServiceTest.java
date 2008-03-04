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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.UrlResource;

public abstract class AbstractServiceTest<T> {

    private static final String NO_ITEM_RETREIVED_MSG = "No item retreived";

    private static final Logger logger = Logger.getLogger(AbstractServiceTest.class);

    private static XmlBeanFactory factory;

    protected static ServiceLocator serviceLocator;

    protected static Random randomizer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        randomizer = new Random();
        long startTime = System.currentTimeMillis();
        logger.info("Initializing Services");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL configuration = contextClassLoader.getResource("applicationContext-service.xml");
        factory = new XmlBeanFactory(new UrlResource(configuration));
        long endTime = System.currentTimeMillis();
        logger.info("Initialisation des services en " + ((endTime - startTime) / 1000.0) + " s");
        serviceLocator = (ServiceLocator) factory.getBean("serviceLocator");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        logger.info("Destroying Services");
        factory.destroySingletons();
        factory = null;
        serviceLocator = null;
        randomizer = null;
    }

    @Test
    public void testAdd() throws ServiceException {
        T object = createObject();
        getService().save(object);
        assertNotNull("Generated id is null !", extractId(object));
        logger.info("Inserted object with id: " + extractId(object));
    }

    @Test(expected=ServiceException.class)
    public void testFailedAdd() throws ServiceException {
        T object = createDirtyObject();
        getService().save(object);
    }

    @Test
    public void testList() throws ServiceException {
        List<T> objects = getService().list();
        assertSame(NO_ITEM_RETREIVED_MSG, objects.isEmpty(), false);
        logger.info("Objects extracted count : " + objects.size());
    }

    @Test
    public void testGet() throws ServiceException {
        Service<T> service = getService();
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        Serializable id = extractId(objects.get(0));
        logger.info("Getting object with id: " + id);
        T entity = service.get(id);
        assertNotNull("Returned object is null", entity);
    }

    @Test(expected=ServiceException.class)
    public void testFailedGet() throws ServiceException {
        getService().get(new Long(100));
    }

    @Test
    public void testUpdate() throws ServiceException {
        Service<T> service = getService();
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        Serializable id = extractId(objects.get(0));
        T entity = service.get(id);
        updateObject(entity);
        logger.info("Updating object with id: " + id);
        service.update(entity);
        assertNotNull("Returned object is null", entity);
    }

    @Test(expected=ServiceException.class)
    public void testFailedUpdate() throws ServiceException {
        Service<T> service = getService();
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        T object = objects.get(0);
        updateToDirtyObject(object);
        getService().update(object);
    }

    @Test
    public void testSearch() throws ServiceException {
        Service<T> service = getService();
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        T fromObject = objects.get(0);
        T object = createSearchObject(fromObject);
        List<T> categories = service.search(object);
        logger.info("Search found " + categories.size() + " objects");
        assertSame(NO_ITEM_RETREIVED_MSG, categories.size(), 1);
        assertEquals("Search does retreive the expected object", fromObject, categories.get(0));
    }

    @Test(expected=ServiceException.class)
    public void testDeleteById() throws ServiceException {
        Service<T> service = getService();
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        Serializable id = extractId(objects.get(0));
        logger.info("Deleting object with id: " + id);
        service.deleteById(id);
        service.get(id);
    }

    @Test(expected=ServiceException.class)
    public void testFailedDeleteById() throws ServiceException {
        getService().deleteById(new Long(100));
    }

    @Test(expected=ServiceException.class)
    public void testDelete() throws ServiceException {
        Service<T> service = getService();
        T object = createObject();
        service.save(object);
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        object = objects.get(0);
        Serializable id = extractId(object);
        logger.info("Deleting object with id: " + id);
        service.delete(object);
        service.get(id);
    }

    @Test(expected=ServiceException.class)
    public void testFailedDelete() throws ServiceException {
        T object = createDirtyObject();
        getService().delete(object);
    }

    protected abstract T createObject();

    protected abstract T createDirtyObject();

    protected abstract void updateToDirtyObject(T object);

    protected abstract void updateObject(T object);

    protected abstract T createSearchObject(T fromObject);

    protected abstract Serializable extractId(T object);

    protected abstract Service<T> getService();
}
