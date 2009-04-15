package fr.xebia.blog.guice.service.impl;

import java.io.IOException;

import com.google.inject.Inject;

import fr.xebia.blog.guice.dao.MyBasicDao;
import fr.xebia.blog.guice.service.MyService;

public class MyServiceImpl implements MyService {

	@Inject
	private Appendable recorder;

	private final MyBasicDao basicDao;

	@Inject
	public MyServiceImpl(MyBasicDao basicDao) {
		super();
		this.basicDao = basicDao;
	}

	@Override
	public void displaySample() {
		try {
			recorder.append("Calling DAO\n");
			String daoResult = basicDao.select();
			recorder.append("Result from DAO : " + daoResult + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
