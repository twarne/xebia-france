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
import java.util.Date;

import fr.xebia.demo.wicket.blog.data.Comment;

public class CommentServiceTest extends AbstractServiceTest<Comment> {

    /**
     * @see org.xebia.service.ServiceTestCase#createOneObject()
     */
    @Override
    protected Comment createObject() throws ServiceException {
        Comment comment = new Comment();
        comment.setApproved(randomizer.nextBoolean());
        comment.setAuthor(String.valueOf(randomizer.nextInt(255)));
        comment.setEmail(String.valueOf(randomizer.nextInt(100)));
        comment.setContent(String.valueOf(randomizer.nextInt(65535)));
        comment.setDate(new Date());
        comment.setPostId(randomizer.nextLong());
        return comment;
    }

    /**
     * @see org.xebia.service.ServiceTestCase#updateObject()
     */
    @Override
    protected void updateObject(Comment object) {
        object.setApproved(randomizer.nextBoolean());
        object.setAuthor(String.valueOf(randomizer.nextInt(255)));
        object.setEmail(String.valueOf(randomizer.nextInt(100)));
        object.setContent(String.valueOf(randomizer.nextInt(65535)));
        object.setDate(new Date());
        object.setPostId(randomizer.nextLong());
    }

    @Override
    protected Comment createSearchObject(Comment fromObject) {
        Comment object = new Comment();
        object.setApproved(fromObject.getApproved());
        object.setAuthor(fromObject.getAuthor());
        object.setEmail(fromObject.getEmail());
        object.setContent(fromObject.getContent());
        object.setDate(fromObject.getDate());
        object.setPostId(fromObject.getPostId());
        return object;
    }

    /**
     * @see org.xebia.service.ServiceTestCase#extractId(java.lang.Object)
     */
    @Override
    protected Serializable extractId(Comment object) {
        return object.getId();
    }

    /**
     * @see org.xebia.service.ServiceTestCase#getService(ServiceLocator)
     */
    @Override
    protected Service<Comment> getService() throws ServiceException {
        return serviceLocator.getCommentService();
    }

}
