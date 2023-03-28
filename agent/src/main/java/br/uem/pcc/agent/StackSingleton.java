package br.uem.pcc.agent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

public class StackSingleton {
	private Map<String, List<String>> featureEntryPoints = loadFeatureEntryPoints();
	private static StackSingleton instance = new StackSingleton();
	private Map<String, Clóvis> callstackPerThread = new HashMap<String,Clóvis>();
	
	public synchronized void push(Method m, Object[] arguments) {
		getClóvisOfCurrentThread().push(m, arguments);
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
		printStack(true);
	}
	
	public synchronized void printStack(boolean printEmptyStack) {
		Stack<StackElement> callstack = getClóvisOfCurrentThread().getCallstack();
		
		StackElement[] elements = callstack.toArray(new StackElement[] {});
		if (!printEmptyStack && elements.length == 0) {
			return;
		}
		System.out.println(">>>>>>>>>>>>>>>>>> printStack!!! " + Thread.currentThread().getName());
		System.out.println(elements.length);
		Stream.of(elements).forEach(e -> {
			if (e.getDeep() == 1 && methodIsFeatureEntryPoint(e.getMethod())) {
				printFeature(e.getMethod());
			}
			System.out.println("Class:" + e.getMethod().getDeclaringClass().getName() + 
					"#Method:" +  e.getMethod().getName() + 
					"#SizeOf:" + e.getSizeOf() + 
					"#Deep=" + e.getDeep() + 
					"#numberOfCalls=" + e.getNumberOfCalls()+
					"#Thread:" + Thread.currentThread().getName());		
		});
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
