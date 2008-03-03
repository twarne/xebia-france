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

import fr.xebia.demo.wicket.blog.data.Category;

public class CategoryServiceTest extends AbstractServiceTest<Category> {

    /**
     * @see org.xebia.service.ServiceTestCase#createOneObject()
     */
    @Override
    protected Category createObject() throws ServiceException {
        Category category = new Category();
        category.setDescription(String.valueOf(randomizer.nextInt(2147483647)));
        category.setName(String.valueOf(randomizer.nextInt(55)));
        category.setNicename(String.valueOf(randomizer.nextInt(200)));
        return category;
    }

    /**
     * @see org.xebia.service.ServiceTestCase#updateObject()
     */
    @Override
    protected void updateObject(Category object) {
        object.setDescription(String.valueOf(randomizer.nextInt(2147483647)));
        object.setName(String.valueOf(randomizer.nextInt(55)));
        object.setNicename(String.valueOf(randomizer.nextInt(200)));
    }

    @Override
    protected Category createSearchObject(Category fromObject) {
        Category object = new Category();
        object.setDescription(fromObject.getDescription());
        object.setName(fromObject.getName());
        object.setNicename(fromObject.getNicename());
        return object;
    }

    /**
     * @see org.xebia.service.ServiceTestCase#extractId(java.lang.Object)
     */
    @Override
    protected Serializable extractId(Category object) {
        return object.getId();
    }

    /**
     * @see org.xebia.service.ServiceTestCase#getService(ServiceLocator)
     */
    @Override
    protected Service<Category> getService() throws ServiceException {
        return serviceLocator.getCategoryService();
    }

}
