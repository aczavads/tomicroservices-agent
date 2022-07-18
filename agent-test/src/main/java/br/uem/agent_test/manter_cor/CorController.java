package br.uem.agent_test.manter_cor;

public class CorController {
	private CorService service = new CorService();
	
	public void salvar(Cor nova, boolean teste, int teste2, long ttt) {
		service.salvar(nova);
	}

	public void limparCache() {
	}

}
