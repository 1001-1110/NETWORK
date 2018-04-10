package packets;

import java.io.Serializable;

public class ChatRoomRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roomName;
	private String password;
	private String sender;
	private String[] participants;
	boolean action;
	// action = join or remove
	// join = TRUE
	// remove = FALSE

	public ChatRoomRequest(String roomName, String password, String sender, boolean action) {
		this.roomName = roomName;
		this.password = password;
		this.sender = sender;
		this.action = action;
	}

	public String[] getParticipants() {
		return participants;
	}

	public void setParticipants(String[] participants) {
		this.participants = participants;
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
