package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Test;

public class ViewCategoryPageTest extends AddCategoryPageTest {

    @Test
    public void testRender() {
        super.testRender();

        tester.assertComponent("categories:0:viewLink", Link.class);
//        Link viewLink = (Link) tester.getComponentFromLastRenderedPage("categories:0:viewLink");
        tester.clickLink("categories:0:viewLink");
        tester.assertRenderedPage(ViewCategoryPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("name", Label.class);
        tester.assertComponent("nicename", Label.class);
        tester.assertComponent("description", Label.class);
        
        tester.assertLabel("name", "test");
        tester.assertLabel("nicename", "Test");
    }
}
