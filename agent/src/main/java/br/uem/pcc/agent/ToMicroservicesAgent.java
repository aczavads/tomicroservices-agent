package br.uem.pcc.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import javax.annotation.PostConstruct;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class ToMicroservicesAgent {
	
	public static void premain(String arg, Instrumentation inst) throws Exception {
		System.out.println(">>>>>>>>> ToMicroservicesAgent loading...v63");
			
        File temp = Files.createTempDirectory("tmpzzzz").toFile();        
        
        Map<TypeDescription.ForLoadedType, byte[]> toInject = new HashMap<>();
        toInject.put(new TypeDescription.ForLoadedType(StackSingleton.class), ClassFileLocator.ForClassLoader.read(StackSingleton.class));
        toInject.put(new TypeDescription.ForLoadedType(Clóvis.class), ClassFileLocator.ForClassLoader.read(Clóvis.class));
        toInject.put(new TypeDescription.ForLoadedType(StackElement.class), ClassFileLocator.ForClassLoader.read(StackElement.class));
        toInject.put(new TypeDescription.ForLoadedType(StackAdvice.class), ClassFileLocator.ForClassLoader.read(StackAdvice.class));
        
        //ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst).inject(toInject);
         
        MethodDelegation interceptor = MethodDelegation.to(StackAdvice.class);
		new AgentBuilder.Default()		      
		        .with(new AgentBuilder.InjectionStrategy.UsingInstrumentation(inst, temp))
		        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
		        //.disableClassFormatChanges()
		        .type(ElementMatchers.not(ElementMatchers.isAbstract().or(ElementMatchers.nameContains("$$")) ).and((ElementMatchers.nameStartsWith("br.com.webpublico").or(ElementMatchers.hasSuperType(ElementMatchers.nameStartsWith("br.com.webpublico"))))))
//				.transform((builder, type, classLoader, module) -> {
//					return builder.method(ElementMatchers.isMethod()).intercept(interceptor);
//				})			
				.transform((builder, type, classLoader, module) -> {
						return builder.method(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isAnnotatedWith(PostConstruct.class)))).intercept(Advice.to(StackAdvice.class));
				 })			
//				.transform((builder, type, classLoader, module) -> {
//					return builder
//						.visit(Advice.to(StackAdvice.class).on(ElementMatchers.isMethod()));
//					})	
				.installOn(inst);		
	}
}

