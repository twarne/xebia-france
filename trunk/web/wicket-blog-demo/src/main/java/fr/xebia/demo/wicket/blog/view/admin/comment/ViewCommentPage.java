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

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.PageLink;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.view.util.ParametizedPageLink;

public class ViewCommentPage extends CommentPage {

    private static final long serialVersionUID = 1L;

    public ViewCommentPage(PageParameters pageParameters) {
        super(pageParameters);
        if (pageParameters.containsKey(PARAM_COMMENT_KEY)) {
            Comment comment = (Comment) pageParameters.get(PARAM_COMMENT_KEY);
            createComponents(comment);
        } else {
            throw new IllegalArgumentException("The parameter 'comment' is mandatory");
        }
    }

    private void createComponents(Comment comment) {
        add(new Label("id", String.valueOf(comment.getId())));
        add(new Label("approved", String.valueOf(comment.getApproved())));
        add(new Label("author", String.valueOf(comment.getAuthor())));
        add(new Label("email", String.valueOf(comment.getEmail())));
        add(new Label("content", String.valueOf(comment.getContent())));
        add(new Label("date", String.valueOf(comment.getDate())));
        add(new Label("postId", String.valueOf(comment.getPostId())));
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(EditCommentPage.PARAM_COMMENT_KEY, comment);
        add(new ParametizedPageLink("editLink", EditCommentPage.class, pageParameters));
        add(new PageLink("backToListLink", ListCommentPage.class));
    }
}
