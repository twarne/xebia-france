package fr.xebia.demo.wicket.blog.view.admin.comment;

import static org.junit.Assert.fail;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class ViewCommentPageTest extends CommentListPageTest {

    private static final Logger logger = Logger.getLogger(ViewCommentPageTest.class);
    
    @Test
    @Override
    public void testRender() {
        
        CommentService commentService = (CommentService) appContext.getBean("commentService");
        Comment comment = new Comment();
        comment.setAuthor("test");
        comment.setDate(new Date());
        comment.setEmail("none@nowhere.com");
        comment.setApproved(Boolean.FALSE);
        comment.setPostId(Long.valueOf(1));
        comment.setContent("Hello guys !");
        try {
			commentService.save(comment);
		} catch (ServiceException e) {
			logger.error(e);
			fail("Error while creating fixture");
		}
        super.testRender();
        
        tester.assertComponent("comments:0:viewLink", Link.class);
        tester.clickLink("comments:0:viewLink");
        tester.assertRenderedPage(ViewCommentPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("author", Label.class);
        tester.assertComponent("date", Label.class);
        tester.assertComponent("email", Label.class);
        tester.assertComponent("approved", Label.class);
        tester.assertComponent("postId", Label.class);
        tester.assertComponent("content", Label.class);
    }
}
