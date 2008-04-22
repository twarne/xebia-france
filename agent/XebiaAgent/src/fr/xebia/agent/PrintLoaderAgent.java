package fr.xebia.agent;

import java.lang.instrument.Instrumentation;

import fr.xebia.transformer.PrintTransformer;

public class PrintLoaderAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.err.println("*** Premain method is called : loading " + PrintTransformer.class.getSimpleName());
		inst.addTransformer(new PrintTransformer(), true);
	}

	public static void premain(String agentArgs) {
		System.err.println("Will not be called because of the premain(String, Intrusrumentation) method.");
	}

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.err.println("* Agent main : used if the agent is added at runtime");
		inst.addTransformer(new PrintTransformer(), true);
    }

}
