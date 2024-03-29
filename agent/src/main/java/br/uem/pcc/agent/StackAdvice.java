package br.uem.pcc.agent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner.Typing;

public class StackAdvice {
	public StackAdvice() {
		System.out.println(">>>> new StackAdvice()");
	}
	
	@Advice.OnMethodEnter(inline = true)
	public static void onEnter(@Advice.Origin Method method,  @Advice.This(typing = Typing.DYNAMIC) Object originObject, @Advice.AllArguments Object[] arguments) {
//		//System.out.println(">>> before ...");		
////		if (isGeneratedAtRuntime(method.getDeclaringClass())) {
////			return;
////		}
//		//boolean featureStarted = StackSingleton.getInstance().methodIsFeatureEntryPoint(method) || StackSingleton.getInstance().getDeep() > 0;
//		//boolean featureStarted = Stream.of(method.getDeclaringClass().getAnnotations()).map( StackAdvice::getNameFromAnnotation ).anyMatch( StackAdvice::isRestController ) || StackSingleton.getInstance().getDeep() > 0;
//		boolean featureStarted = 
//				StackSingleton.getInstance().getDeep() > 0
//				|| (
//				StackSingleton.getInstance().getDeep() == 0 
//				&& (!method.getDeclaringClass().getName().contains("AbstractController")
//				&& !method.getDeclaringClass().getName().contains("ConsultaEntidadeController") 
//				&& !method.getDeclaringClass().getName().contains("ControladorDeMensagens") 
//				&& !method.getDeclaringClass().getName().contains("SistemaControlador") 
//				&& !method.getDeclaringClass().getName().contains("NotificacaoControlador")
//				&& !method.getDeclaringClass().getName().contains("MensagensControlador")
//				&& !method.getDeclaringClass().getName().contains("ManualUsuarioConsultaController")
//				&& !method.getDeclaringClass().getName().contains("FAQController")
//				&& !method.getDeclaringClass().getName().contains("ComunicacaoTodosUsuariosControlador")
//				&& !method.getDeclaringClass().getName().contains("HierarquiaOrganizacionalController")
//				&& !method.getDeclaringClass().getName().contains("ConfiguracaoSistemaController")
//				&& !method.getDeclaringClass().getName().contains("TemplateControlador")
//				&& !method.getDeclaringClass().getName().contains("ForneceArquivoControlador")
//				&& !method.getDeclaringClass().getName().contains("SistemaService")
//				&& !method.getDeclaringClass().getName().contains("NotificacaoService")
//				&& (method.getDeclaringClass().getName().contains("Service") || method.getDeclaringClass().getName().contains("Controller") || method.getDeclaringClass().getName().contains("Controlador"))));
//		if (featureStarted) {
//			if (method.getDeclaringClass().getName().contains("TipoMultaVeiculoController")) {
//				System.out.println(")))))))))))))))) " + method.getDeclaringClass().getName() + "." + method.getName());
//				StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//				Arrays.stream(stackTraceElements).forEach(System.out::println);
//			}
//			int deep = StackSingleton.getInstance().increaseDeep();
//			StackSingleton.getInstance().push(method, arguments);		
//		}			
//		String originClassName = originObject.getClass().getName();
//		try {
//			boolean isSpringClass = originObject.getClass().getSimpleName().contains("BySpring");
////			if (isSpringClass) {
////				System.out.println(">>>>>> " + originClassName + "." + method.getName() + "Class: " +  originObject.getClass().getName());
////				return;
////			}
//			boolean isProxy = originObject.getClass().getSimpleName().startsWith("$Proxy");
//			if (isProxy) {
//				Class<?>[] interfaces = originObject.getClass().getInterfaces();
//				if (interfaces.length == 0) {
//					return;
//				}
//				originClassName = interfaces[0].getName();
//				//System.out.println(">>>>> " + method.toString());
//				//method = interfaces[0].getDeclaredMethod(method.getName(), method.getParameterTypes());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//		System.out.println(">>>>>> " + originClassName + "." + method.getName() + "Class: " +  method.getDeclaringClass().getName());
		boolean featureStarted = StackSingleton.getInstance().getDeep() > 0 || (
					StackSingleton.getInstance().getDeep() == 0 && (
							method.getDeclaringClass().getName().contains("controller") ||
							method.getDeclaringClass().getName().contains("Controller") ||
							method.getDeclaringClass().getName().contains("controler") ||
							method.getDeclaringClass().getName().contains("Controler") ||
							method.getDeclaringClass().getName().contains("controlador") ||
							method.getDeclaringClass().getName().contains("Controlador") 
					)
				);
		if (featureStarted) {
			int deep = StackSingleton.getInstance().increaseDeep(); 
			StackSingleton.getInstance().push(originObject, method, arguments);		
		}
	}
	
	
	public static boolean isRestController(String annotationName) {
		return annotationName.contains("RestController");
	}
	public static String getNameFromAnnotation(Annotation a) {
		//System.out.println(" ----------------->> " + a.annotationType().getSimpleName());
		return a.annotationType().getSimpleName();
	}
	
	public static boolean isGeneratedAtRuntime(Class clazz) {
		return clazz.getName().lastIndexOf('$') >= 0;
	}

	@Advice.OnMethodExit(inline = false)
	public static void onExit(@Advice.Origin Method method, @Advice.This(typing = Typing.DYNAMIC) Object originObject,  @Advice.AllArguments Object[] arguments) {
//		//System.out.println("<<< after ...");
//		if (isGeneratedAtRuntime(method.getDeclaringClass())) {
//			return;
//		}
//		//boolean featureStarted = StackSingleton.getInstance().methodIsFeatureEntryPoint(method) || StackSingleton.getInstance().getDeep() > 0;
//		//boolean featureStarted = Stream.of(method.getDeclaringClass().getAnnotations()).map( StackAdvice::getNameFromAnnotation ).anyMatch( StackAdvice::isRestController ) || StackSingleton.getInstance().getDeep() > 0;  
//		boolean featureStarted = StackSingleton.getInstance().getDeep() > 0;  
//		if (featureStarted) {
//			//System.out.println("<<< " + method.getDeclaringClass().getName() + "." + method.getName());
//			StackSingleton.getInstance().decreaseDeep();
//		}
//		if (StackSingleton.getInstance().getDeep() == 0) {
//			StackSingleton.getInstance().printStack(false);
//			StackSingleton.getInstance().clearStack();
//		}
//		String originClassName = originObject.getClass().getName();
//		try {
//			boolean isSpringClass = originObject.getClass().getSimpleName().contains("BySpring");
//			if (isSpringClass) {
//				return;
//			}
//			boolean isProxy = originObject.getClass().getSimpleName().startsWith("$Proxy");
//			if (isProxy) {
//				Class<?>[] interfaces = originObject.getClass().getInterfaces();
//				if (interfaces.length == 0) {
//					return;
//				}
//				originClassName = interfaces[0].getName();
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		StackSingleton.getInstance().decreaseDeep(method); 
		if (StackSingleton.getInstance().getDeep() == 0) {
			StackSingleton.getInstance().printStack(2);
			StackSingleton.getInstance().clearStack();
		}
	}
}
