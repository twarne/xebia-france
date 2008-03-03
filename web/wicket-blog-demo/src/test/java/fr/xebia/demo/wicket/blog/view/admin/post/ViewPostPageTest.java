package fr.xebia.demo.wicket.blog.view.admin.post;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Test;

public class ViewPostPageTest extends AddPostPageTest {

    @Test
    public void testRender() {
        super.testRender();

        tester.assertComponent("posts:0:viewLink", Link.class);
        tester.clickLink("posts:0:viewLink");
        tester.assertRenderedPage(ViewPostPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("id", Label.class);
        tester.assertComponent("author", Label.class);
        tester.assertComponent("title", Label.class);
        tester.assertComponent("status", Label.class);
        tester.assertComponent("date", Label.class);
        tester.assertComponent("modified", Label.class);
        tester.assertComponent("commentStatus", Label.class);
        tester.assertComponent("category", Label.class);
        tester.assertComponent("content", MultiLineLabel.class);
        
        tester.assertLabel("title", "test");
        tester.assertLabel("author", "Test");
    }
}
