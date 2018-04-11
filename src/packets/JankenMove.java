package packets;

import java.io.Serializable;

public class JankenMove implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String move;
	public String opponent;
	public String player;
	
	public JankenMove(String move) {
		this.move = move;
	}

}
