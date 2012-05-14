package netpoker.lobby.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JEditorPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MultipleTextFieldDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private String[] values;

	/**
	 * Create the dialog.
	 */
	public MultipleTextFieldDialog(final String[] dialogValues) {
		values = new String[2];
		setBounds(100, 100, 400, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblHostAddress = new JLabel("Host Address");
		lblHostAddress.setBounds(62, 19, 85, 16);

		final JEditorPane editorPane_1 = new JEditorPane();
		editorPane_1.setBounds(165, 19, 146, 16);

		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(122, 47, 25, 16);

		final JEditorPane editorPane_2 = new JEditorPane();
		editorPane_2.setBounds(165, 47, 146, 16);
		JPanel dialogPanel = new JPanel();
		contentPanel.setLayout(null);
		contentPanel.add(lblHostAddress);
		contentPanel.add(lblPort);
		contentPanel.add(editorPane_2);
		contentPanel.add(editorPane_1);
		
		editorPane_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_TAB){
					editorPane_2.requestFocus(true);
					editorPane_2.setCaretPosition(0);
					editorPane_2.setText("");
				}
			}
		});
		
		editorPane_2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_TAB){
					editorPane_1.requestFocus(true);
					editorPane_1.setCaretPosition(0);
					editorPane_1.setText("");
				}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
					values[0] = editorPane_1.getText().trim();
					values[1] = editorPane_2.getText().trim();		
					setValues();
					dispose();
				}
			}
		});
		

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String s1 = editorPane_1.getText();
				String s2 = editorPane_2.getText();
				if(s1 != null && s2 != null){
					values[0] = editorPane_1.getText().trim();
					values[1] = editorPane_2.getText().trim();		
					setValues();
					dispose();
				}
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});

	}
	
	public synchronized String[] getValues() {
		while (values[0] == null || values[1] == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return values;
	}
	
	public synchronized void setValues() {
		notifyAll();
	}
}
