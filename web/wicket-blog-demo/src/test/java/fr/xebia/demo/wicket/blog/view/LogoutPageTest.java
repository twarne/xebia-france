package fr.xebia.demo.wicket.blog.view;

import org.junit.Test;

public class LogoutPageTest extends HomePageTest {

    @Override
    @Test
    public void testRender() {
        tester.startPage(LogoutPage.class);
        tester.assertRenderedPage(HomePage.class);
        tester.assertNoErrorMessage();
    }

}
