package fr.xebia.coherence.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;

public class SimonMethodInterceptor implements MethodInterceptor {

	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		String name = invocation.getThis().getClass().getName() + "."
				+ invocation.getMethod().getName()+"."+invocation.getArguments()[0];
	
		Stopwatch stopwatch = SimonManager.getStopwatch(name);

		Split split = stopwatch.start();

		try {
			return invocation.proceed();
		} finally {
			split.stop();
			System.out.println(stopwatch);
		}
	}

}
