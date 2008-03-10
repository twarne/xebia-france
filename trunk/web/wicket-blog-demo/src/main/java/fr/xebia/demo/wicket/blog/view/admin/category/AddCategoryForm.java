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

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class AddCategoryForm extends Form {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AddCategoryForm.class);

    @SpringBean(name = "categoryService")
    protected transient Service<Category> categoryService;

    protected final Category category;

    public AddCategoryForm(String id) {
        this(id, new Category());
    }

    protected AddCategoryForm(String id, Category category) {
        super(id);
        this.category = category;
        createComponents();
    }

    private void createComponents() {
        TextField nameTextField = new TextField("name", new PropertyModel(category, "name"));
        nameTextField.setRequired(true);
        add(nameTextField);
        TextField nicenameTextField = new TextField("nicename", new PropertyModel(category, "nicename"));
        nicenameTextField.setRequired(true);
        add(nicenameTextField);
        add(new TextField("description", new PropertyModel(category, "description")));
        add(new Button("submitButton", getButtonModel()));
    }

    protected IModel getButtonModel() {
        return new StringResourceModel("category.add.submitLink", this, null);
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    public void onSubmit() {
        saveCategory(category);
    }

    private void saveCategory(Category category) {
        try {
            logger.debug("Adding category: " + category);
            categoryService.save(category);
            setResponsePage(CategoryListPage.class, PageParametersUtils.fromStringMessage("Added new category: " + category));
        } catch (Exception e) {
            logger.error("Error while saving category", e);
            throw new RestartResponseException(AddCategoryPage.class, PageParametersUtils.fromException(e));
        }
    }
}
