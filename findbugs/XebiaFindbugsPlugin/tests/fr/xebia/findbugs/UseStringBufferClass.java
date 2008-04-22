package fr.xebia.findbugs;



public class UseStringBufferClass {

	private final static StringBuilder s3 = new StringBuilder();
	private final static StringBuffer s4 = new StringBuffer();

	public void useDeprecated() {
		StringBuilder s1 = new StringBuilder();
		StringBuffer s2 = new StringBuffer();
	}
}