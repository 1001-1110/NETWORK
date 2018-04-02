
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import java.awt.event.ActionListener;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.GridLayout;

public class ClientView extends JFrame{

	private ClientController cc;
	
	private JTextField input;
	private JList<String> userList;
	private JList<String> roomList;
	private JList<String> fileList;
	private JTextArea chatBox;
	
	public ClientView(ClientController cc, String owner) {
		super("Client ("+owner+")");
		this.cc = cc;
		initialize();
		this.setVisible(true);
	}
	
	public void updateChat(String update) {
		chatBox.append("\n"+update);
	}
	
	public void updateOnline(String[] users) {
		userList.setListData(users);
	}
	
	private void initialize() {
		setBounds(100, 100, 860, 330);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			setDefaultLookAndFeelDecorated(isDefaultLookAndFeelDecorated());			
		}catch(Exception e) {}
		
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new TitledBorder(null, "Global Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel userPanel = new JPanel();
		userPanel.setBorder(new TitledBorder(null, "Online Users", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel roomPanel = new JPanel();
		roomPanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Chatrooms", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));

		/*
		  		JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				File file = fc.getSelectedFile();
				System.out.println(file.getName());
		*/
		
		JPanel roomButtonPanel = new JPanel();
		
		JPanel userButtonPanel = new JPanel();
		
		JPanel chatInputPanel = new JPanel();
		
		JPanel filePanel = new JPanel();
		filePanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Files", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		
		JScrollPane fileScroll = new JScrollPane();
		GroupLayout gl_filePanel = new GroupLayout(filePanel);
		gl_filePanel.setHorizontalGroup(
			gl_filePanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 132, Short.MAX_VALUE)
				.addComponent(fileScroll, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
		);
		gl_filePanel.setVerticalGroup(
			gl_filePanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 199, Short.MAX_VALUE)
				.addComponent(fileScroll, GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
		);
		filePanel.setLayout(gl_filePanel);
		
		fileList = new JList<>();
		fileScroll.setViewportView(fileList);
		
		JPanel fileButtonPanel = new JPanel();
		fileButtonPanel.setLayout(new GridLayout(0, 1, 5, 5));
		
		JButton btnDownload = new JButton("Download");
		fileButtonPanel.add(btnDownload);
		
		JButton btnUpload = new JButton("Upload");
		fileButtonPanel.add(btnUpload);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
						.addComponent(chatInputPanel, GroupLayout.PREFERRED_SIZE, 337, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(roomPanel, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
						.addComponent(roomButtonPanel, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(userPanel, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
						.addComponent(userButtonPanel, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(filePanel, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
						.addComponent(fileButtonPanel, GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
						.addComponent(userPanel, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
						.addComponent(filePanel, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
						.addComponent(roomPanel, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(userButtonPanel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
						.addComponent(roomButtonPanel, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
						.addComponent(chatInputPanel, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(fileButtonPanel, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
					.addGap(10))
		);
		
		input = new JTextField();
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					if(input.getText().equals("/clear")) {
						chatBox.setText("");
					}else {
						cc.sendMessage(input.getText(),"all");					
					}
					input.setText(null);	
				}
			}
		});
		input.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		GroupLayout gl_chatInputPanel = new GroupLayout(chatInputPanel);
		gl_chatInputPanel.setHorizontalGroup(
			gl_chatInputPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_chatInputPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(input, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
					.addGap(9))
		);
		gl_chatInputPanel.setVerticalGroup(
			gl_chatInputPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_chatInputPanel.createSequentialGroup()
					.addGroup(gl_chatInputPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(input, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		chatInputPanel.setLayout(gl_chatInputPanel);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(input.getText().equals("/clear")) {
					chatBox.setText("");
				}else {
					cc.sendMessage(input.getText(),"all");					
				}
				input.setText(null);
			}
		});
		userButtonPanel.setLayout(new GridLayout(3, 1, 5, 5));
		
		JButton btnChat = new JButton("Private Chat");
		userButtonPanel.add(btnChat);
		btnChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.util.List<String> users = userList.getSelectedValuesList();
				ArrayList<String> participants = new ArrayList<>();
				for(int i = 0 ; i < users.size() ; i++) {
					if(!users.get(i).equals(cc.getUsername()))
						participants.add(users.get(i));
				}
				
				if(participants.size() > 0) {
					if(participants.size() > 1) {
						participants.add(cc.getUsername());
						cc.createGroupChat(participants);
					}else {
						cc.createPrivateChat(participants.get(0));
					}
				}
				
				userList.clearSelection();
			}
		});
		
		JButton btnSendFile = new JButton("Send File");
		userButtonPanel.add(btnSendFile);
		
		JButton btnStartGame = new JButton("Start Game");
		userButtonPanel.add(btnStartGame);
		roomButtonPanel.setLayout(new GridLayout(0, 1, 5, 5));
		
		JButton btnCreate = new JButton("Create");
		roomButtonPanel.add(btnCreate);
		
		JButton btnJoin = new JButton("Join");
		roomButtonPanel.add(btnJoin);
		
		JScrollPane roomScroll = new JScrollPane();
		GroupLayout gl_roomPanel = new GroupLayout(roomPanel);
		gl_roomPanel.setHorizontalGroup(
			gl_roomPanel.createParallelGroup(Alignment.TRAILING)
				.addComponent(roomScroll, GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
		);
		gl_roomPanel.setVerticalGroup(
			gl_roomPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(roomScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
		);
		
		roomList = new JList<>();
		roomScroll.setViewportView(roomList);
		roomPanel.setLayout(gl_roomPanel);
		
		JScrollPane userScroll = new JScrollPane();
		GroupLayout gl_userPanel = new GroupLayout(userPanel);
		gl_userPanel.setHorizontalGroup(
			gl_userPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(userScroll, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
		);
		gl_userPanel.setVerticalGroup(
			gl_userPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(userScroll, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
		);
		
		userList = new JList<>();
		userScroll.setViewportView(userList);
		userPanel.setLayout(gl_userPanel);
		userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane chatScroll = new JScrollPane();
		GroupLayout gl_chatPanel = new GroupLayout(chatPanel);
		gl_chatPanel.setHorizontalGroup(
			gl_chatPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(chatScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
		);
		gl_chatPanel.setVerticalGroup(
			gl_chatPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(chatScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
		);
		
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatBox);
		chatPanel.setLayout(gl_chatPanel);
		getContentPane().setLayout(groupLayout);
	}
}
