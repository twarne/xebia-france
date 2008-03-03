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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

public abstract class BasePage extends WebPage {

    private static final long serialVersionUID = 1L;

    public static final String PARAM_MESSAGE_KEY = "message";
    public static final String PARAM_ERRORMESSAGE_KEY = "errorMessage";
    public static final String PARAM_EXCEPTION_KEY = "exception";

    private static final Logger logger = Logger.getLogger(BasePage.class);

    private FeedbackPanel feedbackPanel;

    public BasePage(PageParameters pageParameters) {
        super(pageParameters);
        add(HeaderContributor.forCss("common/styles.css"));
        add(new BookmarkablePageLink("titleLink", Application.get().getHomePage()));

        ListView menuItemsListView = new ListView("menuItems", getMenuItems()) {

            private static final long serialVersionUID = 1L;

            @Override
            @SuppressWarnings("unchecked")
            public void populateItem(final ListItem listItem) {
                final MenuItem menuItem = (MenuItem) listItem.getModelObject();
                Link menuItemLink = new BookmarkablePageLink("menuItemLink", menuItem.getPageClass());
                menuItemLink.add(new Label("menuItemLabel", menuItem.getLabelModel()));
                listItem.add(menuItemLink);
            }
        };
        add(menuItemsListView);

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
            return ;
        }
        if (pageParameters.containsKey(PARAM_EXCEPTION_KEY)) {
            Throwable t = (Throwable) pageParameters.get(PARAM_EXCEPTION_KEY);
            addErrorMessage(t);
        }
        if (pageParameters.containsKey(PARAM_MESSAGE_KEY)) {
            String message = (String) pageParameters.getString(PARAM_MESSAGE_KEY);
            addInfoMessage(message);
        }
        if (pageParameters.containsKey(PARAM_ERRORMESSAGE_KEY)) {
            String errorMessage = (String) pageParameters.getString(PARAM_ERRORMESSAGE_KEY);
            addErrorMessage(errorMessage);
        }
    }

    protected void addInfoMessage(String message) {
        feedbackPanel.info(message);
    }

    protected void addErrorMessage(String message) {
        feedbackPanel.error(message);
    }

    protected void addErrorMessage(Throwable t) {
        if (t == null) {
            return ;
        }
        logger.error("Une erreur est survenu", t);
        String errorMessage = StringUtils.isEmpty(t.getMessage()) ? t.toString() : t.getMessage();
        feedbackPanel.error(errorMessage);
    }

    protected BlogWebSession getWebSession() {
        return (BlogWebSession) getSession();
    }

    @SuppressWarnings("unchecked")
    public static class MenuItem implements Serializable {

        private static final long serialVersionUID = 1L;

        private Class pageClass;

        private IModel labelModel;

        public MenuItem(Class pageClass, IModel labelModel) {
            super();
            this.pageClass = pageClass;
            this.labelModel = labelModel;
        }

        public Class getPageClass() {
            return pageClass;
        }

        public void setPageClass(Class pageClass) {
            this.pageClass = pageClass;
        }

        public IModel getLabelModel() {
            return labelModel;
        }

        @Override
        public String toString() {
            ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("pageClass", getPageClass()).append("label", getLabelModel().getObject());
            return super.toString();
        }
    }
}
