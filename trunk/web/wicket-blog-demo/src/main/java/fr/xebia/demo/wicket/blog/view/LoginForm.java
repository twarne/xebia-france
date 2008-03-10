package fr.xebia.demo.wicket.blog.view;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.User;
import fr.xebia.demo.wicket.blog.service.LoginService;
import fr.xebia.demo.wicket.blog.view.admin.AdminHomePage;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class LoginForm extends Form {

    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(LoginForm.class);

    @SpringBean(name = "loginService")
    private transient LoginService loginService;

    private final User user;

    public LoginForm(String id) {
        super(id);
        user = new User();
        createComponents();
    }

    private void createComponents() {
        TextField userField = new TextField("user", new PropertyModel(user, "login"));
        userField.setRequired(true);
        add(userField);

        PasswordTextField passwordField = new PasswordTextField("password", new PropertyModel(user, "password"));
        passwordField.setRequired(true);
        add(passwordField);

        add(new Button("submitButton", new StringResourceModel("login.submitButton", this, null)));
    }

    @Override
    protected void onSubmit() {
        BlogWebSession session = (BlogWebSession) getSession();
        if (loginService.authenticate(user)) {
            session.setUser(user);
            // L'appel à continueToOriginalDestination() redirige vers la page
            // stocker via l'appel à redirectToInterceptPage(page)
            // ou la levée de l'exception RestartResponseAtInterceptPageException
            if (continueToOriginalDestination()) {
                logger.debug("Redirecting to original page");
            } else { 
                setResponsePage(AdminHomePage.class);
            }
        } else {
            session.clearUser();
            throw new RestartResponseException(LoginPage.class, PageParametersUtils.fromStringErrorMessage("Invalid user or password !"));
        }
    }
}
