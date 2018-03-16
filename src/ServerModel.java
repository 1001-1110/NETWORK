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
	
	public ChatRoom getRoom(int i) {
		return chatrooms.get(i);
	}
	
	public void removeUser(int i) {
		onlineusers.remove(i);
	}
	
	public void removeRoom(int i) {
		chatrooms.get(i);
	}
	
	public int getNumOfUsers() {
		return onlineusers.size();
	}
	
	public int getRooms() {
		return chatrooms.size();
	}
	
}
