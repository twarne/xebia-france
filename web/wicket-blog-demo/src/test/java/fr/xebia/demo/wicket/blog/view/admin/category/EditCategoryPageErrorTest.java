package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class EditCategoryPageErrorTest extends ViewCategoryPageErrorTest {

    protected CategoryService getCategoryService() {
        CategoryService categoryService = new CategoryService() {
            @Override
            public Category update(Category entity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        return categoryService;
    }

    @Test
    public void testErrorRender() {

        tester.startPage(CategoryListPage.class);
        tester.assertRenderedPage(CategoryListPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("categories:0:viewLink", Link.class);
        tester.clickLink("categories:0:viewLink");
        tester.assertRenderedPage(ViewCategoryPage.class);
        
        tester.assertComponent("editLink", Link.class);
        tester.clickLink("editLink");
        tester.assertRenderedPage(EditCategoryPage.class);
        tester.assertNoErrorMessage();

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("categoryForm");
        // set the parameters for each component in the form
        form.setValue("description", "Test category");
        // all set, submit
        form.submit();
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
