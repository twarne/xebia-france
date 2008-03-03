package fr.xebia.demo.wicket.blog.service;

import fr.xebia.demo.wicket.blog.data.User;

public class LoginService {
	
	private static final User[] users = { new User("admin", "admin") };

	public boolean authenticate(User givenUser) {
		for (User user : users) {
			if (user.equals(givenUser)) {
				return true;
			}
		}
		return false;
	}
}
