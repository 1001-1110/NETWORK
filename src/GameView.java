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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameView extends JFrame{

	String opponent;
	boolean opponentMoved;
	String opponentMove;
	int oScore;
	int pScore;
	JButton btnRock;
	JButton btnPaper;
	JButton btnScissors;
	JLabel lblOpponent;
	JLabel lblPlayer;
	JLabel lblResult;
	private JLabel lblPScore;
	private JLabel lblOScore;
	private ClientController cc;
	
	public GameView(String opponent, ClientController cc) {
		this.opponent = opponent;
		this.opponentMoved = false;
		this.cc = cc;
		oScore = 0;
		pScore = 0;
		initialize();
		setVisible(true);
	}
	
	public String getOpponent() {
		return opponent;
	}

	public void setPlayer(String player) {
		lblPlayer.setText(player);
	}

	public void setOpponent(String opponent) {
		opponentMove = opponent;
		if(!lblPlayer.getText().equals("----")) {
			reveal();
		}else {
			opponentMoved = true;
		}
	}	
	
	public void setResult(String result) {
		lblResult.setText(result);
	}
	
	public void reveal() {
		lblOpponent.setText(opponentMove);
		opponentMoved = false;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lblPlayer.setText("----");
		lblOpponent.setText("----");
		lblPScore.setText("Score: "+pScore);
		lblOScore.setText("Score: "+oScore);
		enableButtons();
	}
	
	public void disableButtons() {
		btnRock.setEnabled(false);
		btnPaper.setEnabled(false);
		btnScissors.setEnabled(false);
	}
	
	public void enableButtons() {
		btnRock.setEnabled(true);
		btnPaper.setEnabled(true);
		btnScissors.setEnabled(true);		
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
		
		lblPScore = new JLabel("Score: 0");
		
		lblOScore = new JLabel("Score: 0");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(95)
							.addComponent(choicePanel, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(172)
							.addComponent(lblResult))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(47)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblPScore)
									.addGap(63)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addGap(10)
											.addComponent(lblVs)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(lblOScore))
										.addComponent(lblJanken, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(panel, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
									.addGap(39)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap(70, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblJanken)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblVs)
							.addGap(18))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPScore)
								.addComponent(lblOScore))
							.addPreferredGap(ComponentPlacement.RELATED)))
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
		
		btnRock = new JButton("Rock");
		btnRock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPlayer("Rock");
				cc.sendMove("Rock", opponent);
				disableButtons();
				if(opponentMoved) {
					reveal();
				}
			}
		});
		choicePanel.add(btnRock);
		
		btnPaper = new JButton("Paper");
		btnPaper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPlayer("Paper");
				cc.sendMove("Paper", opponent);
				disableButtons();
				if(opponentMoved) {
					reveal();
				}
			}
		});
		choicePanel.add(btnPaper);
		
		btnScissors = new JButton("Scissors");
		btnScissors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPlayer("Scissors");
				cc.sendMove("Scissors", opponent);
				disableButtons();
				if(opponentMoved) {
					reveal();
				}
			}
		});
		choicePanel.add(btnScissors);
		getContentPane().setLayout(groupLayout);
	}
}
