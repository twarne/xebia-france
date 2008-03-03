package fr.xebia.demo.wicket.blog.view.admin.comment;

import static org.junit.Assert.assertTrue;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.view.util.CustomDateTimeField;

public class EditCommentPageTest extends ViewCommentPageTest {

    @Test
    @Override
    public void testRender() throws Exception {
        super.testRender();
        
        tester.assertComponent("editLink", Link.class);
        tester.clickLink("editLink");
        tester.assertRenderedPage(EditCommentPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("commentForm", EditCommentForm.class);
        tester.assertComponent("commentForm:idValue", Label.class);
        tester.assertComponent("commentForm:author", TextField.class);
        tester.assertComponent("commentForm:date", CustomDateTimeField.class);
        tester.assertComponent("commentForm:email", TextField.class);
        tester.assertComponent("commentForm:approved", CheckBox.class);
        tester.assertComponent("commentForm:postId", Label.class);
        tester.assertComponent("commentForm:content", TextArea.class);
        tester.assertComponent("commentForm:submitButton", Button.class);

        TextField nameField = (TextField) tester.getComponentFromLastRenderedPage("commentForm:author");
        assertTrue("Field Form is not correctly filled", nameField.getModelObjectAsString().equals("test"));
        TextField nicenameField = (TextField) tester.getComponentFromLastRenderedPage("commentForm:email");
        assertTrue("Field Form is not correctly filled", nicenameField.getModelObjectAsString().equals("none@nowhere.com"));

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("commentForm");
        // set the parameters for each component in the form
        form.setValue("author", "Me");
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(CommentListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();

        tester.assertComponent("comments:0:email", Label.class);
        tester.assertLabel("comments:0:email", "none@nowhere.com");
        tester.assertComponent("comments:0:author", Label.class);
        tester.assertLabel("comments:0:author", "Me");
    }
}
