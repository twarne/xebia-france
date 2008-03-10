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

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.view.AddCommentForm;
import fr.xebia.demo.wicket.blog.view.util.CustomDateTimeField;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class EditCommentForm extends AddCommentForm {

    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(EditCommentForm.class);

    @SpringBean(name = "commentService")
    private transient Service<Comment> commentService;

    public EditCommentForm(String id, Comment comment) {
        super(id, comment);
        createComponents();
    }

    private void createComponents() {
        add(new Label("idValue", new PropertyModel(comment, "id")));
        CheckBox checkBox = new CheckBox("approved", new PropertyModel(comment, "approved"));
        checkBox.setRequired(true);
        add(checkBox);
        
        add(new CustomDateTimeField("date", new PropertyModel(comment, "date")));

    }

    protected IModel getButtonModel() {
        return new StringResourceModel("comment.edit.submitLink", this, null);
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    public void onSubmit() {
        updateComment(comment);
    }

    protected void updateComment(Comment comment) {
        try {
            logger.debug("Updating comment: " + comment);
            Comment updatedComment = commentService.update(comment);
            setResponsePage(CommentListPage.class, PageParametersUtils.fromStringMessage("Updated comment: " + updatedComment));
        } catch (Exception e) {
            logger.error("Error while updating comment", e);
            PageParameters pageParameters = PageParametersUtils.fromException(e);
            pageParameters.put(EditCommentPage.PARAM_COMMENT_KEY, comment);
            throw new RestartResponseException(EditCommentPage.class, pageParameters);
        }
    }
}