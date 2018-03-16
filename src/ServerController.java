
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {
	
	private ServerModel sm;
	private ServerView sv;
	
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
        		            output.writeObject("Connected to server at port "+socket.getPort()+".");
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
    		            if((o = user.getInput().readObject()) != null) {
    		            	if(o instanceof Message) {
    		            		if(((Message) o).getReceiver().equals("all")) {
    		            			broadcast((Message) o);
    		            		}else {
    		            			specificSend((Message) o);
    		            		}
    		            	}else if(o instanceof GroupMessage) {
    		            		groupSend((GroupMessage) o);
    		            	}
    		            }
    		        } catch(Exception ex) {
    		        	try {
							user.getSocket().close();
						} catch (IOException e) {}
    		        	isRunning = false;
    		        }       				
    			}
    		}
    	};
    	
    	userThread.start();
    	
    }
    
    private void updateUsers() {
    	String[] onlineUsers = new String[sm.getNumOfUsers()];
    	
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		onlineUsers[i] = sm.getUser(i).getUsername();
    	}    	
    	
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
				ObjectOutputStream output = sm.getUser(i).getOutput();
		        output.writeObject(onlineUsers);
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
    
    private void broadcast(Message message) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
				ObjectOutputStream output = sm.getUser(i).getOutput();
		        output.writeObject(message);
			} catch (IOException e) {} 		
    	}
        sv.updateChat(message.getSender()+": "+message.getContent());
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
        sv.updateChat("[Group Chat]"+" "+groupMessage.getSender()+": "+groupMessage.getContent());
    }   
    
}
