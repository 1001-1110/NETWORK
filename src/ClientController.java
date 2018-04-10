
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import packets.ChatRoomAction;
import packets.ChatRoomList;
import packets.ChatRoomMessage;
import packets.ChatRoomRequest;
import packets.ChatRoomUserList;
import packets.GroupMessage;
import packets.Message;
import packets.OnlineList;

public class ClientController {
	
	private ClientView cv;
	private String username;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private ArrayList<PrivateChatView> privateChats;
	private ArrayList<GroupChatView> groupChats;
	private ArrayList<RoomChatView> roomChats;
	
    public ClientController(String serverIP, int port, String username){
    	cv = new ClientView(this,username);
    	this.username = username;
    	privateChats = new ArrayList<>();
    	groupChats = new ArrayList<>();
    	roomChats = new ArrayList<>();
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
    
    private void createRoomChat(String roomName, String[] participants) {
    	RoomChatView rcv = new RoomChatView(this,this.username, participants, roomName);
    	rcv.addWindowListener(new ChatRoomListener(roomName));
    	roomChats.add(rcv);
    }
    
    private void deleteRoomChat(String roomName) {
    	for(int i = 0 ; i < roomChats.size() ; i++) {
    		if(roomChats.get(i).getRoomName().equals(roomName)) {
    			roomChats.get(i).dispose();
    			roomChats.remove(i);
    		}
    	}
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
    
    private void receiveChatRoom(ChatRoomMessage chatroomMessage) {
    	for(int i = 0 ; i < roomChats.size() ; i++) {
    		if(roomChats.get(i).getRoomName().equals(chatroomMessage.getRoomName())) {
    			roomChats.get(i).updateChat(chatroomMessage.getSender()+": "+chatroomMessage.getContent());
    		}
    	}
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
    		            	}else if(o instanceof GroupMessage) {
    		            		receiveGroupChat((GroupMessage) o);
    		            	}else if(o instanceof ChatRoomMessage) {
    		            		receiveChatRoom((ChatRoomMessage) o);
    		            	}else if(o instanceof ChatRoomAction) {
    		            		if(((ChatRoomAction) o).getSender().equals("Create")) {
    		            			if(((ChatRoomAction) o).getAction())
    		            				joinChatRoom(((ChatRoomAction) o).getRoomName(), ((ChatRoomAction) o).getPassword());
    		            			else if(!((ChatRoomAction) o).getAction())
    		            				cv.showErrorNotif(((ChatRoomAction) o).getRoomName()+" already exists.", "Existing room");
    		            		}else if(((ChatRoomAction) o).getSender().equals("Delete")) {
    		            			if(((ChatRoomAction) o).getAction())
    		            				deleteRoomChat(((ChatRoomAction) o).getRoomName());
    		            			else if(!((ChatRoomAction) o).getAction())
    		            				cv.showErrorNotif("Invalid password", "Invalid");   		            			
    		            		}
    		            	}else if(o instanceof ChatRoomRequest) {
    		            		if(((ChatRoomRequest) o).getSender().equals("Join")) {
    		            			if(((ChatRoomRequest) o).getAction()) {
    		            				createRoomChat(((ChatRoomRequest) o).getRoomName(),((ChatRoomRequest) o).getParticipants());    		            				
    		            			}else if(!((ChatRoomRequest) o).getAction()) {
    		            				cv.showErrorNotif("Invalid password", "Invalid"); 
    		            			}  		            			
    		            		}
    		            	}else if(o instanceof OnlineList) {
    		            		cv.updateOnline(((OnlineList) o).getUsers());
    		            	}else if(o instanceof ChatRoomList) {
    		            		cv.updateRooms(((ChatRoomList) o).getChatRoomList());
    		            	}else if(o instanceof ChatRoomUserList) {
    		            		updateRoomChats((ChatRoomUserList) o);
    		            	}
    		            }
    		        } catch(IOException ex) {
    		        	cv.updateChat("Lost connection with server.");
    		        	ex.printStackTrace();
    		        	isRunning = false;
    		        } catch (ClassNotFoundException e) {
    		        	e.printStackTrace();
    		        }         				
    			}
    		}
    	};  
    	
        socketConnect.start();
    	
    }
    
    private void updateRoomChats(ChatRoomUserList crul) {
    	for(int i = 0 ; i < roomChats.size() ; i++) {
    		if(roomChats.get(i).getRoomName().equals(crul.getRoomName())) {
    			roomChats.get(i).updateParticipants(crul.getParticipants());
    		}
    	}
    }
    
    public void joinChatRoom(String roomName, String password) {
    	if(roomName != null) {
    		boolean valid = true;
    		for(int i = 0 ; i < roomChats.size() && valid ; i++) {
    			if(roomChats.get(i).getRoomName().equals(roomName)) {
    				valid = false;
    			}
    		}
    		if(valid) {
        	    try {
        			output.writeObject(new ChatRoomRequest(roomName, password, username, true));
        		} catch (IOException e) {
        			e.printStackTrace();
        		}      			
    		}else {
    			cv.showErrorNotif("Already joined chatroom.","Invalid");
    		}
     		
    	}
    }

    public void unJoinChatRoom(String roomName, String password) {
    	if(roomName != null) {
    	    try {
    			output.writeObject(new ChatRoomRequest(roomName, password, username, false));
    			for(int i = 0 ; i < roomChats.size() ; i++) {
    				if(roomChats.get(i).getRoomName().equals(roomName)) {
    					roomChats.remove(i);
    				}
    			}
    		} catch (IOException e) {}       		
    	}
    }    
    
    public void sendMessage(String content, String receiver) {
    	if(content != null) {
    	    try {
    			output.writeObject(new Message(username,receiver,content));
    		} catch (IOException e) {}	
    	}
    }
    
    public void sendGroupMessage(String content, ArrayList<String> participants) {
    	if(content != null) {
    	    try {
    			output.writeObject(new GroupMessage(username,participants,content));
    		} catch (IOException e) {}       		
    	}
    }

    public void sendChatRoomMessage(String content, String roomName) {
    	if(content != null) {
    	    try {
    			output.writeObject(new ChatRoomMessage(content, username, roomName));
    		} catch (IOException e) {}       		
    	}
    }    
    
    public void sendChatRoomCreation(String roomName, String password) {
    	if(roomName != null) {
    	    try {
    			output.writeObject(new ChatRoomAction(roomName, password, username, true));
    		} catch (IOException e) {}       		
    	}
    }

    public void sendChatRoomDeletion(String roomName, String password) {
    	if(roomName != null) {
    	    try {
    			output.writeObject(new ChatRoomAction(roomName, password, username, false));
    		} catch (IOException e) {}       		
    	}    	
    }    
    
    public String getUsername() {
    	return username;
    }
    
    class ChatRoomListener implements WindowListener{

    	private String roomName;
    	
    	public ChatRoomListener(String roomName) {
    		this.roomName = roomName;
    	}
		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			unJoinChatRoom(roomName,"");
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    
}
