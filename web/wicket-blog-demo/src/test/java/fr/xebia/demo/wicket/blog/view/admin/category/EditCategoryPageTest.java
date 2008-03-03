package fr.xebia.demo.wicket.blog.view.admin.category;

import static org.junit.Assert.assertTrue;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class EditCategoryPageTest extends ViewCategoryPageTest {

    @Test
    public void testRender() {
        super.testRender();

        tester.assertComponent("editLink", Link.class);
        tester.clickLink("editLink");
        tester.assertRenderedPage(EditCategoryPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("categoryForm", EditCategoryForm.class);
        tester.assertComponent("categoryForm:idValue", Label.class);
        tester.assertComponent("categoryForm:name", TextField.class);
        tester.assertComponent("categoryForm:nicename", TextField.class);
        tester.assertComponent("categoryForm:description", TextField.class);
        tester.assertComponent("categoryForm:submitButton", Button.class);

        TextField nameField = (TextField) tester.getComponentFromLastRenderedPage("categoryForm:name");
        assertTrue("Field Form is not correctly filled", nameField.getModelObjectAsString().equals("test"));
        TextField nicenameField = (TextField) tester.getComponentFromLastRenderedPage("categoryForm:nicename");
        assertTrue("Field Form is not correctly filled", nicenameField.getModelObjectAsString().equals("Test"));

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("categoryForm");
        // set the parameters for each component in the form
        form.setValue("description", "Test category");
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(CategoryListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();

        tester.assertComponent("categories:0:description", Label.class);
        tester.assertLabel("categories:0:description", "Test category");
    }
}
