
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.border.LineBorder;

public class ServerView extends JFrame{
	
	private JTextArea chatBox;
	private JList<String> userList;
	private JList<String> roomList;
	private JList<String> fileList;

	public ServerView(int port) {
		super("Server ("+port+")");
		initialize();
		this.setVisible(true);
	}

	public void updateChat(String update) {
		chatBox.append("\n"+update);
	}
	
	public void updateOnline(String[] users) {
		userList.setListData(users);
	}

	public void updateRooms(String[] rooms) {
		roomList.setListData(rooms);
	}	
	
	private void initialize() {
		setBounds(100, 100, 670, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane chatScroll = new JScrollPane();
		
		JScrollPane userScroll = new JScrollPane();
		
		JScrollPane roomScroll = new JScrollPane();
		
		JScrollPane fileScroll = new JScrollPane();

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(userScroll, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(roomScroll, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(fileScroll, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
					.addGap(4))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGap(13)
							.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(fileScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
								.addComponent(roomScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
								.addComponent(userScroll, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))))
					.addContainerGap())
		);
		
		fileList = new JList<String>();
		fileList.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		fileScroll.setViewportView(fileList);
		
		roomList = new JList<String>();
		roomList.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Chatrooms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		roomScroll.setViewportView(roomList);
		
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatBox);
		
		userList = new JList<String>();
		userList.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Online Users", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		userScroll.setViewportView(userList);
		getContentPane().setLayout(groupLayout);
	}
}
