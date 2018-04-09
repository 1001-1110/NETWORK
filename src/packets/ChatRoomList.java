package packets;

import java.io.Serializable;

public class ChatRoomList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String[] chatRoomList;
	private int[] numOfParticipants;
	
	public ChatRoomList(String[] chatRoomList, int[] numOfParticipants) {
		this.chatRoomList = chatRoomList;
		this.numOfParticipants = numOfParticipants;
	}
	
	public String[] getChatRoomList() {
		return chatRoomList;
	}	
	
	public int[] getNumOfParticipants() {
		return numOfParticipants;
	}


}
