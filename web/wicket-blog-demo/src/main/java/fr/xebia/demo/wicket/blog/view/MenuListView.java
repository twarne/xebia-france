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

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;


public class MenuListView extends ListView {
    private static final long serialVersionUID = 1L;

    public MenuListView(String id) {
        this(id, new LinkedList<MenuItem>());
    }

    public MenuListView(String id, List<MenuItem> menuItems) {
        super(id, menuItems);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void populateItem(final ListItem listItem) {
        final MenuItem menuItem = (MenuItem) listItem.getModelObject();
        Link menuItemLink = new BookmarkablePageLink("menuItemLink", menuItem.getPageClass());
        menuItemLink.add(new Label("menuItemLabel", menuItem.getLabelModel()));
        listItem.add(menuItemLink);
    }
}