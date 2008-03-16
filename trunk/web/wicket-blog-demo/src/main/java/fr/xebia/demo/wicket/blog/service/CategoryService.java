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

//import javax.annotation.Resource;
//import javax.persistence.EntityManagerFactory;

import fr.xebia.demo.wicket.blog.data.Category;
//import org.springframework.stereotype.Service;

//@Service
public class CategoryService extends GenericService<Category> {

//    @Resource(name="entityManagerFactory")
//    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
//        super.setEntityManagerFactory(entityManagerFactory);
//    }

    @Override
    protected Class<Category> getObjectClass() {
        return Category.class;
    }

    @Override
    protected Serializable getObjectId(Category object) {
        return object.getId();
    }

    @Override
    protected Category merge(Category loadedObject, Category updatedObject) {
        loadedObject.setDescription(updatedObject.getDescription());
        loadedObject.setName(updatedObject.getName());
        loadedObject.setNicename(updatedObject.getNicename());
        return loadedObject;
    }
}
