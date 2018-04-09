
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import packets.ChatRoomAction;
import packets.ChatRoomList;
import packets.GroupMessage;
import packets.Message;
import packets.OnlineList;

public class ServerController {
	
	private ServerModel sm;
	private ServerView sv;
	
	private final String helpMessage = ""
			+ "## HELP ##\n"
			+ "==========\n"
			+ "To create a private chat, select a user in 'Online Users' then press 'Private Chat'\n"
			+ "To create a group chat, select multiple users in 'Online Users' by shift-clicking then press 'Private Chat'\n"
			+ "Chat commands:\n"
			+ "/help : shows this help message\n"
			+ "/clear : clears the chat\n"
			+ "==========";
	
    public ServerController(int port){
    	sv = new ServerView(port);
    	sm = new ServerModel();
    	if(checkPort(port)) {
    		createSocket(port);
    		maintainUpdates();    		
    	}
    }
    
    private boolean checkPort(int port) {
    	if(port > 65535 || port < 0) {
    		sv.updateChat("Server port unavailable. Server cannot be initialized.");
    		return false;
    	}
    	return true;
    }
    
    private void maintainUpdates() {
    	
    	Thread maintainUpdate = new Thread() {
    		public void run() {
    			while(true) {
    				
    				boolean hasDisconnected = false;
    				for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    					if(sm.getUser(i).getSocket().isClosed()) {
    						broadcast(new Message("Server","all",sm.getUser(i).getUsername()+" has disconnected."));
    						sm.removeUser(i);
    						hasDisconnected = true;

    					}
    				}
    				
    				if(hasDisconnected)
        	    		updateUsers();  
    				
        	    	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
    			}
    		}
    	};
    	
