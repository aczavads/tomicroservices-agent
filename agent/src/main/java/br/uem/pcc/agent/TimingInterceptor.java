package br.uem.pcc.agent;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class TimingInterceptor {
	@RuntimeType
	public Object intercept(@Origin Method method, @SuperCall Callable<?> callable) {
		long start = System.nanoTime();
		try {
			System.out.println("Opa opa opa!");
			return callable.call();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println(method + " took " + (System.nanoTime() - start));
		}
		return null;
	}

}
