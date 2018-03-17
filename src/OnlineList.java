import java.io.Serializable;

public class OnlineList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] onlineUsers;
			
	public OnlineList(String[] onlineUsers) {
		this.onlineUsers = onlineUsers;
	}
	
	public String[] getUsers() {
		return onlineUsers;
	}
	
}
