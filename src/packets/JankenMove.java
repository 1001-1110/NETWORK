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
	
	public JankenMove(String move, String player, String opponent) {
		this.move = move;
		this.player = player;
		this.opponent = opponent;
	}

	public String getMove() {
		return move;
	}

	public String getOpponent() {
		return opponent;
	}

	public String getPlayer() {
		return player;
	}

}
