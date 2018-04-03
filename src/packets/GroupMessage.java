package packets;
import java.io.Serializable;
import java.util.ArrayList;

public class GroupMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String content;
	private String sender;
	private ArrayList<String> participants;
	
	public GroupMessage(String sender, ArrayList<String> participants, String content) {
		this.sender = sender;
		this.participants = participants;
		this.content = content;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getContent() {
		return content;
	}
	
	public ArrayList<String> getParticipants() {
		return participants;
	}

}
