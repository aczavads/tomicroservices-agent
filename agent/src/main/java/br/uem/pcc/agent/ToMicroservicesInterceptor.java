package br.uem.pcc.agent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class ToMicroservicesInterceptor {
	private Map<String, List<String>> featureEntryPoints = loadFeatureEntryPoints();
	private String actualFeature = null;
	
	@RuntimeType
	public Object intercept(@Origin Method method, @SuperCall Callable<?> callable,@AllArguments Object[] arguments) {
		//System.out.println("Chamou: " + method.getDeclaringClass().getName() + "." + method.getName());
		try {
			if (SingleNivel.getInstance().current() == 0 && methodIsFeatureEntryPoint(method)) {
				printFeature(method);
			}
			System.out.println("Class:" + method.getDeclaringClass().getName() 
				+ "#Method:" +  method.getName() 
				+ "#SizeOf:0" //+ Stream.of(arguments).map(SizeOf::sizeOf).reduce((v, acc) -> acc+v).get()
				+ "#Deep:" + (SingleNivel.getInstance().increment()) 
				+ "#Thread:" + Thread.currentThread().getName());
			Object result = callable.call();
			//System.out.println("<<< " + method.getName() + ":" + (SingleNivel.getInstance().current()));
			SingleNivel.getInstance().decrement();
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	private void printFeature(Method method) {
		String featureLine = "##SF:#" + getFeatureName(method) + "<" +method.getDeclaringClass().getName() + "." + method.getName() +">";
		System.out.println(featureLine);
	}

	private String getFeatureName(Method method) {		
//		return "XXX";
		final String methodName = method.getDeclaringClass().getName() + "." + method.getName();
		return featureEntryPoints
			      .entrySet()
			      .stream()
			      .filter(entry -> entry.getValue().contains(methodName))
			      .map(Map.Entry::getKey).findFirst().get();
	}

	private boolean methodIsFeatureEntryPoint(Method method) {		 
		return (method.getName().endsWith("main"));
	}

	private Map<String, List<String>> loadFeatureEntryPoints() {
		HashMap<String, List<String>> featureEntryPoints = new HashMap<>();
		featureEntryPoints.put("ManterCor", Arrays.asList("br.uem.agent_test.AppAgentTest.main"));
		return featureEntryPoints;
	}
}
