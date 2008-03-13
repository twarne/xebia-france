package fr.xebia.appz;

/**
 * Test Class
 * @author Benoit Moussaud
 *
 */
public class ClassToMonitor {

	public String methodToMonitor() {
		return "Hello";
	}

	public static void main(String[] args) throws Exception {
		ClassToMonitor classToMonitor = new ClassToMonitor();
		while (true) {
			classToMonitor.methodToMonitor();
			Thread.sleep(3000);
		}
	}
}
