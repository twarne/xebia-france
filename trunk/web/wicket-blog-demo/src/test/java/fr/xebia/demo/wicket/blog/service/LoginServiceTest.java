package fr.xebia.demo.wicket.blog.service;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.User;


public class LoginServiceTest {

    @Test
    public void testAuthenticate() {
        LoginService loginService = new LoginService();
        boolean authenticated = loginService.authenticate(new User("admin", "admin"));
        assertSame("Authentication should succedd", authenticated, true);
    }

    @Test
    public void testAuthenticateWrongPassword() {
        LoginService loginService = new LoginService();
        boolean authenticated = loginService.authenticate(new User("admin", "wrong"));
        assertSame("Authentication should fail", authenticated, false);
    }

    @Test
    public void testAuthenticateWrongUser() {
        LoginService loginService = new LoginService();
        boolean authenticated = loginService.authenticate(new User("wrong", "admin"));
        assertSame("Authentication should fail", authenticated, false);
    }
}
