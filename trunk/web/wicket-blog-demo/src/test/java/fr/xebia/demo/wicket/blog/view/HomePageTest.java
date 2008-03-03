package fr.xebia.demo.wicket.blog.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.PostService;


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

}
