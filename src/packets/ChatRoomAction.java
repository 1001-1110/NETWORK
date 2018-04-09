package packets;

import java.io.Serializable;

public class ChatRoomAction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roomName;
	private String password;
	private String sender;
	boolean action;
	// action = create or delete
	// create = TRUE
	// delete = FALSE

	public ChatRoomAction(String roomName, String password, String sender, boolean action) {
		this.roomName = roomName;
		this.password = password;
		this.sender = sender;
		this.action = action;
	}

	public String getSender() {
		return sender;
	}

	public String getPassword() {
		return password;
	}	
	
	public String getRoomName() {
		return roomName;
	}

	public boolean getAction() {
		return action;
	}

}
