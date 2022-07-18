package br.uem.pcc.agent;

import java.lang.instrument.Instrumentation;

import org.openjdk.jol.info.ClassData;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.FieldData;
import org.openjdk.jol.layouters.CurrentLayouter;
import org.openjdk.jol.util.ClassUtils;
import org.openjdk.jol.vm.ContendedSupport;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class ToMicroservicesAgent {
	public static void premain(String arg, Instrumentation inst) {
		//System.out.println("Opa!!!!");
		new AgentBuilder.Default()
				.type(ElementMatchers.nameStartsWith("br.uem.agent_test"))
				.transform((builder, type, classLoader, module) -> {
						//System.out.println("# instrumentando: " + type.getActualName());
						return builder
							.method(ElementMatchers.any())
							.intercept(MethodDelegation.to(new StackInterceptor()));						
							//.intercept(MethodDelegation.to(new ToMicroservicesInterceptor()));
							//.andThen(SuperMethodCall.INSTANCE));
						})
				.installOn(inst);
	}
}

