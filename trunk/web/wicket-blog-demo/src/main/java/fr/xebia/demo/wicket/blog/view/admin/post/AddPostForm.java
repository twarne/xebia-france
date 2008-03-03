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
package fr.xebia.demo.wicket.blog.view.admin.post;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class AddPostForm extends Form {

    private static final long serialVersionUID = 1L;

    private static final List<String> POST_STATUS_CHOICE = Arrays.asList(new String[]{Post.STATUS_DRAFT, Post.STATUS_PUBLISHED});

    private static final Logger logger = Logger.getLogger(AddPostForm.class);

    @SpringBean(name = "postService")
    protected Service<Post> postService;
    
    @SpringBean(name = "categoryService")
    protected Service<Category> categoryService;
    
    protected final Post post;

    public AddPostForm(String id) {
        this(id, new Post());
    }

    protected AddPostForm(String id, Post post) {
        super(id);
        this.post = post;
        createComponents();
    }

    private void createComponents() {
        PasswordTextField passwordTextField = new PasswordTextField("password", new PropertyModel(post, "password"));
        passwordTextField.setRequired(false);
        add(passwordTextField);

        add(new CheckBox("pingAllowed", new PropertyModel(post, "pingAllowed")));
        add(new CheckBox("commentsAllowed", new PropertyModel(post, "commentsAllowed")));

        RadioChoice statusChoice = new RadioChoice("status", new PropertyModel(post, "status"), POST_STATUS_CHOICE);
        statusChoice.setRequired(true);
        add(statusChoice);

        TextField titleField = new TextField("title", new PropertyModel(post, "title"));
        titleField.setRequired(true);
        add(titleField);

        TextField authorField = new TextField("author", new PropertyModel(post, "author"));
        authorField.setRequired(true);
        add(authorField);

        add(new DropDownChoice("category", new PropertyModel(post, "category"), getCategories(), new IChoiceRenderer() {
			private static final long serialVersionUID = 1L;

			public String getIdValue(Object object, int index) {
				return ((Category) object).getId().toString();
			}

			public Object getDisplayValue(Object object) {
				return ((Category) object).getNicename();
			}
        }));

        TextArea textArea = new TextArea("content", new PropertyModel(post, "content"));
        textArea.setRequired(true);
        add(textArea);

        add(new Button("submitButton", getButtonModel()));
    }

    private List<Category> getCategories() {
        try {
            List<Category> categories = categoryService.list();
            logger.debug("Found " + categories.size() + " categories");
            return categories;
        } catch (Exception e) {
            logger.error("Error while retreiving categories", e);
            throw new RestartResponseException(PostListPage.class, PageParametersUtils.fromException(e));
        }
    }

    protected IModel getButtonModel() {
        return new StringResourceModel("post.edit.submitLink", this, null);
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    public void onSubmit() {
        savePost(post);
    }

    private void savePost(Post post) {
        try {
            post.setDate(new Date());
            post.setModified(new Date());
            logger.debug("Adding post: " + post);
            postService.save(post);
            setResponsePage(PostListPage.class, PageParametersUtils.fromStringMessage("Added new post: " + post));
        } catch (Exception e) {
            logger.error("Error while saving post", e);
            throw new RestartResponseException(AddPostPage.class, PageParametersUtils.fromException(e));
        }
    }
}