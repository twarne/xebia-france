package fr.xebia.aop;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * This Aspect monitor performances with Jamon
 * @author Benoit Moussaud
 *
 */
public aspect PerfMonitor {
	
	pointcut monitor() : execution(* fr.xebia.appz.ClassToMonitor.methodToMonitor(..)) ;

	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	pointcut monitorEnabled() : if(aspectOf().isEnabled());

	Object around() : monitor() && monitorEnabled() {
		Monitor monitor = MonitorFactory.start(thisJoinPoint.toShortString());
		try {
			System.out.println("->"+thisJoinPoint+"-"+new java.util.Date()+"....");
			return proceed();
		} finally {
			if (monitor != null)
				monitor.stop();
		}
	}
}
