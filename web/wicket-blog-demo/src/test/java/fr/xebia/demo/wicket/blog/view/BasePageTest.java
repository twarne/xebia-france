package fr.xebia.demo.wicket.blog.view;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasePageTest {

    private static WicketTester tester;

    @BeforeClass
    public static void setUpClass() {
        tester = new WicketTester();
    }
    
    @Test
    public void testBasePage() {
        String[] expectedInfoMessages = { "infoMessage" };
        String[] expectedErrorMessages = { "errorMessage", "exceptionMessage" };
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(BasePage.PARAM_MESSAGE_KEY, expectedInfoMessages[0]);
        pageParameters.put(BasePage.PARAM_ERRORMESSAGE_KEY, expectedErrorMessages[0]);
        pageParameters.put(BasePage.PARAM_EXCEPTION_KEY, new Exception(expectedErrorMessages[1]));
        tester.startPage(TestPage.class, pageParameters);
        tester.assertRenderedPage(TestPage.class);
		tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertInfoMessages(expectedInfoMessages);
        tester.assertErrorMessages(expectedErrorMessages);
    }

    public static class TestPage extends BasePage {
        private static final long serialVersionUID = 1L;

        public TestPage(PageParameters pageParameters) {
            super(pageParameters);
        }

        @Override
        protected List<MenuItem> getMenuItems() {
            return new LinkedList<MenuItem>();
        }
    }
}
