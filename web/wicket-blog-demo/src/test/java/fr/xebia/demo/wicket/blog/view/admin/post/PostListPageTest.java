package fr.xebia.demo.wicket.blog.view.admin.post;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.view.util.CustomDateField;

public class PostListPageTest extends PostPageTest {

    @Test
    public void testRender() {
        tester.startPage(PostListPage.class);
        tester.assertRenderedPage(PostListPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("postForm", SearchPostForm.class);
        tester.assertComponent("postForm:title", TextField.class);
        tester.assertComponent("postForm:status", DropDownChoice.class);
        tester.assertComponent("postForm:date", CustomDateField.class);
        tester.assertComponent("postForm:modified", CustomDateField.class);
        tester.assertComponent("postForm:author", TextField.class);
        tester.assertComponent("postForm:content", TextField.class);
        tester.assertComponent("postForm:category", DropDownChoice.class);
        tester.assertComponent("postForm:submitButton", Button.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("postForm");
        // set the parameters for each component in the form
        form.setValue("title", "test");
        form.setValue("author", "Test");
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(PostListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();
    }
}
