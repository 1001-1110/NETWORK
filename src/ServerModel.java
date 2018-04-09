import java.util.ArrayList;

public class ServerModel {

	private ArrayList<User> onlineusers = new ArrayList<>();
	private ArrayList<ChatRoom> chatrooms = new ArrayList<>();
	
	public void addUser(User user) {
		onlineusers.add(user);
	}
	
	public User getUser(int i) {
		return onlineusers.get(i);
	}
	
	public void removeUser(int i) {
		onlineusers.remove(i);
	}
	
	public void addRoom(ChatRoom cr) {
		chatrooms.add(cr);
	}
	
	public ChatRoom getRoom(int i) {
		return chatrooms.get(i);
	}
	
	public void removeRoom(int i) {
		chatrooms.remove(i);
	}
	
	public int getNumOfUsers() {
		return onlineusers.size();
	}
	
	public int getNumOfChatRooms() {
		return chatrooms.size();
	}	
	
	public int getRooms() {
		return chatrooms.size();
	}
	
}
