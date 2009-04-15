package fr.xebia.blog.guice.main;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import fr.xebia.blog.guice.module.MyModule;
import fr.xebia.blog.guice.service.MyService;

public class MyMain {
	public static void main(String[] args) throws IOException {
		Injector injector = Guice.createInjector(new MyModule());

		MyService myService = injector.getInstance(MyService.class);
		myService.displaySample();
	}
}
