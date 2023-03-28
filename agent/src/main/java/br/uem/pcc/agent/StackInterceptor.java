package br.uem.pcc.agent;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class StackInterceptor {
	private static StackInterceptor instance = new StackInterceptor();
	
	public StackInterceptor() {
		System.out.println(">>> StackInterceptor()");
	}
	
	public static StackInterceptor getInstance() { 
		return instance;
	}
	
	@RuntimeType
	public synchronized Object intercept(@Origin Method method, @SuperCall Callable<?> callable, @AllArguments Object[] arguments) {
		boolean featureStarted = StackSingleton.getInstance().methodIsFeatureEntryPoint(method) || StackSingleton.getInstance().getDeep() > 0;
		if (featureStarted) {
			int deep = StackSingleton.getInstance().increaseDeep();
			StackSingleton.getInstance().push(method, arguments);
			
			System.out.println(">>> " + method.getDeclaringClass().getName() + "." +  method.getName());
		}
		try {
			Object result = callable.call();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (featureStarted) {
				StackSingleton.getInstance().decreaseDeep();
			}
		}
	}

}
