
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;

public class FileTransferView extends JFrame{
	
	JLabel lblState;
	JProgressBar progressBar;
	JLabel lblAmount;
	
	public FileTransferView(int x, int y) {
		initialize(x,y);
		setVisible(true);
	}

	public void setState(String state) {
		lblState.setText(state);
	}
	
	public void setAmount(String amount) {
		lblAmount.setText(amount);
	}
	
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}
	
	public void setMaxProgress(int maxProgress) {
		progressBar.setMaximum(maxProgress);
	}	
	private void initialize(int x, int y) {
		setBounds(x+100, y+100, 310, 125);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		lblState = new JLabel("");
		
		progressBar = new JProgressBar();
		
		lblAmount = new JLabel("");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGap(43)
							.addComponent(lblAmount, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGap(22)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblState, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(22, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblState, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblAmount, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
	}
}
