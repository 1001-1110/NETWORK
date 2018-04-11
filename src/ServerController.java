
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import packets.ChatRoomAction;
import packets.ChatRoomList;
import packets.ChatRoomMessage;
import packets.ChatRoomRequest;
import packets.ChatRoomUserList;
import packets.FileContent;
import packets.FileHeader;
import packets.FileList;
import packets.GroupMessage;
import packets.JankenMove;
import packets.Message;
import packets.NewGame;
import packets.OnlineList;

public class ServerController {
	
	private ServerModel sm;
	private ServerView sv;
	private ArrayList<FileSender> fileSenders;
	private ArrayList<FileReceiver> fileReceivers;
	
	private final String helpMessage = ""
			+ "## HELP ##\n"
			+ "==========\n"
			+ "To create a private chat, select a user in 'Online Users' then press 'Private Chat'\n"
			+ "To create a group chat, select multiple users in 'Online Users' by shift-clicking then press 'Private Chat'\n"
			+ "Chat commands:\n"
			+ "/help : shows this help message\n"
			+ "/clear : clears the chat\n"
			+ "/autoscroll : autoscrolls the chat\n"
			+ "==========";
	
    public ServerController(int port){
    	sv = new ServerView(port);
    	sm = new ServerModel();
    	fileSenders = new ArrayList<>();
    	fileReceivers = new ArrayList<>();
    	if(checkPort(port)) {
    		createSocket(port);
    		maintainUpdates();    		
    	}
    	updateFiles();
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
        		            updateRooms();
        		            updateFiles();
    		            }else {
    		            	output.writeObject("Username already taken. Connection rejected.");
    		            }

