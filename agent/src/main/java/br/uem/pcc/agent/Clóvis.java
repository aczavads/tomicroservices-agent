package br.uem.pcc.agent;

import java.lang.reflect.Method;
import java.util.Stack;

public class Clóvis {
	private Stack<StackElement> callstack = new Stack<>();
	private int deep = 0;

	public Stack<StackElement> getCallstack() {
		return callstack;
	}
	
	public synchronized void push(Method m, Object[] arguments) {
		boolean alreadCalled = false;
						
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

	public int getDeep() {
		return deep;
	}
}
