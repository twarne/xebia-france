package fr.xebia.demo.wicket.blog.view.admin.comment;

import org.apache.wicket.markup.html.link.Link;
import org.junit.Test;

public class DeleteCommentPageTest extends ViewCommentPageTest {

	@Test
	@Override
	public void testRender() {
		super.testRender();
		tester.clickLink("backToListLink");
		tester.assertComponent("comments:0:deleteLink", Link.class);
		tester.clickLink("comments:0:deleteLink");
		tester.assertRenderedPage(CommentListPage.class);
		tester.assertNoErrorMessage();
	}
}
