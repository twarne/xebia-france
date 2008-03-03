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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import fr.xebia.demo.wicket.blog.data.Post;

/**
 * Service pour l'objet Post qui permet d'effectuer les actions standards (save, update, get, list, search, delete)
 */
public class PostService extends GenericService<Post> {

    private static final int POST_PER_PAGE = 10;

    @Override
    protected Class<Post> getObjectClass() {
        return Post.class;
    }

    @Override
    protected Serializable getObjectId(Post object) {
        return object.getId();
    }
    
    @SuppressWarnings("unchecked")
    public List<Post> getLastPosts() {

        Session session = ((Session) currentEntityManager().getDelegate());
        Criteria criteria = session.createCriteria(getObjectClass())
            .add(Expression.eq("status", "published"))
            .addOrder(Order.desc("date"))
            .setMaxResults(POST_PER_PAGE)
            .setCacheable(false);
        return criteria.list();
    }

    @Override
    protected Post merge(Post loadedObject, Post updatedObject) {
        loadedObject.setCommentsAllowed(updatedObject.getCommentsAllowed());
        loadedObject.setContent(updatedObject.getContent());
        loadedObject.setDate(updatedObject.getDate());
        loadedObject.setModified(updatedObject.getModified());
        loadedObject.setPassword(updatedObject.getPassword());
        loadedObject.setPingAllowed(updatedObject.getPingAllowed());
        loadedObject.setAuthor(updatedObject.getAuthor());
        loadedObject.setStatus(updatedObject.getStatus());
        loadedObject.setTitle(updatedObject.getTitle());
        return loadedObject;
    }
}
