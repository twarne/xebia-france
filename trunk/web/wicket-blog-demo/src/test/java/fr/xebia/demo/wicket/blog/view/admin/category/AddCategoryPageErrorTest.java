package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class AddCategoryPageErrorTest extends CategoryListPageErrorTest {

    @Test
    public void testValidationErrorRender() {
        super.testErrorRender();
        
        tester.assertComponent("addLink", Link.class);
        tester.clickLink("addLink");
        tester.assertRenderedPage(AddCategoryPage.class);
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("categoryForm", AddCategoryForm.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("categoryForm");
        // set the parameters for each component in the form
        form.setValue("name", "test");
        // all set, submit
        form.submit();
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE, "Field 'nicename' is required" });
    }

    @Test
    public void testErrorRender() {
        super.testErrorRender();
        
        tester.assertComponent("addLink", Link.class);
        tester.clickLink("addLink");
        tester.assertRenderedPage(AddCategoryPage.class);
        tester.assertErrorMessages( new String[] { ERROR_MESSAGE });
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
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
