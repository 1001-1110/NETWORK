
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

public class ServerView extends JFrame{
	
	private JTextArea chatBox;
	private JList<String> userList;

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
	
	private void initialize() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane chatScroll = new JScrollPane();
		
		JScrollPane userScroll = new JScrollPane();

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(userScroll, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(13)
							.addComponent(chatScroll, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(userScroll, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatBox);
		
		userList = new JList<String>();
		userList.setBorder(new TitledBorder(null, "Online Users", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		userScroll.setViewportView(userList);
		getContentPane().setLayout(groupLayout);
	}
}
