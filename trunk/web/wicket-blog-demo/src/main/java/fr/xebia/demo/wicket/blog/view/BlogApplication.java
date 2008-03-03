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

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.apache.wicket.settings.ISecuritySettings;
import org.apache.wicket.spring.SpringWebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import fr.xebia.demo.wicket.blog.view.admin.AdminHomePage;
import fr.xebia.demo.wicket.blog.view.admin.category.CategoryListPage;
import fr.xebia.demo.wicket.blog.view.admin.comment.CommentListPage;
import fr.xebia.demo.wicket.blog.view.admin.post.PostListPage;
import fr.xebia.demo.wicket.blog.view.security.AnnotationAuthorizationStrategy;
import fr.xebia.demo.wicket.blog.view.security.UnauthorizedComponentInstantiationListener;

public class BlogApplication extends SpringWebApplication {

	public BlogApplication() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		getRequestCycleSettings().setRenderStrategy(IRequestCycleSettings.ONE_PASS_RENDER);
		// For production and W3C HTML compliance you can switch this option to true
		getMarkupSettings().setStripWicketTags(true);
		// Remove (or not) the wicket xml namespace declaration
		getMarkupSettings().setStripXmlDeclarationFromOutput(true);
		// Enable Spring injection on page
		addComponentInstantiationListener(new SpringComponentInjector(this));

        mountBookmarkablePage("/home", getHomePage());
        mountBookmarkablePage("/login", LoginPage.class);
        mountBookmarkablePage("/admin", AdminHomePage.class);
		mountBookmarkablePage("/admin/categories", CategoryListPage.class);
        mountBookmarkablePage("/admin/comments", CommentListPage.class);
        mountBookmarkablePage("/admin/posts", PostListPage.class);
        mountBookmarkablePage("/logout", LogoutPage.class);
		        
		ISecuritySettings securitySettings = getSecuritySettings();
		securitySettings.setAuthorizationStrategy(new AnnotationAuthorizationStrategy(LoginPage.class));
		securitySettings.setUnauthorizedComponentInstantiationListener(
						new UnauthorizedComponentInstantiationListener(getHomePage()));
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new BlogWebSession(request);
	}

	/**
	 * @see wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}
}
