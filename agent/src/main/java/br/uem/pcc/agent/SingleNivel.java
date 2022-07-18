package br.uem.pcc.agent;

public class SingleNivel {
	private static SingleNivel instance = new SingleNivel();
	private int nivel = 0;
	
	private SingleNivel() {
		
	}
	
	public static SingleNivel getInstance() {
		return instance;
	}

	public int increment() {
		return ++nivel;
	}
	
	public int decrement() {
		return nivel--;
	}

	public int current() {
		return nivel;
	}

}