    	maintainUpdate.start();
    	
    }
    
    private boolean checkUsernames(String username) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			if(sm.getUser(i).getUsername().equals(username)) {
				return false;
			}
    	}    	
    	return true;
    }
    
    private void createSocket(int port) {
    	
    	Thread socketCreate = new Thread() {
    		public void run() {
    			boolean isRunning = true;
    			while(isRunning) {
    		        try {
    		            ServerSocket listener = new ServerSocket(port);
    		            Socket socket = listener.accept();
    		            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
    		            Object username;
    		            while((username = input.readObject()) == null);
    		            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
    		            if(checkUsernames((String) username)) {
        		            output.writeObject("Connected to server at port "+socket.getPort()+".\n"
        		            		+ helpMessage);
        		            sv.updateChat(username+" ("+socket.getInetAddress() + ":" +socket.getPort()+")" + " has connected to server.");
        		            announce(username+" has connected to server.");
        		            User user = new User((String) username, socket, input, output);
        		            sm.addUser(user);
        		            userStartThread(user);
        		            updateUsers();    		            	
    		            }else {
    		            	output.writeObject("Username already taken. Connection rejected.");
    		            }

    		            listener.close();
    		        } catch(Exception ex) {
    		        	sv.updateChat("Server port unavailable. Server cannot be initialized.");
    		        	isRunning = false;
    		        }
    			}
    		}
    	}; 
    	
    	socketCreate.start();    
    	
    }
    
    private void userStartThread(User user) {
    	
    	Thread userThread = new Thread() {
    		public void run() {
    			boolean isRunning = true;
    			while(isRunning) {
    		        try {
    		            Object o;
    		            boolean isCommand;
    		            if((o = user.getInput().readObject()) != null) {
    		            	if(o instanceof Message) {
    		            		isCommand = false;
        		            		if(((Message) o).getContent().charAt(0) == '/') {
        		            			isCommand = true;
        		            			if(((Message) o).getContent().length() >= 5) {
            		            			if(((Message) o).getContent().substring(1,5).equals("help")) {
            		            				announce(helpMessage,((Message) o).getSender());
            		            			}else {
            		            				announce("Invalid command",((Message) o).getSender());
            		            			}
        		            			}else {
        		            				announce("Invalid command",((Message) o).getSender());
        		            			}

        		            		}
    		            		
    		            			if(!isCommand) {
            		            		if(((Message) o).getReceiver().equals("all")) {
            		            			broadcast((Message) o);
            		            		}else {
            		            			specificSend((Message) o);
            		            		}  
    		            			}
    		            	}else if(o instanceof GroupMessage) {
    		            		groupSend((GroupMessage) o);
    		            	}else if(o instanceof ChatRoomAction) {
    		            		if(((ChatRoomAction) o).getAction()) {
    		            			createChatRoom((ChatRoomAction) o);
    		            		}else {
    		            			deleteChatRoom((ChatRoomAction) o);
    		            		}
    		            	}
    		            }
    		        } catch(IOException ex) {
    		        	ex.printStackTrace();
    		        	try {
							user.getSocket().close();
						} catch (IOException e) {}
    		        	isRunning = false;
    		        } catch (ClassNotFoundException e) {
    		        	e.printStackTrace();
    		        }       				
    			}
    		}
    	};
    	
    	userThread.start();
    	
    }
    
    private void createChatRoom(ChatRoomAction cra) {
    	boolean valid = true;
    	for(int i = 0 ; i < sm.getNumOfChatRooms() && valid ; i++) {
    		if(sm.getRoom(i).getRoomName().equals(cra.getRoomName())) {
    			valid = false;
    		}
    	}
    	if(valid) 
		sm.addRoom(new ChatRoom(cra.getRoomName(), cra.getPassword()));
		String chatRoomList[] = new String[sm.getNumOfChatRooms()];
		int numOfParticipants[] = new int[sm.getNumOfChatRooms()];
    	if(valid) {
    		for(int i = 0 ; i < sm.getNumOfChatRooms() ; i++) {
    			chatRoomList[i] = sm.getRoom(i).getRoomName()+"\n ("+sm.getRoom(i).getNumOfParticipants()+" participants)";
    		}
    		sv.updateRooms(chatRoomList);
    	}
		for(int j = 0 ; j < sm.getNumOfUsers() ; j++) {
			if(sm.getUser(j).getUsername().equals(cra.getSender())){
				try {
			    	if(valid) {
						sm.getUser(j).getOutput().writeObject(new ChatRoomAction(cra.getRoomName(), cra.getPassword(), "Create", true));
			    	}else {
						sm.getUser(j).getOutput().writeObject(new ChatRoomAction(cra.getRoomName(), cra.getPassword(), "Create", false));
			    	}					
				} catch (IOException e) {}
			}
			if(valid) {
				try {
					sm.getUser(j).getOutput().writeObject(new ChatRoomList(chatRoomList,numOfParticipants));
				} catch (IOException e) {}
			}
		}
    }

    private void deleteChatRoom(ChatRoomAction cra) {
    	boolean valid = false;
    	for(int i = 0 ; i < sm.getNumOfChatRooms() && !valid ; i++) {
    		System.out.println(sm.getRoom(i).getRoomName() + " = "+cra.getRoomName());
    		if(sm.getRoom(i).getRoomName().equals(cra.getRoomName()) && sm.getRoom(i).getPassword().equals(cra.getPassword())) {
	    		sm.removeRoom(i);
    			valid = true;
    		}
    	}
		String chatRoomList[] = new String[sm.getNumOfChatRooms()];
		int numOfParticipants[] = new int[sm.getNumOfChatRooms()];
    	if(valid) {
    		sv.updateRooms(chatRoomList);
    		for(int i = 0 ; i < sm.getNumOfChatRooms() ; i++) {
    			chatRoomList[i] = sm.getRoom(i).getRoomName();
    		}
    	}
		for(int j = 0 ; j < sm.getNumOfUsers() ; j++) {
			if(sm.getUser(j).getUsername().equals(cra.getSender())){
				try {
			    	if(valid) {
						sm.getUser(j).getOutput().writeObject(new ChatRoomAction(cra.getRoomName(), cra.getPassword(), "Delete", true));
			    	}else {
						sm.getUser(j).getOutput().writeObject(new ChatRoomAction(cra.getRoomName(), cra.getPassword(), "Delete", false));
			    	}					
				} catch (IOException e) {}
			}
			if(valid) {
				try {
					sm.getUser(j).getOutput().writeObject(new ChatRoomList(chatRoomList, numOfParticipants));
				} catch (IOException e) {}
			}
		}    	
    }    
    
    private void updateUsers() {
    	String[] onlineUsers = new String[sm.getNumOfUsers()];
    	
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		onlineUsers[i] = sm.getUser(i).getUsername();
    	}    	
    	
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
				ObjectOutputStream output = sm.getUser(i).getOutput();
				OnlineList users = new OnlineList(onlineUsers);
		        output.writeObject(users);
			} catch (IOException e) {} 		
    	}   
    	
    	sv.updateOnline(onlineUsers);
    	
    }

    private void announce(String announcement) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
				ObjectOutputStream output = sm.getUser(i).getOutput();
		        output.writeObject(new Message("Server","all",announcement));
			} catch (IOException e) {} 		
    	}
    }    

    private void announce(String announcement, String specificUser) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		if(sm.getUser(i).getUsername().equals(specificUser)) {
    			try {
    				ObjectOutputStream output = sm.getUser(i).getOutput();
    				output.writeObject(new Message("Server","all",announcement));   
    			} catch (IOException e) {} 	    			
    		}
    	}
    }     
    
    private void broadcast(Message message) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
				ObjectOutputStream output = sm.getUser(i).getOutput();
		        output.writeObject(message);
			} catch (IOException e) {} 		
    	}
        sv.updateChat("[Global Chat] "+message.getSender()+": "+message.getContent());
    }
    
    private void specificSend(Message message) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		if(sm.getUser(i).getUsername().equals(message.getReceiver())) {
                ObjectOutputStream output;
    			try {
    				output = sm.getUser(i).getOutput();
    		        output.writeObject(message);   
    		        sv.updateChat("["+message.getSender()+" -> "+message.getReceiver()+"]"+": "+message.getContent());
    			} catch (IOException e) {} 	    			
    		}
    	}   	
    }

    private void groupSend(GroupMessage groupMessage) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		if(groupMessage.getParticipants().contains(sm.getUser(i).getUsername())) {
                ObjectOutputStream output;
    			try {
    				output = sm.getUser(i).getOutput();
    		        output.writeObject(groupMessage);   
    			} catch (IOException e) {} 	    			
    		}
    	}
        sv.updateChat("[Group Chat] "+groupMessage.getSender()+": "+groupMessage.getContent());
    }   
    
}
