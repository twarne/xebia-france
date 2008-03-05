package fr.xebia.demo.wicket.blog.view.admin.category;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public class CategoryListPageErrorTest extends WicketPageTest {

    @Before
    public void setUpAppContext() {
        CategoryService categoryService = getCategoryService();
        categoryService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("categoryService", categoryService);
    }

    protected CategoryService getCategoryService() {
        CategoryService categoryService = new CategoryService() {
            @Override
            public List<Category> search(Category exampleEntity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
            @Override
            public void save(Category exampleEntity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
            @Override
            public Category get(Serializable id) throws ServiceException {
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
        tester.assertComponent("categoryForm", SearchCategoryForm.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("categoryForm");
        // set the parameters for each component in the form
        form.setValue("name", "test");
        form.setValue("nicename", "Test");
        // all set, submit
        form.submit();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(CategoryListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
