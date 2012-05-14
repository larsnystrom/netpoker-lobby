package Lobby;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class LobbyClient {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java LobbyClient <hostname> <port>");
			System.exit(1);
		}

		final Socket connection;
		try {
			connection = new Socket(InetAddress.getByName(args[0]),
					Integer.parseInt(args[1]));

			LobbyClientGUI window = new LobbyClientGUI(connection);
			window.setPlayerName();
			
			InputStream input;

			try {
				input = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
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

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
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
