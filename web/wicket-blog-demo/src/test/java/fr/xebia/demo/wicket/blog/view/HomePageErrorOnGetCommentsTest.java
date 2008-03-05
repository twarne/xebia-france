package fr.xebia.demo.wicket.blog.view;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class HomePageErrorOnGetCommentsTest extends WicketPageTest {

    @Before
    public void setUpAppContext() {
        PostService postService = new PostService();
        postService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("postService", postService);

        CommentService commentService = new CommentService() {
            @Override
            public List<Comment> getCommentsForPostId(Long postId) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        commentService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("commentService", commentService);
    }
    
    @Test
    public void testRenderWithData() throws ServiceException {
        Random randomizer = new Random();
        Post post = new Post();
        post.setCommentsAllowed(randomizer.nextBoolean());
        post.setContent("Content");
        post.setDate(new Date());
        post.setModified(new Date());
        post.setAuthor("Test");
        post.setStatus(Post.STATUS_PUBLISHED);
        post.setTitle("Test title");
        ((PostService)appContext.getBean("postService")).save(post);
        Long postId = post.getId();
        
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setApproved(Boolean.TRUE);
        comment.setAuthor("Me");
        comment.setEmail("none@nowhere.com");
        comment.setContent("Comment");
        comment.setDate(new Date());
        ((CommentService)appContext.getBean("commentService")).save(comment);
        
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
