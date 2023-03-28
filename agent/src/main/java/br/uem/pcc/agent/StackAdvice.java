package br.uem.pcc.agent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import net.bytebuddy.asm.Advice;

public class StackAdvice {
	public StackAdvice() {
		System.out.println(">>>> new StackAdvice()");
	}
	
	@Advice.OnMethodEnter(inline = true)
	public static void onEnter(@Advice.Origin Method method,  @Advice.AllArguments Object[] arguments) {
		//System.out.println(">>> before ...");		
//		if (isGeneratedAtRuntime(method.getDeclaringClass())) {
//			return;
//		}
		//boolean featureStarted = StackSingleton.getInstance().methodIsFeatureEntryPoint(method) || StackSingleton.getInstance().getDeep() > 0;
		//boolean featureStarted = Stream.of(method.getDeclaringClass().getAnnotations()).map( StackAdvice::getNameFromAnnotation ).anyMatch( StackAdvice::isRestController ) || StackSingleton.getInstance().getDeep() > 0;
		boolean featureStarted = 
				StackSingleton.getInstance().getDeep() > 0
				|| (
				StackSingleton.getInstance().getDeep() == 0 
				&& (!method.getDeclaringClass().getName().contains("AbstractController")
				&& !method.getDeclaringClass().getName().contains("ConsultaEntidadeController") 
				&& !method.getDeclaringClass().getName().contains("ControladorDeMensagens") 
				&& !method.getDeclaringClass().getName().contains("SistemaControlador") 
				&& !method.getDeclaringClass().getName().contains("NotificacaoControlador")
				&& !method.getDeclaringClass().getName().contains("MensagensControlador")
				&& !method.getDeclaringClass().getName().contains("ManualUsuarioConsultaController")
				&& !method.getDeclaringClass().getName().contains("FAQController")
				&& !method.getDeclaringClass().getName().contains("ComunicacaoTodosUsuariosControlador")
				&& !method.getDeclaringClass().getName().contains("HierarquiaOrganizacionalController")
				&& !method.getDeclaringClass().getName().contains("ConfiguracaoSistemaController")
				&& !method.getDeclaringClass().getName().contains("TemplateControlador")
				&& !method.getDeclaringClass().getName().contains("ForneceArquivoControlador")
				&& !method.getDeclaringClass().getName().contains("SistemaService")
				&& !method.getDeclaringClass().getName().contains("NotificacaoService")
				&& (method.getDeclaringClass().getName().contains("Service") || method.getDeclaringClass().getName().contains("Controller") || method.getDeclaringClass().getName().contains("Controlador"))));
		if (featureStarted) {
			if (method.getDeclaringClass().getName().contains("TipoMultaVeiculoController")) {
				System.out.println(")))))))))))))))) " + method.getDeclaringClass().getName() + "." + method.getName());
				StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
				Arrays.stream(stackTraceElements).forEach(System.out::println);
			}
			int deep = StackSingleton.getInstance().increaseDeep();
			StackSingleton.getInstance().push(method, arguments);		
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

	@Advice.OnMethodExit(inline = true)
	public static void onExit(@Advice.Origin Method method,  @Advice.AllArguments Object[] arguments) {
		//System.out.println("<<< after ...");
		if (isGeneratedAtRuntime(method.getDeclaringClass())) {
			return;
		}
		//boolean featureStarted = StackSingleton.getInstance().methodIsFeatureEntryPoint(method) || StackSingleton.getInstance().getDeep() > 0;
		//boolean featureStarted = Stream.of(method.getDeclaringClass().getAnnotations()).map( StackAdvice::getNameFromAnnotation ).anyMatch( StackAdvice::isRestController ) || StackSingleton.getInstance().getDeep() > 0;  
		boolean featureStarted = StackSingleton.getInstance().getDeep() > 0;  
		if (featureStarted) {
			//System.out.println("<<< " + method.getDeclaringClass().getName() + "." + method.getName());
			StackSingleton.getInstance().decreaseDeep();
		}
		if (StackSingleton.getInstance().getDeep() == 0) {
			StackSingleton.getInstance().printStack(false);
			StackSingleton.getInstance().clearStack();
		}
	}

}
