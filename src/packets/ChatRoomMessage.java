package packets;

import java.io.Serializable;

public class ChatRoomMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String content;
	private String sender;
	private String roomName;
	
	public ChatRoomMessage(String content, String sender, String roomName) {
		this.content = content;
		this.sender = sender;
		this.roomName = roomName;
	}

	public String getContent() {
		return content;
	}

	public String getSender() {
		return sender;
	}

	public String getRoomName() {
		return roomName;
	}
	
}
