
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.UIManager;

import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class RoomChatView extends JFrame{

	private ClientController cc;
	
	private String owner;
	private String roomName;
	
	private JTextField input;
	private JTextArea chatBox;
	private JList<String> userList;
	
	public RoomChatView(ClientController cc, String owner, String[] participants, String roomName) {
		super("Chatroom: "+owner+" -> "+roomName);
		this.cc = cc;
		this.owner = owner;
		this.roomName = roomName;
		initialize();
		this.userList.setListData(participants);
		this.setVisible(true);
	}
	
	public String getRoomName() {
		return roomName;
	}

	public void updateChat(String update) {
		chatBox.append("\n"+update);
	}

	public void updateParticipants(String[] participants) {
		userList.setListData(participants);
	}	

	private void sendMessage(String message) {
		if(message.equals("/clear")) {
			chatBox.setText("");
		}else if(message.equals("/autoscroll")) {
			autoScroll();
		}else if(!message.equals("")) {
			cc.sendChatRoomMessage(message, roomName);
		}
	}		
	
	private void autoScroll() {
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	private void initialize() {
		setBounds(100, 100, 450, 330);
		
		try {
			setDefaultLookAndFeelDecorated(isDefaultLookAndFeelDecorated());			
		}catch(Exception e) {}

		JScrollPane chatScroll = new JScrollPane();
		
		JScrollPane userScroll = new JScrollPane();
		
		JPanel userButtonPanel = new JPanel();
		
		JPanel chatInputPanel = new JPanel();

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(chatInputPanel, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
							.addGap(7)))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(userButtonPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
						.addComponent(userScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
					.addGap(12))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(userScroll, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(13)
							.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(userButtonPanel, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
						.addComponent(chatInputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		input = new JTextField();
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					sendMessage(input.getText());
					input.setText(null);
				}
			}
		});
		input.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(input.getText());
				input.setText(null);
			}
		});
		GroupLayout gl_chatInputPanel = new GroupLayout(chatInputPanel);
		gl_chatInputPanel.setHorizontalGroup(
			gl_chatInputPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_chatInputPanel.createSequentialGroup()
					.addComponent(input, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSend)
					.addGap(6))
		);
		gl_chatInputPanel.setVerticalGroup(
			gl_chatInputPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_chatInputPanel.createSequentialGroup()
					.addGroup(gl_chatInputPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSend)
						.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(26, Short.MAX_VALUE))
		);
		chatInputPanel.setLayout(gl_chatInputPanel);
		userButtonPanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		JButton btnSendFile = new JButton("Send File");
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(userList.getSelectedValue() != null) {
					File file;
					if((file = cc.showFileChooser(JFileChooser.FILES_ONLY)) != null) {
						if(file.exists()) {
							cc.sendFileUploadRequest(file, userList.getSelectedValue());
						}else {
							cc.showErrorNotif("File does not exist","Invalid");
						}
					}
				}else {
					cc.showNotif("Select an online user.");	
				}				
			}
		});
		userButtonPanel.add(btnSendFile);
		
		JButton btnStartGame = new JButton("Start Game");
		userButtonPanel.add(btnStartGame);
		
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatBox);
		
		userList = new JList<String>();
		userList.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Participants", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		userScroll.setViewportView(userList);
		getContentPane().setLayout(groupLayout);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}
}
