package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class CategoryListPageTest extends CategoryPageTest {

    @Test
    public void testRender() {
        tester.startPage(ListCategoryPage.class);
        tester.assertRenderedPage(ListCategoryPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("categoryForm", SearchCategoryForm.class);
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
        tester.assertRenderedPage(ListCategoryPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();
    }
}
