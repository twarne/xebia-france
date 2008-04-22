package fr.xebia.demo.wicket.blog.view.admin.comment;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class EditCommentPageErrorTest extends ViewCommentPageErrorTest {

    @Override
    protected CommentService getCommentService() {
        CommentService commentService = new CommentService() {
            @Override
            public Comment update(Comment comment) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        return commentService;
    }

    @Override
    @Test
    public void testErrorRender() {

        tester.startPage(ListCommentPage.class);
        tester.assertRenderedPage(ListCommentPage.class);
        tester.assertNoErrorMessage();
        tester.clickLink("comments:0:viewLink");
        tester.assertRenderedPage(ViewCommentPage.class);
        
        tester.clickLink("editLink");
        tester.assertRenderedPage(EditCommentPage.class);
        tester.assertNoErrorMessage();

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("commentForm");
        // set the parameters for each component in the form
        form.setValue("content", "Test category");
        // all set, submit
        form.submit();
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
