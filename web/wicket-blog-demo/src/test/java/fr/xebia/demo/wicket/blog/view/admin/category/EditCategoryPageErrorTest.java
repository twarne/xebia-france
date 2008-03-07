package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.wicket.PageParameters;
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
        PageParameters pageParameters = new PageParameters();
        Category category = new Category();
        category.setName("test");
        category.setNicename("Test");
        pageParameters.put(EditCategoryPage.PARAM_CATEGORY_KEY, category);
        tester.startPage(EditCategoryPage.class, pageParameters);
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
