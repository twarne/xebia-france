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

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class AddCommentForm extends Form {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AddCommentForm.class);

    @SpringBean(name = "commentService")
    protected transient Service<Comment> commentService;

    protected Comment comment;

    public AddCommentForm(String id, Long postId) {
        this(id, new Comment(postId));
    }

    public AddCommentForm(String id, Comment comment) {
        super(id);
        this.comment = comment;
        createComponents();
    }

    private void createComponents() {
        TextField authorField = new TextField("author", new PropertyModel(comment, "author"));
        authorField.setRequired(true);
        add(authorField);

        TextField emailField = new TextField("email", new PropertyModel(comment, "email"));
        emailField.setRequired(true);
        emailField.add(EmailAddressValidator.getInstance());
        add(emailField);

        TextArea textArea = new TextArea("content", new PropertyModel(comment, "content"));
        textArea.setRequired(true);
        add(textArea);

        add(new Label("postId", new PropertyModel(comment, "postId")));
        add(new Button("submitButton", getButtonModel()));
    }

    protected IModel getButtonModel() {
        return new StringResourceModel("comment.add.submitLink", this, null);
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    public void onSubmit() {
        saveComment(comment);
    }

    private void saveComment(Comment comment) {
        try {
            comment.setDate(new Date());
            comment.setApproved(false);
            logger.debug("Adding comment: " + comment);
            commentService.save(comment);
            setResponsePage(Application.get().getHomePage(), PageParametersUtils.fromStringMessage("Added new comment: " + comment));
        } catch (Exception e) {
            logger.error("Error while saving comment", e);
            PageParameters pageParameters = PageParametersUtils.fromException(e);
            pageParameters.put(AddCommentPage.PARAM_POSTID_KEY, comment.getPostId());
            throw new RestartResponseException(AddCommentPage.class, pageParameters);
        }
    }
}