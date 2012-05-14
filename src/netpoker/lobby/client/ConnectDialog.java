package netpoker.lobby.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JEditorPane;

public class ConnectDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ConnectDialog dialog = new ConnectDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ConnectDialog() {
		setBounds(100, 100, 400, 180);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel label = new JLabel("PlayerName");
		label.setBounds(87, 30, 73, 16);
		JLabel label_1 = new JLabel("Host Address");
		label_1.setBounds(75, 58, 85, 16);
		JEditorPane editorPane = new JEditorPane();
		editorPane.setBounds(178, 58, 146, 16);
		JEditorPane editorPane_1 = new JEditorPane();
		editorPane_1.setBounds(178, 30, 146, 16);
		contentPanel.setLayout(null);
		contentPanel.add(label);
		contentPanel.add(label_1);
		contentPanel.add(editorPane);
		contentPanel.add(editorPane_1);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
