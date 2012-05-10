package Lobby;
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

		try {
			Socket connection = new Socket(InetAddress.getByName(args[0]),
					Integer.parseInt(args[1]));
			WriterThread writer = new WriterThread(connection);
			writer.start();
			InputStream input = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			System.out.println("Write your player name:");
			String line = reader.readLine();

			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
			System.exit(1);

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
