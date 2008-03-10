package fr.xebia.demo.wicket.blog.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class AddCommentPageErrorTest extends WicketPageTest {

    protected static final String COMMENT_FORM_ID = "commentForm";

    @BeforeClass
    public static void setUpAppContext() {
        CommentService commentService = new CommentService() {
            @Override
            public void save(Comment entity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        commentService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("commentService", commentService);
    }

    @Test
    public void testValidationErrorRender() {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(AddCommentPage.PARAM_POSTID_KEY, Long.valueOf(1));
        tester.startPage(AddCommentPage.class, pageParameters);
        tester.assertRenderedPage(AddCommentPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent(COMMENT_FORM_ID, AddCommentForm.class);
        tester.assertComponent("commentForm:author", TextField.class);
        tester.assertComponent("commentForm:email", TextField.class);
        tester.assertComponent("commentForm:postId", Label.class);
        tester.assertComponent("commentForm:content", TextArea.class);
        tester.assertComponent("commentForm:submitButton", Button.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester(COMMENT_FORM_ID);
        // set the parameters for each component in the form
        form.setValue("author", "Test");
        form.setValue("email", "none@nowhere.com");
        // all set, submit
        form.submit();
        tester.assertErrorMessages(new String[] { "Field 'content' is required" });
    }

    @Test
    public void testRender() {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(AddCommentPage.PARAM_POSTID_KEY, Long.valueOf(1));
        tester.startPage(AddCommentPage.class, pageParameters);
        tester.assertRenderedPage(AddCommentPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent(COMMENT_FORM_ID, AddCommentForm.class);
        tester.assertComponent("commentForm:author", TextField.class);
        tester.assertComponent("commentForm:email", TextField.class);
        tester.assertComponent("commentForm:postId", Label.class);
        tester.assertComponent("commentForm:content", TextArea.class);
        tester.assertComponent("commentForm:submitButton", Button.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester(COMMENT_FORM_ID);
        // set the parameters for each component in the form
        form.setValue("author", "Test");
        form.setValue("email", "none@nowhere.com");
        form.setValue("content", "Comment World !");
        // all set, submit
        form.submit();
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
