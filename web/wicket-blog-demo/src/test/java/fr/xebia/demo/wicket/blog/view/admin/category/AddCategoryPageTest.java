package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class AddCategoryPageTest extends CategoryListPageTest {

    @Test
    public void testRender() {
        super.testRender();
        
        tester.assertComponent("addLink", Link.class);
        tester.clickLink("addLink");
        tester.assertRenderedPage(AddCategoryPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("categoryForm", AddCategoryForm.class);
        tester.assertComponent("categoryForm:name", TextField.class);
        tester.assertComponent("categoryForm:nicename", TextField.class);
        tester.assertComponent("categoryForm:description", TextField.class);
        tester.assertComponent("categoryForm:submitButton", Button.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("categoryForm");
        // set the parameters for each component in the form
        form.setValue("name", "test");
        form.setValue("nicename", "Test");
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(CategoryListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();

        tester.assertComponent("categories:0:name", Label.class);
        tester.assertLabel("categories:0:name", "test");
        tester.assertComponent("categories:0:nicename", Label.class);
        tester.assertLabel("categories:0:nicename", "Test");
    }
}
