package fr.xebia.blog.guice.module;

import fr.xebia.blog.guice.dao.MyBasicDao;
import fr.xebia.blog.guice.dao.impl.MyBasicDaoImpl;
import fr.xebia.blog.guice.service.MyService;
import fr.xebia.blog.guice.service.impl.MyServiceImpl;

public class MyModule implements com.google.inject.Module {

	@Override
	public void configure(com.google.inject.Binder binder) {

		// Bind to class
		binder.bind(MyBasicDao.class).to(MyBasicDaoImpl.class);
		binder.bind(MyService.class).to(MyServiceImpl.class);

		binder.bind(Appendable.class).toInstance(System.out);

	}

}
