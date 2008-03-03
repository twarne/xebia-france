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

/**
 * 
 */
public interface Service<T> {

    /**
     * Renvoi le nombre d'enregistrement maximum ramené par une recherche ou une récupératio nde liste d'objets
     */
    public abstract int getMaxResults();

    public abstract void save(T entity) throws ServiceException;

    public abstract T update(T entity) throws ServiceException;

    public abstract void deleteById(Serializable id) throws ServiceException;

    public abstract void delete(T entity) throws ServiceException;

    public abstract T get(Serializable id) throws ServiceException;

    public abstract List<T> list() throws ServiceException;

    public abstract List<T> search(T exampleEntity) throws ServiceException;
}
