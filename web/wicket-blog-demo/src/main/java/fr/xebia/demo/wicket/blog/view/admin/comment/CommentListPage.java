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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class CommentListPage extends CommentPage {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CommentListPage.class);

    @SpringBean(name = "commentService")
    private transient Service<Comment> commentService;

    @SuppressWarnings("unchecked")
    public CommentListPage(PageParameters pageParameters) {
        super(pageParameters);
        List<Comment> comments = null;
        if (pageParameters.containsKey(PARAM_COMMENTS_KEY)) {
            comments = (List<Comment>) pageParameters.get(PARAM_COMMENTS_KEY);
        }
        createComponents(comments);
    }

    private void createComponents(List<Comment> comments) {
        add(new SearchCommentForm("commentForm"));
        if (comments == null) {
            comments = getComments();
        }
        add(new ListView("comments", comments) {

            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(final ListItem listItem) {
                final Comment comment = (Comment) listItem.getModelObject();
                Link viewLink = new Link("viewLink") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        try {
                            Comment viewedComment = getComment(comment);
                            if (viewedComment == null) {
                                throw new RestartResponseException(CommentListPage.class,
                                     PageParametersUtils.fromStringErrorMessage(
                                          getString("comment.list.notFound", new Model(comment.getId()))));
                            }
                            PageParameters pageParameters = new PageParameters();
                            pageParameters.put(ViewCommentPage.PARAM_COMMENT_KEY, viewedComment);
                            setResponsePage(ViewCommentPage.class, pageParameters);
                        } catch (Exception e) {
                            logger.error("Error while getting comment", e);
                            throw new RestartResponseException(CommentListPage.class, PageParametersUtils.fromException(e));
                        }
                    }
                };
                viewLink.add(new Label("id", new Model(comment.getId())));
                listItem.add(viewLink);
                listItem.add(new Link("deleteLink") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        try {
                            deleteComment(comment);
                            setResponsePage(CommentListPage.class,
                                PageParametersUtils.fromStringMessage(getString("comment.list.deleted",
                                    new Model(comment.getId()))));
                        } catch (Exception e) {
                            logger.error("Error while deleting comment", e);
                            throw new RestartResponseException(CommentListPage.class, PageParametersUtils.fromException(e));
                        }
                    }
                });
                listItem.add(new Label("approved", new Model(comment.getApproved())));
                listItem.add(new Label("author", comment.getAuthor()));
                listItem.add(new Label("email", comment.getEmail()));
                listItem.add(new Label("date", new Model(comment.getDate())));
                listItem.add(new Label("postId", new Model(comment.getPostId())));
            }
        });
        add(new Label("resultCount", new StringResourceModel("comment.list.resultCount", this, null, new Object[]{comments.size()})));
    }

    private List<Comment> getComments() {
        try {
            List<Comment> comments = commentService.list();
            logger.debug("Found " + comments.size() + " comments");
            return comments;
        } catch (Exception e) {
            logger.error("Error while getting comment list", e);
            addErrorMessage(e);
            return new LinkedList<Comment>();
        }
    }

    private Comment getComment(Comment comment) throws ServiceException {
        Serializable id = comment.getId();
        logger.debug("Getting comment with id: " + id);
        return commentService.get(id);
    }

    private void deleteComment(Comment comment) throws ServiceException {
        Serializable id = comment.getId();
        logger.debug("Deleting comment with id: " + id);
        commentService.deleteById(id);
    }
}
