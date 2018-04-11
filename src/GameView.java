import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;

public class GameView extends JFrame{

	String opponent;
	JLabel lblOpponent;
	JLabel lblPlayer;
	JLabel lblResult;
	
	public GameView(String opponent) {
		this.opponent = opponent;
		initialize();
		setVisible(true);
	}
	
	public void setPlayer(String player) {
		lblPlayer.setText(player);
	}

	public void setOpponent(String opponent) {
		lblPlayer.setText(opponent);
	}	
	
	public void setResult(String result) {
		lblResult.setText(result);
	}
	
	private void initialize() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JLabel lblJanken = new JLabel("JANKEN");
		lblJanken.setFont(new Font("Tunga", Font.PLAIN, 30));
		
		JPanel choicePanel = new JPanel();
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "You", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Opponent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel lblVs = new JLabel("vs "+opponent);
		
		lblResult = new JLabel("Result: ----");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(47)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
							.addGap(39)
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(95)
							.addComponent(choicePanel, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(156)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(10)
									.addComponent(lblVs))
								.addComponent(lblJanken, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(172)
							.addComponent(lblResult)))
					.addContainerGap(70, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblJanken)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblVs)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblResult)
					.addGap(8)
					.addComponent(choicePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(29))
		);
		
		lblOpponent = new JLabel("----");
		panel_1.add(lblOpponent);
		
		lblPlayer = new JLabel("----");
		panel.add(lblPlayer);
		
		JButton btnRock = new JButton("Rock");
		choicePanel.add(btnRock);
		
		JButton btnPaper = new JButton("Paper");
		choicePanel.add(btnPaper);
		
		JButton btnScissors = new JButton("Scissors");
		choicePanel.add(btnScissors);
		getContentPane().setLayout(groupLayout);
	}
}
