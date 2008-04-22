package fr.xebia.findbugs;



public class BadClass {

	public void existing1() {
		Integer i = 0;
		System.err.println("".equals(i));
	}

	//////////////////////////

	public void useDoNotUseMethod() {
		BadClass.doNotUse();
	}
	public static void doNotUse() {
		// dummy
	}
}

