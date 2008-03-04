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
package fr.xebia.demo.wicket.blog.service;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.data.Post;

public class ServiceLocator {

    private Service<Category> categoryService;

    public void setCategoryService(Service<Category> service) {
        categoryService = service;
    }

    public Service<Category> getCategoryService() {
        return categoryService;
    }

    private Service<Comment> commentService;

    public void setCommentService(Service<Comment> service) {
        commentService = service;
    }

    public Service<Comment> getCommentService() {
        return commentService;
    }

    private Service<Post> postService;

    public void setPostService(Service<Post> service) {
        postService = service;
    }

    public Service<Post> getPostService() {
        return postService;
    }

}
