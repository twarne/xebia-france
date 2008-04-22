package fr.xebia.demo.wicket.blog.view.admin.comment;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class DeleteCommentPageErrorTest extends CommentListPageErrorTest {

    @Override
    @Test
    public void testErrorRender() {
    	CommentService commentService = new CommentService();
    	commentService.setEntityManagerFactory(entityManagerFactory);
    	Comment comment = new Comment();
    	comment.setAuthor("test");
    	comment.setEmail("none@nowhere.com");
    	comment.setContent("Comment content");
    	comment.setDate(new Date());
    	comment.setPostId(Long.valueOf(1));
		try {
			commentService.save(comment);
		} catch (ServiceException e) {
			fail("Error while creating fixture");
		}
    	
        tester.startPage(ListCommentPage.class);
        tester.assertRenderedPage(ListCommentPage.class);
        tester.assertNoErrorMessage();
        
        tester.clickLink("comments:0:deleteLink");
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
        tester.assertRenderedPage(ListCommentPage.class);
    }
}
