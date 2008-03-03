package fr.xebia.demo.wicket.blog.view.util;

import org.apache.wicket.PageParameters;

import fr.xebia.demo.wicket.blog.view.BasePage;


public class PageParametersUtils {

    public static PageParameters fromStringMessage(String message) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(BasePage.PARAM_MESSAGE_KEY, message);
        return pageParameters;
    }

    public static PageParameters fromStringErrorMessage(String errorMessage) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(BasePage.PARAM_ERRORMESSAGE_KEY, errorMessage);
        return pageParameters;
    }

    public static PageParameters fromException(Throwable t) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(BasePage.PARAM_EXCEPTION_KEY, t);
        return pageParameters;
    }
}
