package fr.xebia.demo.wicket.blog.view;

import java.util.Date;
import java.util.Random;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class HomePageTest extends WicketPageTest {

    @BeforeClass
    public static void setUpAppContext() {
        PostService postService = new PostService();
        postService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("postService", postService);

        CommentService commentService = new CommentService();
        commentService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("commentService", commentService);
    }
    
    @Test
    public void testRender() {
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("welcomeMessage", Label.class);
        tester.assertComponent("titleLink", BookmarkablePageLink.class);
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("menuItems", ListView.class);
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
        
        testRender();
        
        tester.assertComponent("posts:0:title", Label.class);
        tester.assertLabel("posts:0:title", "Test title");
        tester.assertComponent("posts:0:author", Label.class);
        tester.assertLabel("posts:0:author", "Test");
        tester.assertComponent("posts:0:content", Label.class);
        tester.assertLabel("posts:0:content", "Content");

        tester.assertComponent("posts:0:comments:0:author", Label.class);
        tester.assertLabel("posts:0:comments:0:author", "Me");
        tester.assertComponent("posts:0:comments:0:content", Label.class);
        tester.assertLabel("posts:0:comments:0:content", "Comment");
    }
}
