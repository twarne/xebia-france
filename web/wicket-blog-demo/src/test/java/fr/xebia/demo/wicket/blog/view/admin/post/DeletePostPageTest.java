package fr.xebia.demo.wicket.blog.view.admin.post;

import org.apache.wicket.markup.html.link.Link;
import org.junit.Test;

public class DeletePostPageTest extends AddPostPageTest {

	@Test
	@Override
	public void testRender() {
		super.testRender();
		tester.assertComponent("posts:0:deleteLink", Link.class);
		tester.clickLink("posts:0:deleteLink");
		tester.assertRenderedPage(PostListPage.class);
		tester.assertNoErrorMessage();
	}
}
