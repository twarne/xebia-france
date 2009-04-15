package fr.xebia.blog.guice.dao.impl;

import fr.xebia.blog.guice.dao.MyBasicDao;

public class MyBasicDaoImpl implements MyBasicDao {

	public String select() {
		return "Selected";
	}
}
