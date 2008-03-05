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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.Service;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class PostListPage extends PostPage {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(PostListPage.class);

    @SpringBean(name = "postService")
    private Service<Post> postService;

    @SuppressWarnings("unchecked")
    public PostListPage(PageParameters pageParameters) {
        super(pageParameters);
        List<Post> posts = null;
        if (pageParameters.containsKey(PARAM_POSTS_KEY)) {
            posts = (List<Post>) pageParameters.get(PARAM_POSTS_KEY);
        }
        createComponents(posts);
    }

    private void createComponents(List<Post> posts) {
        PageLink pageLink = new PageLink("addLink", AddPostPage.class);
        add(pageLink);
        add(new SearchPostForm("postForm"));
        if (posts == null) {
            posts = getPosts();
        }
        add(new ListView("posts", posts) {

            private static final long serialVersionUID = 1L;

            @Override
            public void populateItem(final ListItem listItem) {
                final Post post = (Post) listItem.getModelObject();
                Link viewLink = new Link("viewLink") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        try {
                            Post viewedPost = getPost(post);
                            if (viewedPost == null) {
                                throw new RestartResponseException(PostListPage.class, PageParametersUtils
                                        .fromStringErrorMessage(getString("post.list.notFound",
                                                new Model(post.getId()))));
                            }
                            PageParameters pageParameters = new PageParameters();
                            pageParameters.put(ViewPostPage.PARAM_POST_KEY, viewedPost);
                            setResponsePage(ViewPostPage.class, pageParameters);
                        } catch (Exception e) {
                            logger.error("Error while getting post", e);
                            throw new RestartResponseException(PostListPage.class,
                                 PageParametersUtils.fromException(e));
                        }
                    }
                };
                viewLink.add(new Label("id", String.valueOf(post.getId())));
                listItem.add(viewLink);
                listItem.add(new Link("deleteLink") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        try {
                            deletePost(post);
                            setResponsePage(PostListPage.class,
                                 PageParametersUtils.fromStringMessage(getString(
                                    "post.list.deleted", new Model(post.getId()))));
                        } catch (Exception e) {
                            logger.error("Error while deleting post", e);
                            throw new RestartResponseException(PostListPage.class,
                                 PageParametersUtils.fromException(e));
                        }
                    }
                });
                listItem.add(new Label("date", new Model(post.getDate())));
                listItem.add(new Label("modified", new Model(post.getModified())));
                listItem.add(new Label("author", post.getAuthor()));
                listItem.add(new Label("status", post.getStatus()));
                listItem.add(new Label("title", post.getTitle()));
                String categoryName = null;
                if (post.getCategory() == null) {
                    categoryName = "";
                } else {
                    categoryName = post.getCategory().getNicename();
                }
                listItem.add(new Label("category", categoryName));
            }
        });
        add(new Label("resultCount", new StringResourceModel("post.list.resultCount", this, null, new Object[]{posts.size()})));
    }

    private List<Post> getPosts() {
        List<Post> posts = null;
        try {
            posts = postService.list();
            logger.debug("Found " + posts.size() + " posts");
        } catch (Exception e) {
            logger.error("Can't get post list", e);
            addErrorMessage(e);
            posts = new LinkedList<Post>();
        }
        return posts;
    }

    private Post getPost(Post post) throws ServiceException {
        Serializable id = post.getId();
        logger.debug("Getting post with id: " + id);
        return postService.get(id);
    }

    private void deletePost(Post post) throws ServiceException {
        Serializable id = post.getId();
        logger.debug("Deleting post with id: " + id);
        postService.deleteById(id);
    }
}
