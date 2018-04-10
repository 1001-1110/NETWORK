import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainView extends JFrame{

	private MainController mc;
	
	private JTextField startServerPort;
	private JTextField connectServerIP;
	private JTextField connectServerPort;
	private JTextField username;

	public MainView(MainController mc) {
		super("Main Menu");
		this.mc = mc;
		initialize();
		this.setVisible(true);
	}

	private void initialize() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel serverPanel = new JPanel();
		FlowLayout fl_serverPanel = (FlowLayout) serverPanel.getLayout();
		fl_serverPanel.setVgap(10);
		serverPanel.setBorder(new TitledBorder(null, "Server", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel clientPanel = new JPanel();
		FlowLayout fl_clientPanel = (FlowLayout) clientPanel.getLayout();
		fl_clientPanel.setVgap(10);
		clientPanel.setBorder(new TitledBorder(null, "Client", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(serverPanel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
					.addComponent(clientPanel, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(serverPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
						.addComponent(clientPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		JLabel ipLabel = new JLabel("Server IP: ");
		clientPanel.add(ipLabel);
		
		connectServerIP = new JTextField();
		clientPanel.add(connectServerIP);
		connectServerIP.setColumns(10);
		
		JLabel sportLabel = new JLabel("Server Port:");
		clientPanel.add(sportLabel);
		
		connectServerPort = new JTextField();
		clientPanel.add(connectServerPort);
		connectServerPort.setColumns(10);
		
		JLabel usernameLabel = new JLabel("Username: ");
		clientPanel.add(usernameLabel);
		
		username = new JTextField();
		username.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					mc.newClient(connectServerIP.getText(), Integer.parseInt(connectServerPort.getText()), username.getText());
				}
			}
		});
		clientPanel.add(username);
		username.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mc.newClient(connectServerIP.getText(), Integer.parseInt(connectServerPort.getText()), username.getText());
			}
		});
		clientPanel.add(btnConnect);
		
		JLabel portLabel = new JLabel("Port: ");
		serverPanel.add(portLabel);
		
		startServerPort = new JTextField();
		startServerPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					mc.newServer(Integer.parseInt(startServerPort.getText()));
				}
			}
		});
		serverPanel.add(startServerPort);
		startServerPort.setColumns(10);
		
		JButton btnStartServer = new JButton("Start Server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mc.newServer(Integer.parseInt(startServerPort.getText()));
			}
		});
		serverPanel.add(btnStartServer);
		getContentPane().setLayout(groupLayout);
	}
}
