package br.uem.pcc.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StackSingleton {
	private Map<String, List<String>> featureEntryPoints = loadFeatureEntryPoints();
	private static StackSingleton instance = new StackSingleton();
	private Map<String, Clóvis> callstackPerThread = new HashMap<String,Clóvis>();
	
	public synchronized void push(Object originObject, Method m, Object[] arguments) {
		getClóvisOfCurrentThread().push(originObject, m, arguments);
	}
	
	public synchronized int increaseDeep() {
		return getClóvisOfCurrentThread().increaseDeep();
	}
	public synchronized int decreaseDeep() {
		return getClóvisOfCurrentThread().decreaseDeep();
	}
	
	private synchronized Clóvis getClóvisOfCurrentThread() {
		String currentThreadName = Thread.currentThread().getName();
		Clóvis clóvis = callstackPerThread.getOrDefault(currentThreadName, new Clóvis());
		callstackPerThread.put(currentThreadName, clóvis);
		return clóvis;
	}
	private synchronized void clearClóvisOfCurrentThread() {
		String currentThreadName = Thread.currentThread().getName();
		callstackPerThread.remove(currentThreadName);
	}
	public synchronized void printStack() {
		printStack(0);
	}
	
	public synchronized void printStack(int minSizeToPrint) {		
		Stack<StackElement> callstack = getClóvisOfCurrentThread().getCallstack();
		
		StackElement[] elements = callstack.toArray(new StackElement[] {});
		if (elements.length < minSizeToPrint) {
			return;
		}
		System.out.println(">>>>>>>>>>>>>>>>>> printStack!!! " + Thread.currentThread().getName());
		System.out.println(elements.length);
		try (PrintWriter logFileWriter = new PrintWriter(new FileWriter("/tmp/to-microservices-log.txt", true))) {			
//		try (PrintWriter logFileWriter = new PrintWriter(new FileWriter("C:\\mestrado\\to-microservices-log.txt", true))) {			
			printFeatureLine(elements[0], logFileWriter);
			Stream.of(elements).forEach(e -> {
				if (e.getDeep() == 1 && methodIsFeatureEntryPoint(e.getMethod())) {
					printFeature(e.getMethod());
				}
				String declaringClassName = e.getMethod().getDeclaringClass().getName();
				declaringClassName = getDeclaringClassName(e);

				String logLine = "Class:" + declaringClassName + 
						"#Method:" +  e.getMethod().getName() + 
						//"#SizeOf:" + e.getSizeOf() + 
						"#SizeOf:" + e.getNumberOfCalls() + 
						"#Deep:" + e.getDeep() + 
						//"#numberOfCalls:" + e.getNumberOfCalls()+
						//"#Thread:" + Thread.currentThread().getName();
						"#Thread:main";
				logFileWriter.println(logLine);
			});
			logFileWriter.flush();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private String getDeclaringClassName(StackElement e) {
		//String declaringClassName = e.getMethod().getDeclaringClass().getName();
		String declaringClassName = e.getOriginObject().getClass().getName();
		boolean isProxy = e.getOriginObject().getClass().getSimpleName().startsWith("$Proxy");
		if (isProxy) {
			Class<?>[] interfaces = e.getOriginObject().getClass().getInterfaces();
			declaringClassName = interfaces[0].getName();
		}
		return declaringClassName;
	}
	private void printFeatureLine(StackElement stackElement, PrintWriter logFileWriter) {
		//SF:SF:#ManterAluno<teste.gerado.ManterAluno.executar>
		String declaringClassName = getDeclaringClassName(stackElement);		
		String featureLine = "SF:SF:#" + extractFeatureNameFromClassName(declaringClassName) + "<" + declaringClassName+ "." + stackElement.method.getName() + ">";
		logFileWriter.println(featureLine);
		
	}
	
	public static String extractFeatureNameFromClassName(String input) {
        Pattern pattern = Pattern.compile("(?<=\\.)[A-Z][a-z|\\d]+");
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "regex-de-feature-falhou"; // ou lance uma exceção, caso a palavra não seja encontrada
        }
    }

	public synchronized void clearStack() {
		clearClóvisOfCurrentThread();
	}

	public synchronized int getDeep() {
		return getClóvisOfCurrentThread().getDeep();
	}
	
	public static StackSingleton getInstance() {
		return instance;
	}
	private void printFeature(Method method) {
		String featureLine = "##SF:#" + getFeatureName(method) + "<" +method.getDeclaringClass().getName() + "." + method.getName() +">";
		System.out.println(featureLine);
	}

	private String getFeatureName(Method method) {		
		final String methodName = method.getDeclaringClass().getName() + "." + method.getName();
		return featureEntryPoints
			      .entrySet()
			      .stream()
			      .filter(entry -> entry.getValue().contains(methodName))
			      .map(Map.Entry::getKey).findFirst().orElse("no-feature");
	}

	public boolean methodIsFeatureEntryPoint(Method method) {		 
		//return (method.getName().endsWith("main"));
		return !getFeatureName(method).equalsIgnoreCase("no-feature");
	}

	private Map<String, List<String>> loadFeatureEntryPoints() {
		HashMap<String, List<String>> featureEntryPoints = new HashMap<>();
//		featureEntryPoints.put("ManterCor", Arrays.asList("br.uem.agent_test.AppAgentTest.testarManterCor"));
//		featureEntryPoints.put("EfetuarLogin", Arrays.asList("br.uem.agent_test.AppAgentTest.testarEfetuarLogin"));
//		featureEntryPoints.put("ObterGrupoEconomico", Arrays.asList("com.accountfy.components.grupoeconomicoconfig.GrupoEconomicoConfigController.getByGrupo"));
//		featureEntryPoints.put("DemonstrativoFinanceiro", Arrays.asList("com.accountfy.components.tabelao.PainelResultadoRealController.getPainelRealDF"));		
//		featureEntryPoints.put("ManterUsuario", Arrays.asList("com.accountfy.data.extractor.controller.UserController.getUsers"));
		return featureEntryPoints;
	}
	
}
