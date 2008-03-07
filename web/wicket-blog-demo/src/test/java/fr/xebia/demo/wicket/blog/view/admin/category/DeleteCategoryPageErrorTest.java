package fr.xebia.demo.wicket.blog.view.admin.category;

import static org.junit.Assert.fail;

import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class DeleteCategoryPageErrorTest extends CategoryListPageErrorTest {

    @Test
    public void testErrorRender() {
    	CategoryService categoryService = new CategoryService();
    	categoryService.setEntityManagerFactory(entityManagerFactory);
    	Category category = new Category();
    	category.setName("test");
    	category.setNicename("Test");
		try {
			categoryService.save(category);
		} catch (ServiceException e) {
			fail("Error while creating fixture");
		}
    	
        tester.startPage(CategoryListPage.class);
        tester.assertRenderedPage(CategoryListPage.class);
        tester.assertNoErrorMessage();
        
        tester.clickLink("categories:0:deleteLink");
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
        tester.assertRenderedPage(CategoryListPage.class);
    }
}
