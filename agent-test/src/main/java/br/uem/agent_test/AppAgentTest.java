package br.uem.agent_test;

import br.uem.agent_test.efetuar_login.LoginController;
import br.uem.agent_test.manter_cor.Cor;
import br.uem.agent_test.manter_cor.CorController;
import br.uem.pcc.agent.StackSingleton;

public class AppAgentTest {

	public static void main(String[] args) {
		
		testarManterCor();
		
		testarEfetuarLogin();
		
		StackSingleton.getInstance().printStack();
	}

	public static void testarEfetuarLogin() { 
		LoginController controller = new LoginController();
		controller.login("fernando.felizardo","teste123");
		controller.login("arthur.zavadski","teste123");
		
	}

	public static void testarManterCor() {
		CorController controller = new CorController();
		for (int i = 0; i < 10; i++) {
			Cor nova = new Cor("Sigla" + i, "Nome " + i);
			controller.salvar(nova, false, 1, 55L);
		}
		controller.limparCache();
		System.out.println("Foi.");		
	}
}
