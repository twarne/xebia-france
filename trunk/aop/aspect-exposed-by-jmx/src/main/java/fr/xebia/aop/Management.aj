package fr.xebia.aop;

import java.util.Hashtable;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.util.ClassUtils;

/**
 * This Aspect exposes all the classes implementing ManagedBean as JMX MBean
 * @author Benoit Moussaud
 *
 */
public aspect Management {

	public interface ManagedBean {
		public boolean isEnabled();

		public void setEnabled(boolean enabled);
	}

	private pointcut managedBeanConstruction(ManagedBean bean) : 
        execution(ManagedBean+.new(..)) && this(bean);

	after(ManagedBean bean) returning: managedBeanConstruction(bean) {
		ObjectName registered = getMBeanExporter()
				.registerManagedResource(bean);
		System.out.println("Bean " + bean
				+ " is exposed with this object name " + registered);
	}

	declare parents: PerfMonitor implements ManagedBean;

	private MBeanExporter getMBeanExporter() {
		//Spring Framework Stuff...
		MBeanServerFactoryBean server = new MBeanServerFactoryBean();
		server.setLocateExistingServerIfPossible(true);
		server.afterPropertiesSet();
		
		InterfaceBasedMBeanInfoAssembler assembler = new InterfaceBasedMBeanInfoAssembler();
		assembler.setManagedInterfaces(new Class[] { ManagedBean.class });
		
		MBeanExporter exporter = new MBeanExporter();
		exporter.setNamingStrategy(new ObjectNamingStrategy() {

			@SuppressWarnings("unchecked")
			public ObjectName getObjectName(Object managedBean, String beanKey)
					throws MalformedObjectNameException {
				Hashtable keys = new Hashtable();
				String shortName = ClassUtils.getShortName(managedBean
						.getClass());
				keys.put("Name", shortName);
				keys.put("Type", "Monitor");
				return ObjectNameManager.getInstance("xebia", keys);

			}
		});
		exporter.setServer((MBeanServer) server.getObject());
		exporter.setAutodetectMode(MBeanExporter.AUTODETECT_NONE);
		exporter.setEnsureUniqueRuntimeObjectNames(false);
		exporter.setAssembler(assembler);
		exporter.afterPropertiesSet();

		return exporter;
	}

}
