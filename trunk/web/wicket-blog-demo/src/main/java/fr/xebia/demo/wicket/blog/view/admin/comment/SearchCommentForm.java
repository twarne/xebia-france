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
package fr.xebia.demo.wicket.blog.view.admin.comment;

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
import org.apache.wicket.validation.validator.NumberValidator;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.view.util.CustomDateField;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class SearchCommentForm extends Form {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(SearchCommentForm.class);

    @SpringBean(name = "commentService")
    protected transient Service<Comment> commentService;

    protected Comment comment;

    private static final List<String> COMMENT_STATUS_CHOICE = Arrays.asList(new String[]{"true", "false"});

    public SearchCommentForm(String id) {
        super(id);
        this.comment = new Comment();
        createComponents();
    }

    private void createComponents() {
        add(new TextField("author", new PropertyModel(comment, "author")));
        add(new TextField("email", new PropertyModel(comment, "email")));
        add(new TextField("content", new PropertyModel(comment, "content")));
        
        add(new CustomDateField("date", new PropertyModel(comment, "date")));
        
        add(new DropDownChoice("approved", new PropertyModel(comment, "approved"), COMMENT_STATUS_CHOICE));

        TextField postIdField = new TextField("postId", new PropertyModel(comment, "postId"));
        postIdField.add(NumberValidator.POSITIVE);
        add(postIdField);

        add(new Button("submitButton", new StringResourceModel("comment.list.searchLink", this, null)));
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    public void onSubmit() {
        searchComments(comment);
    }

    protected void searchComments(Comment comment) {
        try {
            List<Comment> comments = commentService.search(comment);
            logger.debug("Found " + comments.size() + " comments");
            PageParameters pageParameters = new PageParameters();
            pageParameters.put(ViewCommentPage.PARAM_COMMENTS_KEY, comments);
            setResponsePage(CommentListPage.class, pageParameters);
        } catch (Exception e) {
            logger.error("Error while searching comments", e);
        	throw new RestartResponseException(CommentListPage.class, PageParametersUtils.fromException(e));
        }
    }
}