package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.wicket.markup.html.link.Link;
import org.junit.Test;

public class DeleteCategoryPageTest extends AddCategoryPageTest {

	@Test
	@Override
	public void testRender() {
		super.testRender();
		tester.assertComponent("categories:0:deleteLink", Link.class);
		tester.clickLink("categories:0:deleteLink");
		tester.assertRenderedPage(ListCategoryPage.class);
		tester.assertNoErrorMessage();
	}
}
