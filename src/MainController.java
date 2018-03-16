
public class MainController {
	
	private MainView mv;
	
	public MainController() {
		mv = new MainView(this);
	}
	
	public void newServer(int port) {
		mv.setVisible(false);
		ServerController sc = new ServerController(port);
	}
	
	public void newClient(String serverIP, int port, String username) {
		mv.setVisible(false);
		ClientController cc = new ClientController(serverIP, port, username);
	}
}
