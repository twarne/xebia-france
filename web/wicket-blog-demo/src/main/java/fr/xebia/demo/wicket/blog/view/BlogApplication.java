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

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.apache.wicket.settings.ISecuritySettings;
import org.apache.wicket.spring.SpringWebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import fr.xebia.demo.wicket.blog.view.admin.AdminHomePage;
import fr.xebia.demo.wicket.blog.view.admin.category.CategoryListPage;
import fr.xebia.demo.wicket.blog.view.admin.comment.CommentListPage;
import fr.xebia.demo.wicket.blog.view.admin.post.PostListPage;
import fr.xebia.demo.wicket.blog.view.security.AnnotationAuthorizationStrategy;
import fr.xebia.demo.wicket.blog.view.security.UnauthorizedComponentInstantiationListener;

// TODO Corriger les label des boutons submit
public class BlogApplication extends SpringWebApplication {

    public BlogApplication() {
        super();
    }

    public BlogApplication(ApplicationContext applicationContext) {
        this();
        setApplicationContext(applicationContext);
    }

    @Override
    protected void init() {
        super.init();
        getRequestCycleSettings().setRenderStrategy(IRequestCycleSettings.ONE_PASS_RENDER);
        // Remove (or not) the wicket xml namespace declaration
        getMarkupSettings().setStripXmlDeclarationFromOutput(true);

        initSecuritySettings();

        mountBookmarkablePage("/home", getHomePage());
        mountBookmarkablePage("/login", LoginPage.class);
        mountBookmarkablePage("/admin", AdminHomePage.class);
        mountBookmarkablePage("/admin/categories", CategoryListPage.class);
        mountBookmarkablePage("/admin/comments", CommentListPage.class);
        mountBookmarkablePage("/admin/posts", PostListPage.class);
        mountBookmarkablePage("/logout", LogoutPage.class);

        initSpringInjection();
    }

    protected void initSpringInjection() {
        // Enable Spring injection on page
        SpringComponentInjector springComponentInjector = new SpringComponentInjector(this);
        addComponentInstantiationListener(springComponentInjector);
    }

    protected void initSecuritySettings() {
        ISecuritySettings securitySettings = getSecuritySettings();
        securitySettings.setAuthorizationStrategy(new AnnotationAuthorizationStrategy(LoginPage.class));
        securitySettings.setUnauthorizedComponentInstantiationListener(new UnauthorizedComponentInstantiationListener(getHomePage()));
    }

    @Override
    public String getConfigurationType() {
        // Uncomment this for production
        return Application.DEPLOYMENT;
        // return Application.DEVELOPMENT;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new BlogWebSession(request);
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }
}
