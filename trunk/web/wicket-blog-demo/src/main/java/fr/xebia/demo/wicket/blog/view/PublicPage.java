package fr.xebia.demo.wicket.blog.view;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.StringResourceModel;

import fr.xebia.demo.wicket.blog.view.admin.AdminHomePage;

public abstract class PublicPage extends BasePage {

    public PublicPage(PageParameters pageParameters) {
        super(pageParameters);
    }

    private List<MenuItem> menuItemPages;

    @Override
    protected List<MenuItem> getMenuItems() {
        if (menuItemPages == null) {
            menuItemPages = new LinkedList<MenuItem>();
            menuItemPages.add(new MenuItem(Application.get().getHomePage(), new StringResourceModel("menu.home", this, null)));
            menuItemPages.add(new MenuItem(AdminHomePage.class, new StringResourceModel("menu.adminHome", this, null)));
        }
        return menuItemPages;
    }
}
