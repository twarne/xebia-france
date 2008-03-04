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

import static org.junit.Assert.assertFalse;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Post;

public class PostServiceTest extends AbstractServiceTest<Post> {
    
    @Test
    public void testGetLastPosts() throws ServiceException {
        Post post = createObject();
        post.setStatus("published");
        getService().save(post);
        PostService postService = (PostService) getService();
        List<Post> posts = postService.getLastPosts();
        assertFalse("getLastPosts() should bring back almost one post", posts.isEmpty());
    }

    /**
     * @see org.xebia.service.ServiceTestCase#createOneObject()
     */
    @Override
    protected Post createObject() {
        Post post = new Post();
        post.setCommentsAllowed(randomizer.nextBoolean());
        post.setContent(String.valueOf(randomizer.nextInt(2147483647)));
        post.setDate(new Date());
        post.setModified(new Date());
        post.setAuthor(String.valueOf(randomizer.nextLong()));
        post.setStatus(String.valueOf(randomizer.nextInt(10)));
        post.setTitle(String.valueOf(randomizer.nextInt(65535)));
        return post;
    }

    @Override
    protected Post createDirtyObject() {
        return new Post();
    }

    @Override
    protected void updateToDirtyObject(Post object) {
        object.setAuthor(null);
        object.setTitle(null);
    }

    /**
     * @see org.xebia.service.ServiceTestCase#updateObject()
     */
    @Override
    protected void updateObject(Post object) {
        object.setCommentsAllowed(randomizer.nextBoolean());
        object.setContent(String.valueOf(randomizer.nextInt(2147483647)));
        object.setDate(new Date());
        object.setModified(new Date());
        object.setAuthor(String.valueOf(randomizer.nextLong()));
        object.setStatus(String.valueOf(randomizer.nextInt(10)));
        object.setTitle(String.valueOf(randomizer.nextInt(65535)));
    }

    @Override
    protected Post createSearchObject(Post fromObject) {
        Post object = new Post();
        object.setCommentsAllowed(fromObject.getCommentsAllowed());
        object.setContent(fromObject.getContent());
        object.setDate(fromObject.getDate());
        object.setModified(fromObject.getModified());
        object.setAuthor(fromObject.getAuthor());
        object.setStatus(fromObject.getStatus());
        object.setTitle(fromObject.getTitle());
        return object;
    }

    /**
     * @see org.xebia.service.ServiceTestCase#extractId(java.lang.Object)
     */
    @Override
    protected Serializable extractId(Post object) {
        return object.getId();
    }

    /**
     * @throws ServiceException
     * @see org.xebia.service.ServiceTestCase#getService(ServiceLocator)
     */
    @Override
    protected Service<Post> getService() {
        return serviceLocator.getPostService();
    }

}
