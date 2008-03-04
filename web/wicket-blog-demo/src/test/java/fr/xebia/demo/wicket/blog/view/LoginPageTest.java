package fr.xebia.demo.wicket.blog.view;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.service.LoginService;
import fr.xebia.demo.wicket.blog.view.admin.AdminHomePage;


public class LoginPageTest extends WicketPageTest {

    @BeforeClass
    public static void setUpAppContext() {
        appContext.putBean("loginService", new LoginService());
    }

    @Test
    public void testRender() {
        tester.startPage(LoginPage.class);
        tester.assertRenderedPage(LoginPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("loginForm", LoginForm.class);
        tester.assertComponent("loginForm:user", TextField.class);
        tester.assertComponent("loginForm:password", PasswordTextField.class);
        
        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("loginForm");
        // set the parameters for each component in the form
        form.setValue("user", "admin");
        form.setValue("password", "admin");
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(AdminHomePage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();
    }

}
