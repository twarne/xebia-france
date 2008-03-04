package fr.xebia.demo.wicket.blog.view.security;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;

public class UnauthorizedComponentInstantiationListener implements IUnauthorizedComponentInstantiationListener {

    private final Class<? extends Component> restartPageClass;

    public UnauthorizedComponentInstantiationListener(Class<? extends Component> restartPageClass) {
        super();
        this.restartPageClass = restartPageClass;
    }

    public void onUnauthorizedInstantiation(Component component) {
        if (Page.class.isAssignableFrom(component.getClass())) {
            // Redirect to the defined restartPage
            throw new RestartResponseAtInterceptPageException(restartPageClass);
        } else {
            // The component was not a page, so throw exception
            throw new UnauthorizedInstantiationException(component.getClass());
        }
    }
}
