package br.uem.agent_test.efetuar_login;


public class LoginController {
	private Teste teste = new Teste();
	
	public boolean login(String username, String password) {
		return isValid();
	}

	private boolean isValid() {
		return true;
	}
	
	public void testar(Teste tt) {
		
	}

}
