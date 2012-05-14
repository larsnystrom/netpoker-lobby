package netpoker.lobby.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	
	
	public static void main(String[]args){
		Lobby lobby = new Lobby();
		LobbyBox mailBox = new LobbyBox();
		ReaderThread mailBoxReader = new ReaderThread(mailBox, lobby);
		mailBoxReader.start();
		ServerSocket server = null;
		try {
			server = new ServerSocket(30000);
			System.out.println("Started server: " + server.getInetAddress().getHostAddress());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
			try {				
				Socket connection = server.accept();
				InetAddress address = connection.getInetAddress();
				
				System.out.println("Connection to " + address.toString() + " opened.");
				PlayerThread thread = new PlayerThread(connection, lobby, mailBox);
				thread.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
}
