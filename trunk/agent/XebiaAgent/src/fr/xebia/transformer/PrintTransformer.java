package fr.xebia.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class PrintTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String fullyQualifiedClassName, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classofileBuffer) throws IllegalClassFormatException {

		String className = fullyQualifiedClassName.replaceAll(".*/", "");
		String pacakge = fullyQualifiedClassName.replaceAll("/[~a-zA-Z$0-9_]*$", "");
		System.err.printf("PRINTTRANSFORMER : Class: %s in: %s\n", className, pacakge);

		return null;
	}

}