    		            listener.close();
    		        } catch(Exception ex) {
    		        	sv.updateChat("Server port unavailable. Server cannot be initialized.");
    		        	isRunning = false;
    		        	ex.printStackTrace();
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
    		            	}else if(o instanceof ChatRoomMessage) {
    		            		chatroomSend((ChatRoomMessage) o);
    		            	}else if(o instanceof ChatRoomAction) {
    		            		if(((ChatRoomAction) o).getAction()) {
    		            			createChatRoom((ChatRoomAction) o);
    		            		}else {
    		            			deleteChatRoom((ChatRoomAction) o);
    		            		}
    		            	}else if(o instanceof ChatRoomRequest) {
    		            		if(((ChatRoomRequest) o).getAction()) {
    		            			joinChatRoom((ChatRoomRequest) o);
    		            		}else {
    		            			unJoinChatRoom((ChatRoomRequest) o);
    		            		}
    		            	}else if(o instanceof FileHeader) {
    		            		if(((FileHeader) o).getReceiver().equals("Server")) {
    		            			if(((FileHeader) o).getRequest().equals("Upload")) {
        		            			File file = new File(System.getProperty("user.dir"));
        		            			fileReceivers.add(new FileReceiver(file, ((FileHeader) o).getSourceFile(), ((FileHeader) o).getSender(), ((FileHeader) o).getMaxBytes()));
        		            			user.getOutput().writeObject(new FileHeader(((FileHeader) o).getSourceFile(), file, "Server", ((FileHeader) o).getSender(), "Download", ((FileHeader) o).getMaxBytes(), true));
    		            			}else if(((FileHeader) o).getRequest().equals("Download")) {
    		            				File file = new File(System.getProperty("user.dir")+"\\"+((FileHeader) o).getSourceFile().getName());
    		            				for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		            					if(sm.getUser(i).getUsername().equals(((FileHeader) o).getSender())) {
    		            						FileSender fileSender = new FileSender(file, ((FileHeader) o).getSender(), sm.getUser(i).getOutput());
    		            						fileSender.setDestFile(((FileHeader) o).getDestFile());
    		            						fileSender.start();
    	    		            				fileSenders.add(fileSender);
    		            					}
    		            				}
    		            			}
    		            		}else {
        		            		fileHeaderSend((FileHeader) o);
    		            		}
    		            	}else if(o instanceof FileContent) {
    		            		if(((FileContent) o).getReceiver().equals("Server")) {
    		            			receiveFileContent((FileContent) o);
    		            		}else {
    		            			fileContentSend((FileContent) o);
    		            		}
    		            	}else if(o instanceof NewGame) {
	            				for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
	            					if(sm.getUser(i).getUsername().equals(((NewGame) o).getOpponent())) {
	            						sm.getUser(i).getOutput().writeObject(o);
	            					}
	            				}    		            		
    		            	}else if(o instanceof JankenMove) {
	            				for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
	            					if(sm.getUser(i).getUsername().equals(((NewGame) o).getOpponent())) {
	            						sm.getUser(i).getOutput().writeObject(o);
	            					}
	            				}    		            		
    		            	}
    		            }
    		        } catch(IOException ex) {
    		        	ex.printStackTrace();
    		        	try {
							user.getSocket().close();
						} catch (IOException e) {
							ex.printStackTrace();
						}
    		        	isRunning = false;
    		        } catch (ClassNotFoundException e) {
    		        	e.printStackTrace();
    		        }       				
    			}
    		}
    	};
    	
    	userThread.start();
    	
    }
    
    private void joinChatRoom(ChatRoomRequest crr) {
    	boolean valid = false;
    	String[] participants = null;
    	for(int i = 0 ; i < sm.getNumOfChatRooms() && !valid ; i++) {
    		if(sm.getRoom(i).getRoomName().equals(crr.getRoomName())) {
    			if(sm.getRoom(i).getPassword().equals(crr.getPassword())) {
    				valid = true;
    				sm.getRoom(i).addParticipant(crr.getSender());
    				participants = new String[sm.getRoom(i).getParticipants().size()];
    				for(int j = 0 ; j < sm.getRoom(i).getParticipants().size() ; j++) {
    					participants[j] = sm.getRoom(i).getParticipants().get(j);
    				}
    			}
    		}
    	} 
		for(int j = 0 ; j < sm.getNumOfUsers() ; j++) {
			if(sm.getUser(j).getUsername().equals(crr.getSender())){
				try {
					if(valid) {
						ChatRoomRequest newCrr = new ChatRoomRequest(crr.getRoomName(), crr.getPassword(), "Join", true);
						newCrr.setParticipants(participants);
						sm.getUser(j).getOutput().writeObject(newCrr);		
					}else{
						sm.getUser(j).getOutput().writeObject(new ChatRoomRequest(crr.getRoomName(), crr.getPassword(), "Join", false));	
					}
	
				} catch (IOException e) {}
			}
		}
		updateRoomUsers(crr.getRoomName());
		updateRooms();
    }

    private void unJoinChatRoom(ChatRoomRequest crr) {
    	for(int i = 0 ; i < sm.getNumOfChatRooms() ; i++) {
    		if(sm.getRoom(i).getRoomName().equals(crr.getRoomName())) {
    			sm.getRoom(i).removeParticipant(crr.getSender());
    		}
    	} 
		updateRoomUsers(crr.getRoomName());
		updateRooms();
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
		}
		updateRooms();
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
		for(int j = 0 ; j < sm.getNumOfUsers() ; j++) {
	    	if(valid) {
				try {
					sm.getUser(j).getOutput().writeObject(new ChatRoomAction(cra.getRoomName(), cra.getPassword(), "Delete", true));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
			if(sm.getUser(j).getUsername().equals(cra.getSender())){
				try {
			    	if(!valid){
						sm.getUser(j).getOutput().writeObject(new ChatRoomAction(cra.getRoomName(), cra.getPassword(), "Delete", false));
			    	}					
				} catch (IOException e) {}
			}
		} 
		updateRooms();
    }    
    
    private void updateRooms() {
		String chatRoomList[] = new String[sm.getNumOfChatRooms()];
		int numOfParticipants[] = new int[sm.getNumOfChatRooms()];
		for(int i = 0 ; i < sm.getNumOfChatRooms() ; i++) {
			chatRoomList[i] = sm.getRoom(i).getRoomName()+"\n ("+sm.getRoom(i).getNumOfParticipants()+" participants)";
		}
		sv.updateRooms(chatRoomList);
		for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
				sm.getUser(i).getOutput().writeObject(new ChatRoomList(chatRoomList, numOfParticipants));
			} catch (IOException e) {}
		}
    }  
    
    private void updateRoomUsers(String roomName) {
    	String chatRoomUserList[] = null;
		for(int i = 0 ; i < sm.getNumOfChatRooms() ; i++) {
			if(sm.getRoom(i).getRoomName().equals(roomName)) {
		    	chatRoomUserList = new String[sm.getRoom(i).getNumOfParticipants()];
		    	for(int j = 0 ; j < sm.getRoom(i).getNumOfParticipants() ; j++) {
		    		chatRoomUserList[j] = sm.getRoom(i).getParticipants().get(j);
		    	}
			}
		}
		for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
				sm.getUser(i).getOutput().writeObject(new ChatRoomUserList(roomName, chatRoomUserList));
			} catch (IOException e) {}
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
    
    private void updateFiles() {
    	File file = new File(System.getProperty("user.dir"));	
    	
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
			try {
		        sm.getUser(i).getOutput().writeObject(new FileList(file.listFiles(),System.getProperty("user.dir")));
			} catch (IOException e) {} 		
    	}   
    	
    	sv.updateFiles(new FileList(file.listFiles(),System.getProperty("user.dir")).getFiles());

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
    
    private void chatroomSend(ChatRoomMessage chatroomMessage) {
    	for(int i = 0 ; i < sm.getNumOfChatRooms() ; i++) {
    		if(sm.getRoom(i).getRoomName().equals(chatroomMessage.getRoomName())) {
    			for(int j = 0 ; j < sm.getNumOfUsers() ; j++) {
    				if(sm.getRoom(i).getParticipants().contains(sm.getUser(j).getUsername())) {
    					try {
							sm.getUser(j).getOutput().writeObject(chatroomMessage);
						} catch (IOException e) {
							e.printStackTrace();
						}
    				}

    			}
    		}
    	}
    	sv.updateChat("[ChatRoom ("+chatroomMessage.getRoomName()+")] "+chatroomMessage.getSender()+": "+chatroomMessage.getContent());
    }
    
    private void fileHeaderSend(FileHeader fileHeader) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		if(sm.getUser(i).getUsername().equals(fileHeader.getReceiver())) {
    			try {
					sm.getUser(i).getOutput().writeObject(fileHeader);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    private void fileContentSend(FileContent fileContent) {
    	for(int i = 0 ; i < sm.getNumOfUsers() ; i++) {
    		if(sm.getUser(i).getUsername().equals(fileContent.getReceiver())) {
    			try {
					sm.getUser(i).getOutput().writeObject(fileContent);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }

    private void receiveFileContent(FileContent fileContent) {
    	for(int i = 0 ; i < fileReceivers.size() ; i++) {
    		if(fileReceivers.get(i).getSender().equals(fileContent.getSender()) && 
    				fileReceivers.get(i).getSourceFile().equals(fileContent.getSourceFile()) && 
    	    				fileReceivers.get(i).getDestFile().equals(fileContent.getDestFile())) {
    			if(fileContent.getContent().length == 0) {
    				fileReceivers.get(i).finishWrite();
    			}else {
        			fileReceivers.get(i).writeBytes(fileContent.getContent());
    			}
    		}
    	}
    }    
    
    class FileReceiver{
    	
    	private FileOutputStream fos;
    	private String sender;
    	private File sourceFile;
    	private File destFile;
    	
    	public FileReceiver(File destFile, File sourceFile, String sender, int maxBytes) {
    		try {
				fos = new FileOutputStream(destFile.getAbsolutePath()+"\\"+sourceFile.getName());
				this.sender = sender;
				this.sourceFile = sourceFile;
				this.destFile = destFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	
    	public String getSender() {
			return sender;
		}

		public File getSourceFile() {
			return sourceFile;
		}

		public File getDestFile() {
			return destFile;
		}
		
		public void writeBytes(byte content[]) {
    		try {
				fos.write(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	public void finishWrite() {
    		try {
				fos.close();
				updateFiles();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
    
    class FileSender extends Thread{
    	
    	private FileInputStream fis;
    	private String receiver;
    	private File sourceFile;
    	private File destFile;
    	private ObjectOutputStream output;
    	
    	public FileSender(File sourceFile, String receiver, ObjectOutputStream output) {
    		this.sourceFile = sourceFile;
    		this.receiver = receiver; 
    		this.output = output;
    	}
    	
		public String getReceiver() {
			return receiver;
		}

		public File getSourceFile() {
			return sourceFile;
		}
		
		public void setDestFile(File destFile) {
			this.destFile = destFile;
		}

		public File getDestFile() {
			return destFile;
		}
		
		public void run() {
    		try {
				fis = new FileInputStream(sourceFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}			
    		try {
    			int progress = 0;
				while(fis.available() > 0) {
					int buffer;
					if(fis.available() <= 1024) {
						buffer = fis.available();
					}else {
						buffer = 1024;
					}
					progress += buffer;
					byte content[] = new byte[buffer];
					fis.read(content);
					output.writeObject(new FileContent(sourceFile, destFile, "Server", receiver, progress, content));
				}
				byte content[] = new byte[0];
				output.writeObject(new FileContent(sourceFile, destFile, "Server", receiver, progress, content));
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    }
    
}
