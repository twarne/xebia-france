package fr.xebia.findbugs;



public class BadDeprecatedClass {


	public void useDeprecated() {
		new Deprecated1();
		Deprecated1.deprecatedMethod1();
		Deprecated2.deprecatedMethod2();
		new Deprecated3().deprecatedMethod3();
		Deprecated4.test = "";
		new Deprecated5().test = "";
	}

	public void useNonDeprecated() {
		new Deprecated2();
		Deprecated2.nonDeprecatedMethod2();
		Deprecated4.test2 = "";
		new Deprecated5().test2 = "";
	}

}

@Deprecated
class Deprecated1 {
	public static void deprecatedMethod1() {
	}
}
class Deprecated2 {
	@Deprecated
	public static void deprecatedMethod2() {
		// dummy
	}
	public static void nonDeprecatedMethod2() {
		// dummy
	}

}
class Deprecated3 {
	@Deprecated
	public void deprecatedMethod3() {
		// dummy
	}

}
class Deprecated4 {

	@Deprecated
	public static String test = "";

	public static String test2 = "";

}

class Deprecated5 {

	@Deprecated
	public String test = "";
	public String test2 = "";

}

