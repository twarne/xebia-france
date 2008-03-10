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
package fr.xebia.demo.wicket.blog.view.admin.category;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class CategoryListPage extends CategoryPage {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CategoryListPage.class);

    @SpringBean(name = "categoryService")
    protected transient Service<Category> categoryService;

    @SuppressWarnings("unchecked")
    public CategoryListPage(PageParameters pageParameters) {
        super(pageParameters);
        List<Category> categories = null;
        if (pageParameters.containsKey(PARAM_CATEGORIES_KEY)) {
            categories = (List<Category>) pageParameters.get(PARAM_CATEGORIES_KEY);
        }
        createComponents(categories);
    }

    private void createComponents(List<Category> categories) {
        PageLink pageLink = new PageLink("addLink", AddCategoryPage.class);
        add(pageLink);
        add(new SearchCategoryForm("categoryForm"));
        if (categories == null) {
            categories = getCategories();
        }
        add(new ListView("categories", categories) {

            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(final ListItem listItem) {
                final Category category = (Category) listItem.getModelObject();
                Link editLink = new Link("viewLink") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        try {
                            Category viewedCategory = getCategory(category);
                            if (viewedCategory == null) {
                                throw new RestartResponseException(CategoryListPage.class, PageParametersUtils.fromStringMessage(getString(
                                        "category.list.notFound", new Model(category.getId()))));
                            }
                            PageParameters pageParameters = new PageParameters();
                            pageParameters.put(ViewCategoryPage.PARAM_CATEGORY_KEY, viewedCategory);
                            setResponsePage(ViewCategoryPage.class, pageParameters);
                        } catch (Exception e) {
                            logger.error("Error while getting category", e);
                            throw new RestartResponseException(CategoryListPage.class, PageParametersUtils.fromException(e));
                        }
                    }
                };
                editLink.add(new Label("id", String.valueOf(category.getId())));
                listItem.add(editLink);
                listItem.add(new Link("deleteLink") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        try {
                            deleteCategory(category);
                            setResponsePage(CategoryListPage.class, PageParametersUtils.fromStringMessage(getString(
                                    "category.list.deleted", new Model(category.getId()))));
                        } catch (Exception e) {
                            logger.error("Error while deleting category", e);
                            throw new RestartResponseException(CategoryListPage.class, PageParametersUtils.fromException(e));
                        }
                    }
                });
                listItem.add(new Label("description", new Model(category.getDescription())));
                listItem.add(new Label("name", category.getName()));
                listItem.add(new Label("nicename", category.getNicename()));
            }
        });
        add(new Label("resultCount", new StringResourceModel("category.list.resultCount", this, null, new Object[]{categories.size()})));
    }

    private List<Category> getCategories() {
        List<Category> categories = null;
        try {
            categories = categoryService.list();
            logger.debug("Found " + categories.size() + " categories");
        } catch (Exception e) {
            logger.error("Error while getting category list", e);
            addErrorMessage(e);
            categories = new LinkedList<Category>();
        }
        return categories;
    }

    private Category getCategory(Category category) throws ServiceException {
        Serializable id = category.getId();
        logger.debug("Getting category with id: " + id);
        return categoryService.get(id);
    }

    private void deleteCategory(Category category) throws ServiceException {
        Serializable id = category.getId();
        logger.debug("Deleting category with id: " + id);
        categoryService.deleteById(id);
    }
}
