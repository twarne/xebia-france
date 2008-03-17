package fr.xebia.demo.wicket.blog.view.admin.post;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class AddPostPageTest extends PostListPageTest {

    @Override
    @Test
    public void testRender() {
        super.testRender();

        tester.assertComponent("addLink", Link.class);
        tester.clickLink("addLink");
        tester.assertRenderedPage(AddPostPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("postForm", AddPostForm.class);
        tester.assertComponent("postForm:commentsAllowed", CheckBox.class);
        tester.assertComponent("postForm:title", TextField.class);
        tester.assertComponent("postForm:status", RadioChoice.class);
        tester.assertComponent("postForm:author", TextField.class);
        tester.assertComponent("postForm:category", DropDownChoice.class);
        tester.assertComponent("postForm:content", TextArea.class);
        tester.assertComponent("postForm:submitButton", Button.class);
        
        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("postForm");
        // set the parameters for each component in the form
        form.setValue("title", "test");
        form.setValue("author", "Test");
        form.setValue("content", "Hello World !");
        form.select("status", 0);
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(PostListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();

        tester.assertComponent("posts:0:title", Label.class);
        tester.assertLabel("posts:0:title", "test");
        tester.assertComponent("posts:0:author", Label.class);
        tester.assertLabel("posts:0:author", "Test");
        tester.assertComponent("posts:0:status", Label.class);
        tester.assertLabel("posts:0:status", "draft");
    }
}
