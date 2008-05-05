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

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import fr.xebia.demo.wicket.blog.data.Post;

@Repository("postService")
public class PostService extends GenericService<Post> {

    private static final int POST_PER_PAGE = 10;

    private static final Logger logger = Logger.getLogger(PostService.class);

    @Override
    protected Class<Post> getObjectClass() {
        return Post.class;
    }

    @Override
    protected Serializable getObjectId(Post object) {
        return object.getId();
    }

    @SuppressWarnings("unchecked")
    public List<Post> getLastPosts() throws ServiceException {
        try {
            Session session = ((Session) currentEntityManager().getDelegate());
            Criteria criteria = session.createCriteria(getObjectClass())
                .add(Expression.eq("status", "published"))
                .addOrder(Order.desc("date"))
                .setMaxResults(POST_PER_PAGE)
                .setCacheable(false);
            return criteria.list();
        } catch (PersistenceException e) {
            logger.error(e.getCause(), e);
            throw new ServiceException("Can't get last posts", e);
        } finally {
            closeEntityManager();
        }
    }

    @Override
    protected Post merge(Post loadedObject, Post updatedObject) {
        loadedObject.setCommentsAllowed(updatedObject.getCommentsAllowed());
        loadedObject.setContent(updatedObject.getContent());
        loadedObject.setDate(updatedObject.getDate());
        loadedObject.setModified(updatedObject.getModified());
        loadedObject.setAuthor(updatedObject.getAuthor());
        loadedObject.setStatus(updatedObject.getStatus());
        loadedObject.setTitle(updatedObject.getTitle());
        return loadedObject;
    }
}
