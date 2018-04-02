import java.util.ArrayList;

public class ChatRoom {
	
	private ArrayList<String> participants;
	private String roomName;
	private String password;

	public ChatRoom(String roomName, String password) {
		this.roomName = roomName;
		this.password = password;
	}
	
	public void addParticipant(String participant) {
		participants.add(participant);
	}
	
	public void removeParticipant(String participant) {
		participants.remove(participant);
	}

	public ArrayList<String> getParticipants() {
		return participants;
	}

	public String getRoomName() {
		return roomName;
	}

	public String getPassword() {
		return password;
	}
	
	
	
}
