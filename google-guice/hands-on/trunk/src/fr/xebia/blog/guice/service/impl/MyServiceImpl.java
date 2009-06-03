/*
 * Copyright 2002-2008 the original author or authors.
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
package fr.xebia.blog.guice.service.impl;

import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import fr.xebia.blog.guice.dao.MyBasicDao;
import fr.xebia.blog.guice.service.MyService;

/**
 * Simple service simplementation.
 * 
 * @author <a href="mailto:pabs.agro@gmail.com">Pablo Lopez</a>
 */
public class MyServiceImpl implements MyService {

	/**
	 * Appendable.
	 */
	@Inject
	private Appendable recorder;

	/**
	 * Dao.
	 */
	private final MyBasicDao basicDao;
	// Injection nommée
	private final MyBasicDao memoryDao;
	// Provider
	private final MyBasicDao randomDao;

	/**
	 * Parameterized constructor.
	 */
	// Injection nommée
	// Solution 1
	// @Inject
	// public MyServiceImpl(MyBasicDao basicDao, @Memory MyBasicDao memoryDao) {
	// super();
	// this.basicDao = basicDao;
	// this.memoryDao = memoryDao;
	// }
	/**
	 * Parameterized constructor.
	 */
	// Solution 2
	// @Inject
	// public MyServiceImpl(MyBasicDao basicDao,
	// @Named("Memory") MyBasicDao memoryDao) {
	// super();
	// this.basicDao = basicDao;
	// this.memoryDao = memoryDao;
	// }

	/**
	 * Parameterized constructor.
	 */
	@Inject
	public MyServiceImpl(MyBasicDao basicDao,
			@Named("Memory") MyBasicDao memoryDao,
			@Named("Random") MyBasicDao randomDao) {
		super();
		this.basicDao = basicDao;
		this.memoryDao = memoryDao;
		this.randomDao = randomDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displaySample() {
		try {
			recorder.append("Calling DAO\n");
			String daoResult = basicDao.select();
			recorder.append("Result from DAO : " + daoResult + "\n");
			recorder.append("Calling memory DAO\n");
			daoResult = memoryDao.select();
			recorder.append("Result from memory DAO : " + daoResult + "\n");
			recorder.append("Calling random DAO\n");
			daoResult = randomDao.select();
			recorder.append("Result from random DAO : " + daoResult + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
