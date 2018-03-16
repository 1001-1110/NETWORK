
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController {
	
	private ClientView cv;
	private String username;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private ArrayList<PrivateChatView> privateChats;
	private ArrayList<GroupChatView> groupChats;
	
    public ClientController(String serverIP, int port, String username){
    	cv = new ClientView(this,username);
    	this.username = username;
    	privateChats = new ArrayList<>();
    	groupChats = new ArrayList<>();
    	connectSocket(serverIP,port,username);
    }
    
    public void createPrivateChat(String username) {
    	if(this.username.equals(username)) {
    		return;
    	}
    	for(int i = 0 ; i < privateChats.size() ; i++) {
    		if(privateChats.get(i).getName().equals(username)) {
    			privateChats.get(i).setVisible(true);
    			return;
    		}
    	}
    	privateChats.add(new PrivateChatView(this,this.username,username));
    }
    
    public void createGroupChat(ArrayList<String> participants) {
    	for(int i = 0 ; i < groupChats.size() ; i++) {
    		if(groupChats.get(i).getNames().size() == participants.size())
	    		if(compareParticipants(groupChats.get(i).getNames(),participants)) {
	    			return;	    			
	    		}
    	}
    	String participantsString = new String();
    	for(int i = 0 ; i < participants.size(); i++) {
    		participantsString += participants.get(i) + " ";
    	}
    	groupChats.add(new GroupChatView(this,this.username,participants,participantsString));
    } 
    
    private void receivePrivateChat(Message message) {
    	for(int i = 0 ; i < privateChats.size() ; i++) {
    		if(privateChats.get(i).getName().equals(message.getSender())) {
    			privateChats.get(i).setVisible(true);
    			privateChats.get(i).updateChat(message.getSender()+": "+message.getContent());
    			return;
    		}
    	}
    	privateChats.add(new PrivateChatView(this,this.username,message.getSender()));
    	privateChats.get(privateChats.size()-1).updateChat(message.getSender()+": "+message.getContent());
    }

    private void receiveGroupChat(GroupMessage groupMessage) {
    	for(int i = 0 ; i < groupChats.size() ; i++) {
    		if(groupChats.get(i).getNames().size() == groupMessage.getParticipants().size())
	    		if(compareParticipants(groupChats.get(i).getNames(),groupMessage.getParticipants())) {
	    			groupChats.get(i).setVisible(true);
	    			groupChats.get(i).updateChat(groupMessage.getSender()+": "+groupMessage.getContent());
	    			return;	    			
	    		}
    	}
    	String participantsString = new String();
    	for(int i = 0 ; i < groupMessage.getParticipants().size(); i++) {
    		participantsString += groupMessage.getParticipants().get(i) + " ";
    	}
    	groupChats.add(new GroupChatView(this,this.username,groupMessage.getParticipants(),participantsString));
    	groupChats.get(groupChats.size()-1).updateChat(groupMessage.getSender()+": "+groupMessage.getContent());
    }    
    
    private boolean compareParticipants(ArrayList<String> groupChat, ArrayList<String> groupMessage) {
    	for(int i = 0 ; i < groupMessage.size() ; i++) {
    		if(!groupChat.contains(groupMessage.get(i)))
    			return false;
    	}
    	return true;
    }
    
    private void connectSocket(String serverIP, int port, String username) {
    	
    	try {
    	    socket = new Socket(serverIP, port);
    	    output = new ObjectOutputStream(socket.getOutputStream());
    	    output.writeObject(username);
    	    input = new ObjectInputStream(socket.getInputStream());
    	    Object message;
    	    while((message = input.readObject()) == null);
    	    	cv.updateChat((String) message);     
    	    clientThread();
    	} catch(Exception ex) {
    		cv.updateChat("Connection refused.");
    	}    			

    }
    
    private void clientThread() {
    	
    	Thread socketConnect = new Thread() {
    		public void run() {
    			boolean isRunning = true;
    			while(isRunning) {
    		        try {
    		            Object o;
    		            if((o = input.readObject()) != null) {
    		            	if(o instanceof Message) {
    		            		if(((Message) o).getReceiver().equals("all"))
    		            			cv.updateChat(((Message) o).getSender()+": "+((Message) o).getContent());
    		            		else
    		            			receivePrivateChat((Message) o);
    		            	}else if(o instanceof String[]) {
    		            		cv.updateOnline((String[]) o);
    		            	}else if(o instanceof GroupMessage) {
    		            		receiveGroupChat((GroupMessage) o);
    		            	}
    		            }
    		        } catch(Exception ex) {
    		        	cv.updateChat("Lost connection with server.");
    		        	isRunning = false;
    		        }         				
    			}
    		}
    	};  
    	
        socketConnect.start();
    	
    }
    
    public void sendMessage(String content, String receiver) {
	    try {
			output.writeObject(new Message(username,receiver,content));
		} catch (IOException e) {}
    }
    
    public void sendGroupMessage(String content, ArrayList<String> participants) {
	    try {
			output.writeObject(new GroupMessage(username,participants,content));
		} catch (IOException e) {}    	
    }
    
    public String getUsername() {
    	return username;
    }
    
}
