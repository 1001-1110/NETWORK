
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import java.awt.Color;

public class GroupChatView extends JFrame{

	private ClientController cc;
	
	private String owner;
	private ArrayList<String> participants;
	
	private JTextField input;
	private JTextArea chatBox;
	private JList<String> userList;
	
	public GroupChatView(ClientController cc, String owner, ArrayList<String> participants, String participantsString) {
		super("Group Chat: "+owner+" -> "+participantsString);
		this.cc = cc;
		this.owner = owner;
		this.participants = participants;
		initialize();
		this.setVisible(true);
	}
	
	public ArrayList<String> getNames() {
		return participants;
	}
	
	public void updateChat(String update) {
		chatBox.append("\n"+update);
	}

	public void updateParticipants(String[] users) {
		userList.setListData(users);
	}	
	
	private void initialize() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			setDefaultLookAndFeelDecorated(isDefaultLookAndFeelDecorated());			
		}catch(Exception e) {}

		JScrollPane chatScroll = new JScrollPane();
		
		input = new JTextField();
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					cc.sendGroupMessage(input.getText(),participants);
					input.setText(null);
				}
			}
		});
		input.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cc.sendGroupMessage(input.getText(),participants);
				input.setText(null);
			}
		});
		
		JScrollPane userScroll = new JScrollPane();
		
		JButton btnLogout = new JButton("Exit");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(getDefaultCloseOperation());
			}
		});

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(input)
						.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(btnSend)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnLogout)
							.addGap(12))
						.addComponent(userScroll, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(userScroll, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(13)
							.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSend)
						.addComponent(btnLogout))
					.addGap(12))
		);
		
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatBox);
		
		String[] users = new String[participants.size()];
		for(int i = 0 ; i < participants.size(); i++)
			users[i] = participants.get(i);
		
		userList = new JList<String>(users);
		userList.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Participants", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		userScroll.setViewportView(userList);
		getContentPane().setLayout(groupLayout);
	}
}
