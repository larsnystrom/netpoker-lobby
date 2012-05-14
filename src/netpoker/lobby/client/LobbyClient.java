package netpoker.lobby.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JDialog;

public class LobbyClient {
	private static boolean connected = false;
	private static Socket connection;

	public static void main(String[] args) {

		while(connected == false){
			String[] dialogValues = new String[2];
			MultipleTextFieldDialog dialog = new MultipleTextFieldDialog(dialogValues);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			dialogValues = dialog.getValues();
			if(dialogValues[0] != null && dialogValues[1] != null){
				try {
					connection = new Socket(InetAddress.getByName(dialogValues[0]),
							Integer.parseInt(dialogValues[1]));
					connected = true;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		LobbyClientGUI window = new LobbyClientGUI(connection);
		window.setPlayerName();

		InputStream input;

		try {
			input = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			String line = reader.readLine();

			while (line != null) {
				System.out.println(line);
				window.updateGUI(line);
				line = reader.readLine();
			}
			System.exit(1);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void connect(String playerName, String hostAddress, String port) {
		try {
			connection = new Socket(InetAddress.getByName(hostAddress),
					Integer.parseInt(port));
			connected = true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
