package fr.xebia.demo.wicket.blog.view.admin.post;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.springframework.beans.BeansException;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class EditPostPageTest extends ViewPostPageTest {

    @Override
    @Test
    public void testRender() {
        try {
			Category category = new Category();
			category.setName("test");
			category.setNicename("Test");
			CategoryService categoryService = (CategoryService) appContext.getBean("categoryService");
			categoryService.save(category);
		} catch (BeansException e) {
			fail(e.toString());
		} catch (ServiceException e) {
			fail(e.toString());
		}

		super.testRender();

        tester.assertComponent("editLink", Link.class);
        tester.clickLink("editLink");
        tester.assertRenderedPage(EditPostPage.class);
        tester.assertNoErrorMessage();
        
        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("postForm", EditPostForm.class);
        tester.assertComponent("postForm:idValue", Label.class);
        tester.assertComponent("postForm:category", DropDownChoice.class);
        tester.assertComponent("postForm:commentsAllowed", CheckBox.class);
        tester.assertComponent("postForm:title", TextField.class);
        tester.assertComponent("postForm:status", RadioChoice.class);
        tester.assertComponent("postForm:author", TextField.class);
        tester.assertComponent("postForm:content", TextArea.class);
        tester.assertComponent("postForm:submitButton", Button.class);

        TextField nameField = (TextField) tester.getComponentFromLastRenderedPage("postForm:title");
        assertTrue("Field Form is not correctly filled", nameField.getModelObjectAsString().equals("test"));
        TextField nicenameField = (TextField) tester.getComponentFromLastRenderedPage("postForm:author");
        assertTrue("Field Form is not correctly filled", nicenameField.getModelObjectAsString().equals("Test"));

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("postForm");
        // set the parameters for each component in the form
        form.setValue("author", "Me");
        form.select("status", 1);
        form.select("category", 0);
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(ListPostPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();

        tester.assertComponent("posts:0:title", Label.class);
        tester.assertLabel("posts:0:title", "test");
        tester.assertComponent("posts:0:author", Label.class);
        tester.assertLabel("posts:0:author", "Me");
        tester.assertComponent("posts:0:status", Label.class);
        tester.assertLabel("posts:0:status", "published");
    }
}
