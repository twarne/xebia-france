package fr.xebia.demo.wicket.blog.view.admin.comment;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.view.util.CustomDateField;

public class CommentListPageTest extends CommentPageTest {

    @Test
    public void testRender() throws Exception {
        tester.startPage(CommentListPage.class);
        tester.assertRenderedPage(CommentListPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("commentForm", SearchCommentForm.class);
        tester.assertComponent("commentForm:author", TextField.class);
        tester.assertComponent("commentForm:date", CustomDateField.class);
        tester.assertComponent("commentForm:email", TextField.class);
        tester.assertComponent("commentForm:approved", DropDownChoice.class);
        tester.assertComponent("commentForm:postId", TextField.class);
        tester.assertComponent("commentForm:content", TextField.class);
        tester.assertComponent("commentForm:submitButton", Button.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("commentForm");
        // set the parameters for each component in the form
        form.setValue("author", "test");
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(CommentListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();
    }
}
