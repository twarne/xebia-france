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
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.UrlResource;

public abstract class AbstractServiceTest<T> {

    private static final String NO_ITEM_RETREIVED_MSG = "No item retreived";

    private static final Logger logger = Logger.getLogger(AbstractServiceTest.class);

    private static boolean initialized = false;

    private static int currentExecutedTestCount = 0;

    private static int totalTestCount = 0;

    private static XmlBeanFactory factory;

    protected ServiceLocator serviceLocator;

    protected Service<T> service;

    protected Random randomizer;

    @Before
    public void setUp() throws Exception {
        randomizer = new Random();
        if (initialized == false) {
            long startTime = System.currentTimeMillis();
            init();
            long endTime = System.currentTimeMillis();
            logger.info("Initialisation des services en " + ((endTime - startTime) / 1000.0) + " s");
        }
        serviceLocator = (ServiceLocator) factory.getBean("serviceLocator");
        service = getService();
    }

    protected void init() {
        logger.info("Initializing Services");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL configuration = contextClassLoader.getResource("applicationContext-service.xml");
        factory = new XmlBeanFactory(new UrlResource(configuration));
        currentExecutedTestCount = 0;
        totalTestCount = findTestCount();
        initialized = true;
    }

    @SuppressWarnings("unchecked")
    private int findTestCount() {
        int count = 0;
        Class superClass = this.getClass();
        for (; Test.class.isAssignableFrom(superClass); superClass = superClass.getSuperclass()) {
            Method[] methods = superClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method each = methods[i];
                if (each.getParameterTypes().length == 0 && each.getName().startsWith("test") && each.getReturnType().equals(Void.TYPE)) {
                    count++;
                }
            }
        }
        return count;
    }

    @After
    public void tearDown() throws Exception {
        currentExecutedTestCount++;
        logger.info("Executed Test " + currentExecutedTestCount + '/' + totalTestCount);
        if (currentExecutedTestCount == totalTestCount) {
            destroy();
        }
    }

    protected void destroy() {
        logger.info("Destroying Services");
        factory.destroySingletons();
        initialized = false;
    }

    @Test
    public void testAdd() throws ServiceException {
        T object = createObject();
        service.save(object);
        assertNotNull("Generated id is null !", extractId(object));
        logger.info("Inserted object with id: " + extractId(object));
    }

    @Test
    public void testList() throws ServiceException {
        List<T> objects = service.list();
        assertSame(NO_ITEM_RETREIVED_MSG, objects.isEmpty(), false);
        logger.info("Objects extracted count : " + objects.size());
    }

    @Test
    public void testGet() throws ServiceException {
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        Serializable id = extractId(objects.get(0));
        logger.info("Getting object with id: " + id);
        T entity = service.get(id);
        assertNotNull("Returned object is null", entity);
    }

    @Test
    public void testUpdate() throws ServiceException {
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

    @Test
    public void testSearch() throws ServiceException {
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
        List<T> objects = service.list();
        if (objects.isEmpty()) {
            fail(NO_ITEM_RETREIVED_MSG);
        }
        Serializable id = extractId(objects.get(0));
        logger.info("Deleting object with id: " + id);
        service.deleteById(id);
        service.get(id);
//        try {
//            fail("Object should not exists !");
//        } catch (ServiceException e) {
//            // Ok, object is deleted
//        }
    }

    @Test(expected=ServiceException.class)
    public void testDelete() throws ServiceException {
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
//        try {
//            fail("Object should not exists !");
//        } catch (ServiceException e) {
//            // Ok, object is deleted
//        }
    }

    protected abstract T createObject() throws ServiceException;

    protected abstract void updateObject(T object);

    protected abstract T createSearchObject(T fromObject);

    protected abstract Serializable extractId(T object);

    protected abstract Service<T> getService() throws ServiceException;
}
