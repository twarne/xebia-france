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

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.data.Post;

public class CommentServiceTest extends AbstractServiceTest<Comment> {

    @Test
    public void testGetCommentsForPostId() throws ServiceException {
        Service<Post> postService = serviceLocator.getPostService();
        Post post = new Post();
        post.setCommentsAllowed(randomizer.nextBoolean());
        post.setContent(String.valueOf(randomizer.nextInt(2147483647)));
        post.setDate(new Date());
        post.setModified(new Date());
        post.setAuthor(String.valueOf(randomizer.nextLong()));
        post.setStatus(String.valueOf(randomizer.nextInt(10)));
        post.setTitle(String.valueOf(randomizer.nextInt(65535)));
        postService.save(post);
        Long postId = post.getId();
        Comment comment = createObject();
        comment.setPostId(postId);
        comment.setApproved(Boolean.TRUE);
        CommentService commentService = (CommentService) getService();
        commentService.save(comment);
        List<Comment> comments = commentService.getCommentsForPostId(postId);
        assertFalse("getCommentsForPostId() should bring back almost one comment", comments.isEmpty());
    }

    @Override
    protected Comment createObject() {
        Comment comment = new Comment();
        comment.setApproved(randomizer.nextBoolean());
        comment.setAuthor(String.valueOf(randomizer.nextInt(255)));
        comment.setEmail(String.valueOf(randomizer.nextInt(100)));
        comment.setContent(String.valueOf(randomizer.nextInt(65535)));
        comment.setDate(new Date());
        comment.setPostId(randomizer.nextLong());
        return comment;
    }

    @Override
    protected Comment createDirtyObject() {
        return new Comment();
    }

    @Override
    protected void updateToDirtyObject(Comment object) {
        object.setAuthor(null);
        object.setEmail(null);
    }

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

    @Override
    protected Serializable extractId(Comment object) {
        return object.getId();
    }

    @Override
    protected Service<Comment> getService() {
        return serviceLocator.getCommentService();
    }

}
