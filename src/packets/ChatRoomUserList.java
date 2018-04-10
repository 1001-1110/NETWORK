package packets;

import java.io.Serializable;

public class ChatRoomUserList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String roomName;
	private String[] participants;
	
	public ChatRoomUserList(String roomName, String[] participants) {
		this.roomName = roomName;
		this.participants = participants;
	}

	public String getRoomName() {
		return roomName;
	}

	public String[] getParticipants() {
		return participants;
	}

}
