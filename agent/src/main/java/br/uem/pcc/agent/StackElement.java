package br.uem.pcc.agent;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import lombok.Data;

@Data
public class StackElement {
	public Method method;
	public Object[] arguments;
	public int deep;
	public int numberOfCalls = 1;
	public Long sizeOf = 0L;
	
	public StackElement(Method method, int deep, Object[] arguments) {
		this.method = method;
		this.deep = deep;
		this.arguments = arguments;
		this.sizeOf = Stream.of(arguments).map(SizeOf::sizeOf).reduce(0L, (v, acc) -> acc+v);
	}
	
	
	

}

