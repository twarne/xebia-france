package fr.xebia.demo.wicket.blog.service;

import org.springframework.stereotype.Service;

import fr.xebia.demo.wicket.blog.data.User;

@Service
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
