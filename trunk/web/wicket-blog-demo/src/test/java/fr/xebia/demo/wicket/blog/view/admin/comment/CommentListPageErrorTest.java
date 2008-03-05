package fr.xebia.demo.wicket.blog.view.admin.comment;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public class CommentListPageErrorTest extends WicketPageTest {

    @Before
    public void setUpAppContext() {
        CommentService commentService = getCommentService();
        commentService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("commentService", commentService);
    }

    protected CommentService getCommentService() {
        CommentService commentService = new CommentService() {
            @Override
            public List<Comment> search(Comment exampleEntity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
            @Override
            public void save(Comment exampleEntity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
            @Override
            public Comment get(Serializable id) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        return commentService;
    }

    @Test
    public void testErrorRender() {
        tester.startPage(CommentListPage.class);
        tester.assertRenderedPage(CommentListPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("commentForm", SearchCommentForm.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("commentForm");
        // set the parameters for each component in the form
        form.setValue("author", "test");
        form.setValue("email", "Test");
        // all set, submit
        form.submit();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(CommentListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
