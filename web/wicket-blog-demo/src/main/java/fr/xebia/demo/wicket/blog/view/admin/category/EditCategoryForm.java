package fr.xebia.demo.wicket.blog.view.admin.category;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;


public class EditCategoryForm extends AddCategoryForm {

    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(EditCategoryForm.class);

    public EditCategoryForm(String id, Category category) {
        super(id, category);
        createComponents();
    }
    
    private void createComponents() {
        add(new Label("idValue", new Model(category.getId())));
    }

    @Override
    protected IModel getButtonModel() {
        return new StringResourceModel("category.edit.submitLink", this, null);
    }
    
    @Override
    public void onSubmit() {
        updateCategory(category);
    }

    protected void updateCategory(Category category) {
        try {
            logger.debug("Updating category: " + category);
            Category updatedCategory = categoryService.update(category);
            setResponsePage(CategoryListPage.class, PageParametersUtils.fromStringMessage("Updated category: " + updatedCategory));
        } catch (Exception e) {
            logger.error("Error while updating category", e);
        	throw new RestartResponseException(EditCategoryPage.class, PageParametersUtils.fromException(e));
        }
    }
}
