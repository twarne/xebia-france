package fr.xebia.demo.wicket.blog.view.admin.post;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class AddPostPageErrorTest extends PostListPageErrorTest {

    protected static final String POST_FORM_ID = "postForm";

    @Test
    public void testValidationErrorRender() {
        super.testErrorRender();
        tester.assertComponent("addLink", Link.class);
        tester.clickLink("addLink");
        tester.assertRenderedPage(AddPostPage.class);
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent(POST_FORM_ID, AddPostForm.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester(POST_FORM_ID);
        // set the parameters for each component in the form
        form.setValue("author", "test");
        form.setValue("title", "Test");
        // all set, submit
        form.submit();
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE, "Field 'content' is required" });
    }

    @Test
    public void testErrorRender() {
        super.testErrorRender();
        
        tester.assertComponent("addLink", Link.class);
        tester.clickLink("addLink");
        tester.assertRenderedPage(AddPostPage.class);
        tester.assertErrorMessages( new String[] { ERROR_MESSAGE });
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent(POST_FORM_ID, AddPostForm.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester(POST_FORM_ID);
        // set the parameters for each component in the form
        form.setValue("author", "test");
        form.setValue("title", "Test");
        form.setValue("content", "Test");
        // all set, submit
        form.submit();
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
