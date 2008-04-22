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

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class SearchCategoryForm extends Form {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(SearchCategoryForm.class);

    @SpringBean(name = "categoryService")
    private transient Service<Category> categoryService;

    private final Category category;

    public SearchCategoryForm(String id) {
        super(id);
        this.category = new Category();
        createComponents();
    }

    private void createComponents() {
        add(new TextField("description", new PropertyModel(category, "description")));
        add(new TextField("name", new PropertyModel(category, "name")));
        add(new TextField("nicename", new PropertyModel(category, "nicename")));
        add(new Button("submitButton", new StringResourceModel("category.list.searchLink", this, null)));
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    public void onSubmit() {
        searchCategories(category);
    }

    protected void searchCategories(Category category) {
        try {
            List<Category> categories = categoryService.search(category);
            logger.debug("Found " + categories.size() + " categories");
            PageParameters pageParameters = new PageParameters();
            pageParameters.put(ListCategoryPage.PARAM_CATEGORIES_KEY, categories);
            setResponsePage(ListCategoryPage.class, pageParameters);
        } catch (Exception e) {
            logger.error("Error while searching categories", e);
        	throw new RestartResponseException(ListCategoryPage.class, PageParametersUtils.fromException(e));
        }
    }
}