package br.uem.agent_test.manter_cor;

public class CorService {

	public void salvar(Cor nova) {
		final String sigla = nova.getSigla(); 
		final String nome = nova.getNome();		
		depoisDeSalvar();
	}

	private void depoisDeSalvar() {
	}

}
