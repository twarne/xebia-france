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
package fr.xebia.demo.wicket.blog.view.admin;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.StringResourceModel;

import fr.xebia.demo.wicket.blog.view.BasePage;
import fr.xebia.demo.wicket.blog.view.BlogApplication;
import fr.xebia.demo.wicket.blog.view.LogoutPage;
import fr.xebia.demo.wicket.blog.view.MenuItem;
import fr.xebia.demo.wicket.blog.view.admin.category.ListCategoryPage;
import fr.xebia.demo.wicket.blog.view.admin.comment.ListCommentPage;
import fr.xebia.demo.wicket.blog.view.admin.post.ListPostPage;

public abstract class BaseAdminPage extends BasePage {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    private List<MenuItem> menuItemPages;

    public BaseAdminPage(PageParameters pageParameters) {
        super(pageParameters);
    }

    @Override
    public boolean isSecured() {
    	return true;
    }

    @Override
    protected List<MenuItem> getMenuItems() {
        if (menuItemPages == null) {
            menuItemPages = new LinkedList<MenuItem>();
            menuItemPages.add(new MenuItem(((BlogApplication) getApplication()).getHomePage(), new StringResourceModel("menu.home", this, null)));
            menuItemPages.add(new MenuItem(AdminHomePage.class, new StringResourceModel("menu.adminHome", this, null)));
            menuItemPages.add(new MenuItem(ListCategoryPage.class, new StringResourceModel("menu.category", this, null)));
            menuItemPages.add(new MenuItem(ListCommentPage.class, new StringResourceModel("menu.comment", this, null)));
            menuItemPages.add(new MenuItem(ListPostPage.class, new StringResourceModel("menu.post", this, null)));
            menuItemPages.add(new MenuItem(LogoutPage.class, new StringResourceModel("menu.logout", this, null)));
        }
        return menuItemPages;
    }
}
