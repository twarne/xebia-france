package fr.xebia.demo.wicket.blog.view;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class HomePageErrorTest extends WicketPageTest {

    @Before
    public void setUpAppContext() {
        PostService postService = new PostService() {
            @Override
            public List<Post> getLastPosts() throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
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
        tester.assertErrorMessages(new String[]{ERROR_MESSAGE});

        tester.assertComponent("titleLink", BookmarkablePageLink.class);
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("menuItems", ListView.class);
    }
}
