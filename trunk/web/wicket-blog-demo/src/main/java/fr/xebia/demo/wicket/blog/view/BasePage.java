/*
 * Copyright 2007 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.demo.wicket.blog.view;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public abstract class BasePage extends WebPage {

    private static final long serialVersionUID = 1L;

    public static final String PARAM_MESSAGE_KEY = "message";

    public static final String PARAM_ERRORMESSAGE_KEY = "errorMessage";

    public static final String PARAM_EXCEPTION_KEY = "exception";

    private static final ListView menuItems = new MenuListView("menuItems");

    private final FeedbackPanel feedbackPanel;

    public BasePage(PageParameters pageParameters) {
        super(pageParameters);
        add(HeaderContributor.forCss("common/styles.css"));
        add(new BookmarkablePageLink("titleLink", Application.get().getHomePage()));

        menuItems.setList(getMenuItems());
        add(menuItems);

        feedbackPanel = new FeedbackPanel("feedbackPanel");
        add(feedbackPanel);
        handlePageParameters(pageParameters);
    }

    public boolean isSecured() {
        return false;
    }

    protected abstract List<MenuItem> getMenuItems();

    private void handlePageParameters(PageParameters pageParameters) {
        if (pageParameters == null) {
            return;
        }
        if (pageParameters.containsKey(PARAM_MESSAGE_KEY)) {
            String message = (String) pageParameters.getString(PARAM_MESSAGE_KEY);
            addInfoMessage(message);
        }
        if (pageParameters.containsKey(PARAM_ERRORMESSAGE_KEY)) {
            String errorMessage = (String) pageParameters.getString(PARAM_ERRORMESSAGE_KEY);
            addErrorMessage(errorMessage);
        }
        if (pageParameters.containsKey(PARAM_EXCEPTION_KEY)) {
            Throwable t = (Throwable) pageParameters.get(PARAM_EXCEPTION_KEY);
            addErrorMessage(t);
        }
    }

    protected final void addInfoMessage(String message) {
        feedbackPanel.info(message);
    }

    protected final void addErrorMessage(String message) {
        feedbackPanel.error(message);
    }

    protected final void addErrorMessage(Throwable t) {
        if (t == null) {
            return;
        }
        String errorMessage = StringUtils.isEmpty(t.getMessage()) ? t.toString() : t.getMessage();
        feedbackPanel.error(errorMessage);
    }
    
//    @Override
//    public BlogWebSession getSession() {
//        return (BlogWebSession) super.getSession();
//    }
}
