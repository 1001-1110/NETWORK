
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JFileChooser;

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

public class ClientController {
	
	private ClientView cv;
	private String username;
	private String serverDir;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private ArrayList<PrivateChatView> privateChats;
	private ArrayList<GroupChatView> groupChats;
	private ArrayList<RoomChatView> roomChats;
	private ArrayList<FileSender> fileSenders;
	private ArrayList<FileReceiver> fileReceivers;
	private ArrayList<GameView> gameViews;
	
    public ClientController(String serverIP, int port, String username){
    	cv = new ClientView(this,username);
    	this.username = username;
    	privateChats = new ArrayList<>();
    	groupChats = new ArrayList<>();
    	roomChats = new ArrayList<>();
    	gameViews = new ArrayList<>();
    	fileSenders = new ArrayList<>();
    	fileReceivers = new ArrayList<>();
    	connectSocket(serverIP,port,username);
    }
    
    public String getUsername() {
    	return username;
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
    
	public File showFileChooser(int selectionMode) {
		return cv.showFileChooser(selectionMode);
	}
    
	public void showErrorNotif(String notification, String header) {
		cv.showErrorNotif(notification, header);
	}
	
	public void showNotif(String notification) {
		cv.showNotif(notification);
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
    		            	}else if(o instanceof FileHeader) {
    		            		if(((FileHeader) o).getRequest().equals("Upload")) {
    		            			receiveFileUploadRequest((FileHeader) o);
    		            		}else if(((FileHeader) o).getRequest().equals("Download")) {
    		            			if(((FileHeader) o).isAccepted()) {
    		            				receiveFileDownloadRequest((FileHeader) o);
    		            			}else {
    		            				receiveRejectedFileDownloadRequest((FileHeader) o);
    		            			}
    		            		}
    		            	}else if(o instanceof FileContent) {
    		            		receiveFileContent((FileContent) o);
    		            	}else if(o instanceof OnlineList) {
    		            		cv.updateOnline(((OnlineList) o).getUsers());
    		            	}else if(o instanceof ChatRoomList) {
    		            		cv.updateRooms(((ChatRoomList) o).getChatRoomList());
    		            	}else if(o instanceof ChatRoomUserList) {
    		            		updateRoomChats((ChatRoomUserList) o);
    		            	}else if(o instanceof FileList) {
    		            		updateFiles((FileList) o);
    		            	}else if(o instanceof NewGame) {
    		            		receiveGame(((NewGame) o).getPlayer());
    		            	}else if(o instanceof JankenMove) {
    		            		receiveMove((JankenMove) o);
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
    
    private void updateFiles(FileList fileList) {
    	cv.updateFiles(fileList.getFiles(),fileList.getFileSizes());
    	this.serverDir = fileList.getServerDir();
    }
    
    public void createGame(String opponent) {
    	gameViews.add(new GameView(opponent, this));
    	try {
			output.writeObject(new NewGame(username, opponent));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void receiveGame(String opponent) {
    	gameViews.add(new GameView(opponent, this));
    }
    
    public void receiveMove(JankenMove move) {
    	for(int i = 0 ; i < gameViews.size() ; i++) {
    		if(gameViews.get(i).getOpponent().equals(move.getPlayer())) {
    			gameViews.get(i).setOpponent(move.getMove());
    		}
    	}
    }
    
    public void sendMove(String move, String opponent) {
    	try {
			output.writeObject(new JankenMove(move, username, opponent));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    public void sendFileUploadRequest(File sourceFile, String receiver) {
    	if(sourceFile != null) {
    	    try {
    			output.writeObject(new FileHeader(sourceFile, null, username, receiver, "Upload",(int) Files.size(sourceFile.toPath()), false));
    			fileSenders.add(new FileSender(sourceFile, receiver));
    		} catch (IOException e) {}       		
    	}     	
    }

    public void sendFileDownloadRequest(File destFile, String fileName, int fileSize) {
    	if(destFile != null) {
    	    try {
    			output.writeObject(new FileHeader(new File(serverDir+"\\"+fileName), destFile, username, "Server", "Download", 0, true));
    			fileReceivers.add(new FileReceiver(destFile, new File(serverDir+"\\"+fileName), "Server", fileSize));
    		} catch (IOException e) {}       		
    	}     	
    }    
    
    private void receiveFileUploadRequest(FileHeader fileHeader) {
    	if(fileHeader.getSourceFile() != null) {
    		if(cv.showConfirmNotif("Receive "+fileHeader.getSourceFile().getName()+" from "+fileHeader.getSender()+"?")) {
        		File file;
        		if((file = cv.showFileChooser(JFileChooser.DIRECTORIES_ONLY)) != null) {
            	    try {
            			output.writeObject(new FileHeader(fileHeader.getSourceFile(), file, username, fileHeader.getSender(), "Download", fileHeader.getMaxBytes(), true));
            			fileReceivers.add(new FileReceiver(file, fileHeader.getSourceFile(), fileHeader.getSender(), fileHeader.getMaxBytes()));
            		} catch (IOException e) {}      			
        		}    			
    		}else {
        	    try {
        			output.writeObject(new FileHeader(fileHeader.getSourceFile(), null, username, fileHeader.getSender(), "Download", fileHeader.getMaxBytes(), false));
        		} catch (IOException e) {}     			
    		}
    	}     	
    } 
    
    private void receiveFileDownloadRequest(FileHeader fileHeader) {
    	for(int i = 0 ; i < fileSenders.size() ; i++) {
    		if(fileSenders.get(i).getReceiver().equals(fileHeader.getSender()) && 
    				fileSenders.get(i).getSourceFile().equals(fileHeader.getSourceFile())) {
    			fileSenders.get(i).setDestFile(fileHeader.getDestFile());
    			fileSenders.get(i).start();
    		}
    	}    	
    }
    
    private void receiveRejectedFileDownloadRequest(FileHeader fileHeader) {
    	for(int i = 0 ; i < fileSenders.size() ; i++) {
    		if(fileSenders.get(i).getReceiver().equals(fileHeader.getReceiver()) && 
    				fileSenders.get(i).getSourceFile().equals(fileHeader.getSourceFile())) {
    			fileSenders.get(i).close();
    			fileSenders.remove(i);
    		}
    	}
    }
    
    private void receiveFileContent(FileContent fileContent) {
    	for(int i = 0 ; i < fileReceivers.size() ; i++) {
    		if(fileReceivers.get(i).getSender().equals(fileContent.getSender()) && 
    				fileReceivers.get(i).getSourceFile().equals(fileContent.getSourceFile()) && 
    	    				fileReceivers.get(i).getDestFile().equals(fileContent.getDestFile())) {
    			if(fileContent.getContent().length == 0) {
    				fileReceivers.get(i).updateProgress(fileContent.getProgress());
    				fileReceivers.get(i).finishWrite();
    			}else {
    				fileReceivers.get(i).updateProgress(fileContent.getProgress());
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
    	private FileTransferView fileTransfer;
    	
    	public FileReceiver(File destFile, File sourceFile, String sender, int maxBytes) {
    		try {
				fos = new FileOutputStream(destFile.getAbsolutePath()+"\\"+sourceFile.getName());
				this.sender = sender;
				this.sourceFile = sourceFile;
				this.destFile = destFile;
				fileTransfer = new FileTransferView(cv.getX(),cv.getY());
	    		fileTransfer.setState("Receiving "+sourceFile.getName()+" from: "+sender);
	    		fileTransfer.setAmount("Received: 0 bytes");   
	    		fileTransfer.setMaxProgress(maxBytes);
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
		
		public void updateProgress(int progress) {
			fileTransfer.setAmount("Received: "+progress+" bytes");
			fileTransfer.setProgress(progress);
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
				fileTransfer.dispose();
				fileReceivers.remove(this);
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
    	private FileTransferView fileTransfer;
    	
    	public FileSender(File sourceFile, String receiver) {
    		this.sourceFile = sourceFile;
    		this.receiver = receiver;
    		fileTransfer = new FileTransferView(cv.getX(),cv.getY());
    		fileTransfer.setState("Waiting response from: "+receiver);
    		fileTransfer.setAmount("Sent: 0 bytes");   
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
    	
		public void close() {
			fileTransfer.dispose();
		}
		
		public void run() {
    		try {
				fis = new FileInputStream(sourceFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}			
    		try {
    			int progress = 0;
        		fileTransfer.setMaxProgress(fis.available());
        		fileTransfer.setState("Sending "+sourceFile.getName()+" to: "+receiver); 
				while(fis.available() > 0) {
					int buffer;
					if(fis.available() <= 1024) {
						buffer = fis.available();
					}else {
						buffer = 1024;
					}
					progress += buffer;
					fileTransfer.setProgress(progress);
	        		fileTransfer.setAmount("Sent: "+progress+" bytes");  
					byte content[] = new byte[buffer];
					fis.read(content);
					output.writeObject(new FileContent(sourceFile, destFile, username, receiver, progress, content));
				}
				byte content[] = new byte[0];
				output.writeObject(new FileContent(sourceFile, destFile, username, receiver, progress, content));
				fis.close();
				close();
				fileSenders.remove(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
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
