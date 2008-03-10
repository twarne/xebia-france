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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.PostService;

public class HomePage extends PublicPage {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(HomePage.class);

    @SpringBean(name="postService")
    private transient PostService postService;

    @SpringBean(name="commentService")
    private transient CommentService commentService;

    public HomePage(PageParameters pageParameters) {
        super(pageParameters);
        createComponents();
    }

    private void createComponents() {
        add(new Label("welcomeMessage", new StringResourceModel("index.welcomeMessage", this, null)));
        
        add(new ListView("posts", getLastPosts()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(final ListItem postListItem) {
                final Post post = (Post) postListItem.getModelObject();
                postListItem.add(new Label("modified", post.getModified().toString()));
                postListItem.add(new Label("author", post.getAuthor()));
                postListItem.add(new Label("title", post.getTitle()));
                postListItem.add(new Label("content", post.getContent()));
                postListItem.add(new Link("addCommentLink"){
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.put(AddCommentPage.PARAM_POSTID_KEY, post.getId());
                        setResponsePage(AddCommentPage.class, pageParameters);
                    }
                }.setVisible(post.getCommentsAllowed()));
                ListView commentsListView = new ListView("comments", getCommentsForPostId(post.getId())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void populateItem(final ListItem commentListItem) {
                        final Comment comment = (Comment) commentListItem.getModelObject();
                        commentListItem.add(new Label("author", comment.getAuthor()));
                        commentListItem.add(new Label("date", comment.getDate().toString()));
                        commentListItem.add(new Label("content", comment.getContent()));
                    }
                };
                postListItem.add(commentsListView);
            }
        });
    }

    private List<Post> getLastPosts() {
        List<Post> posts = null;
        try {
            posts = postService.getLastPosts();
            logger.debug("Found " + posts.size() + " posts");
        } catch (Exception e) {
            logger.error("Can't get posts", e);
            addErrorMessage(e);
            posts = new LinkedList<Post>();
        }
        return posts;
    }

    private List<Comment> getCommentsForPostId(Long postId) {
        List<Comment> comments = null;
        try {
            comments = commentService.getCommentsForPostId(postId);
            logger.debug("Found "+comments.size()+" comments for postId: "+postId);
        } catch (Exception e) {
            comments = new LinkedList<Comment>();
            addErrorMessage(e);
            logger.error("Can't get comments for postId: "+postId, e);
        }
        return comments;
    }
}
