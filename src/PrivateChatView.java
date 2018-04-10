
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class PrivateChatView extends JFrame{

	private ClientController cc;
	private String owner;
	private String recipient;
	
	private JTextField input;
	private JTextArea chatBox;
	private JButton btnSendFile;
	private JButton btnStartGame;
	
	public PrivateChatView(ClientController cc, String owner, String recipient) {
		super("Private Chat: "+owner+" -> "+recipient);
		this.cc = cc;
		this.owner = owner;
		this.recipient = recipient;
		initialize();
		this.setVisible(true);
	}
	
	public String getName() {
		return recipient;
	}
	
	public void updateChat(String update) {
		chatBox.append("\n"+update);
	}

	private void sendMessage(String message) {
		if(message.equals("/clear")) {
			chatBox.setText("");
		}else if(message.equals("/autoscroll")) {
			autoScroll();
		}else if(!message.equals("")) {
			cc.sendMessage(message,recipient);
			updateChat(owner+": "+message);
		}
	}	
	
	private void autoScroll() {
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	private void initialize() {
		setBounds(100, 100, 620, 300);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		JScrollPane chatScroll = new JScrollPane();
		
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
		
		btnSendFile = new JButton("Send File");
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file;
				if((file = cc.showFileChooser(JFileChooser.FILES_ONLY)) != null) {
					if(file.exists()) {
						cc.sendFileUploadRequest(file, recipient);
					}else {
						cc.showErrorNotif("File does not exist","Invalid");
					}
				}
			}
		});
		
		btnStartGame = new JButton("Start Game");

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(input, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSendFile, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStartGame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(8)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(13)
					.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
					.addGap(14)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSendFile, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnStartGame, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addGap(12))
		);
		
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatBox);
		getContentPane().setLayout(groupLayout);
	}
}
