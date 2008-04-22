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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.view.util.CustomDateField;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class SearchPostForm extends Form {

    private static final long serialVersionUID = 1L;

    private static final List<String> POST_STATUS_CHOICE = Arrays.asList(new String[]{"draft", "published"});
    
    private static final Logger logger = Logger.getLogger(SearchPostForm.class);

    @SpringBean(name = "postService")
    private transient Service<Post> postService;
    
    @SpringBean(name = "categoryService")
    protected transient Service<Category> categoryService;

    private final Post post = new Post();

    public SearchPostForm(String id) {
        super(id);
        createComponents();
    }

    private void createComponents() {
        
        add(new CustomDateField("date", new PropertyModel(post, "date")));
        add(new CustomDateField("modified", new PropertyModel(post, "modified")));
        
        add(new DropDownChoice("status", new PropertyModel(post, "status"), POST_STATUS_CHOICE));
        add(new TextField("title", new PropertyModel(post, "title")));
        add(new TextField("author", new PropertyModel(post, "author")));

        add(new DropDownChoice("category", new PropertyModel(post, "category.id"), getCategories()));

        add(new TextField("content", new PropertyModel(post, "content")));
        add(new Button("submitButton", new StringResourceModel("post.list.searchLink", this, null)));
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    public void onSubmit() {
        searchPosts(post);
    }

    private void searchPosts(Post post) {
        try {
            List<Post> posts = postService.search(post);
            logger.debug("Found " + posts.size() + " posts");
            PageParameters pageParameters = new PageParameters();
            pageParameters.put(ListPostPage.PARAM_POSTS_KEY, posts);
            setResponsePage(ListPostPage.class, pageParameters);
        } catch (Exception e) {
            logger.error("Error while searching posts", e);
        	throw new RestartResponseException(ListPostPage.class, PageParametersUtils.fromException(e));
        }
    }

    private List<Category> getCategories() {
        try {
            List<Category> categories = categoryService.list();
            logger.debug("Found " + categories.size() + " categories");
            return categories;
        } catch (Exception e) {
            logger.error("Error while getting categories", e);
            throw new RestartResponseException(ListPostPage.class, PageParametersUtils.fromException(e));
        }
    }
}