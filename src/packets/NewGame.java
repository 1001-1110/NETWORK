package packets;

import java.io.Serializable;

public class NewGame implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String opponent;
	public String player;
	
	public NewGame(String player, String opponent) {
		this.opponent = opponent;
		this.player = player;
	}

	public String getOpponent() {
		return opponent;
	}

	public String getPlayer() {
		return player;
	}

}
