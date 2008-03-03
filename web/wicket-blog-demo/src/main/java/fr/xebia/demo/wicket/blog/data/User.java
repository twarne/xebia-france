package fr.xebia.demo.wicket.blog.data;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class User implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private String login;
	private String password;
	
	public User() {
		super();
	}

	public User(String user, String password) {
		super();
		this.login = user;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String user) {
		this.login = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ((obj instanceof User) == false) {
			return false;
		}
		User anotherUser = (User) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(login, anotherUser.getLogin());
		equalsBuilder.append(password, anotherUser.getPassword());
		return equalsBuilder.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.login);
		return hashCodeBuilder.hashCode();
	}
}
