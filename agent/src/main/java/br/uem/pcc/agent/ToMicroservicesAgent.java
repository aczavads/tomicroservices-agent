package br.uem.pcc.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;

public class ToMicroservicesAgent {
	
	public static void premain(String arg, Instrumentation inst) throws Exception {
		System.out.println(">>>>>>>>> ToMicroservicesAgent loading...v26");
//		System.out.println(">>>>>>>>>>>>>>>>>> loading agent jar file...");
//		inst.appendToBootstrapClassLoaderSearch(new JarFile("/home/arthur/Documents/doutorado/workspace-estudos/tomicroservices-agent/agent/target/deps.jar"));			
//		System.out.println(">>>>>>>>>>>>>>>>>> agent jar file loaded!   xxx");
		
        File temp = Files.createTempDirectory("tmpzzzz").toFile();        
        
        Map<TypeDescription.ForLoadedType, byte[]> toInject = new HashMap<>();
//        toInject.put(new TypeDescription.ForLoadedType(ToMicroservicesAgent.class), ClassFileLocator.ForClassLoader.read(ToMicroservicesAgent.class));
        toInject.put(new TypeDescription.ForLoadedType(StackSingleton.class), ClassFileLocator.ForClassLoader.read(StackSingleton.class));
        toInject.put(new TypeDescription.ForLoadedType(Clóvis.class), ClassFileLocator.ForClassLoader.read(Clóvis.class));
        toInject.put(new TypeDescription.ForLoadedType(StackElement.class), ClassFileLocator.ForClassLoader.read(StackElement.class));
        toInject.put(new TypeDescription.ForLoadedType(StackAdvice.class), ClassFileLocator.ForClassLoader.read(StackAdvice.class));
        
        ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst).inject(toInject);
         
		new AgentBuilder.Default()		      
//		        .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
		        .with(new AgentBuilder.InjectionStrategy.UsingInstrumentation(inst, temp))
		         .disableClassFormatChanges()
				.with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
				.with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
				.with(AgentBuilder.TypeStrategy.Default.REDEFINE)		
		        //.type(ElementMatchers.not(ElementMatchers.nameStartsWith("br.uem")).and(ElementMatchers.nameStartsWith("br").and(ElementMatchers.not(ElementMatchers.nameContains("$")))))
		        //.type(ElementMatchers.nameStartsWith("br.uem.agent_test"))
		        .type(ElementMatchers.nameStartsWith("br.com.webpublico"))
				.transform((builder, type, classLoader, module) -> {
						//System.out.println(">>> Instrumentando " + type.getActualName());
						return builder
							.visit(Advice.to(StackAdvice.class).on(ElementMatchers.isMethod()));
						})			
				.installOn(inst);		
	}
}

