package br.uem.pcc.agent;

import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class StackElement {
	public Method method;
	public Object[] arguments;
	public int deep;
	public int numberOfCalls = 1;
	public Long sizeOf = 0L;
	private Object originObject;
	
	public StackElement(Object originObject, Method method, int deep, Object[] arguments) {
		this.originObject = originObject;
		this.method = method;
		this.deep = deep;
		this.arguments = arguments;
		//this.sizeOf = Stream.of(arguments).map(SizeOf::sizeOf).reduce(0L, (v, acc) -> acc+v);
		//this.sizeOf = calcSizeOf(arguments);
	}

	private Long calcSizeOf(Object[] arguments) {
		ObjectMapper objectMapper = new ObjectMapper();	
		try {
			String json = objectMapper.writeValueAsString(arguments);
			//System.out.println("json> " + json);
			return new Long(json.length()-2);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return -1L;
	}
	
	
	

}

