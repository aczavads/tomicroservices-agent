package br.uem.pcc.agent;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

public class StackSingleton {
	private Map<String, List<String>> featureEntryPoints = loadFeatureEntryPoints();
	private static StackSingleton instance = new StackSingleton();
	private Map<String, Stack<StackElement>> callstackPerThread = new HashMap<String,Stack<StackElement>>();
	private int deep = 0;
	
	public synchronized void push(Method m, Object[] arguments) {
		//this.callstack.push(new StackElement(m, deep, arguments));
		boolean alreadCalled = false;
		
		String currentThreadName = Thread.currentThread().getName();
		Stack<StackElement> callstack = callstackPerThread.getOrDefault(currentThreadName, new Stack<StackElement>());
		
		for (int i = callstack.size()-1; i >= 0; i--) {
			StackElement element = callstack.get(i);
			
			if (element.getMethod().equals(m) && element.getDeep() == deep) {
				element.setNumberOfCalls(element.getNumberOfCalls()+1);
				alreadCalled = true;
				break;
			}
		}
		if (!alreadCalled) {
			callstack.push(new StackElement(m, deep, arguments));
		}
	}
	
	public int increaseDeep() {
		return ++deep;
	}
	public int decreaseDeep() {
		return --deep;
	}
	
	public synchronized void printStack() {
		StackElement[] elements = callstack.toArray(new StackElement[] {});
		System.out.println(elements.length);
		Stream.of(elements).forEach(e -> {
			if (e.getDeep() == 1 && methodIsFeatureEntryPoint(e.getMethod())) {
				printFeature(e.getMethod());
			}
			System.out.println("Class:" + e.getMethod().getDeclaringClass().getName() + "#Method:" +  e.getMethod().getName() + "#SizeOf:" + e.getSizeOf() + "#Deep=" + e.getDeep() + "#numberOfCalls=" + e.getNumberOfCalls()+"#Thread:main");		
		});
	}

	public int getDeep() {
		return deep;
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
		featureEntryPoints.put("ManterCor", Arrays.asList("br.uem.agent_test.AppAgentTest.testarManterCor"));
		featureEntryPoints.put("EfetuarLogin", Arrays.asList("br.uem.agent_test.AppAgentTest.testarEfetuarLogin"));
		return featureEntryPoints;
	}
	
}
