
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
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class ClientView extends JFrame{

	private ClientController cc;
	
	private JTextField input;
	private JList<String> userList;
	private JList<String> roomList;
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
		setBounds(100, 100, 640, 320);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			setDefaultLookAndFeelDecorated(isDefaultLookAndFeelDecorated());			
		}catch(Exception e) {}
		
		input = new JTextField();
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					cc.sendMessage(input.getText(),"all");
					input.setText(null);
				}
			}
		});
		input.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cc.sendMessage(input.getText(),"all");
				input.setText(null);
			}
		});
		
		JButton btnLogout = new JButton("Log Out");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(getDefaultCloseOperation());
			}
		});
		
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new TitledBorder(null, "Global Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel userPanel = new JPanel();
		userPanel.setBorder(new TitledBorder(null, "Online Users", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JButton btnChat = new JButton("Private Chat");
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
		
		JPanel roomPanel = new JPanel();
		roomPanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Chatrooms", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		
		JButton btnJoin = new JButton("Join");
		
		JButton btnCreate = new JButton("Create");
		
		JButton btnGames = new JButton("Games");
		
		JButton btnFileTransfer = new JButton("File Transfer");
		btnFileTransfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				File file = fc.getSelectedFile();
				System.out.println(file.getName());
			}
		});

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(input, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSend)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
							.addGap(8)))
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnGames)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFileTransfer)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnLogout))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnCreate)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnJoin))
								.addComponent(roomPanel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnChat, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(2)
									.addComponent(userPanel, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
									.addGap(2)))))
					.addGap(12))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(roomPanel, 0, 0, Short.MAX_VALUE)
								.addComponent(userPanel, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnCreate)
								.addComponent(btnJoin)
								.addComponent(btnChat)))
						.addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnSend))
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnLogout)
							.addComponent(btnFileTransfer)
							.addComponent(btnGames)))
					.addGap(12))
		);
		
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
				.addComponent(userScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
		);
		gl_userPanel.setVerticalGroup(
			gl_userPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(userScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
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
