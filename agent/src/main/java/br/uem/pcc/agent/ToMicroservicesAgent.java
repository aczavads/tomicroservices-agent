package br.uem.pcc.agent;

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class ToMicroservicesAgent {
	public static void premain(String arg, Instrumentation inst) {
		new AgentBuilder.Default()
		        .type(ElementMatchers.nameStartsWith("com.accountfy"))
				.transform((builder, type, classLoader, module) -> {
						return builder
							.method(ElementMatchers.any())
							.intercept(MethodDelegation.to(new StackInterceptor()));						
						})
				.installOn(inst);
	}
}

