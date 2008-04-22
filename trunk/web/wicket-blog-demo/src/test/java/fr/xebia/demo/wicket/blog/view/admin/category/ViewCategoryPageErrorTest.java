package fr.xebia.demo.wicket.blog.view.admin.category;

import java.io.Serializable;

import org.apache.wicket.markup.html.link.Link;
import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public class ViewCategoryPageErrorTest extends WicketPageTest {

    @Before
    public void setUpAppContext() throws ServiceException {
        CategoryService categoryService = getCategoryService();
        categoryService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("categoryService", categoryService);
        Category category = new Category();
        category.setName("test");
        category.setNicename("Test");
        categoryService.save(category);
    }

    protected CategoryService getCategoryService() {
        CategoryService categoryService = new CategoryService() {
            @Override
            public Category get(Serializable id) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        return categoryService;
    }

    @Test
    public void testErrorRender() {
        tester.startPage(ListCategoryPage.class);
        tester.assertRenderedPage(ListCategoryPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("categories:0:viewLink", Link.class);
        tester.clickLink("categories:0:viewLink");
        tester.assertRenderedPage(ListCategoryPage.class);
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
