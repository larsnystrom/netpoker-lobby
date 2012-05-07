package Lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class PlayerThread extends Thread {
	Socket connection;
	Lobby lobby;
	MailBox mailBox;
	
	public PlayerThread(Socket connection, Lobby lobby, MailBox mailBox){
		this.connection = connection;
		this.lobby = lobby;
		this.mailBox = mailBox;
	}
	
	public void run(){
		InputStream input;
		try {
			Player player = new Player("name" ,connection);
			lobby.addIdlePlayer(player);
			input = connection.getInputStream();
			OutputStream output = connection.getOutputStream();
			BufferedReader reader= new BufferedReader(new InputStreamReader(input));
			output.write((CommandHelper.availableCommands() + "\n").getBytes());
			output.flush();
			String command;
			do{
				String line = reader.readLine();
				if(line.length() >= 2){
					
					command = line.substring(0, 2);
					
					if(command.equals("L:")){
						//TODO
					}else if(command.equals("MA")){
						//TODO				
					}else if(command.equals("MG")){
						//TODO
					}else if(command.equals("help:")){
						//TODO
					}else if(command.equals("Join")){
						//TODO
					}else if(command.equals("Q")){
						//TODO
					}else{
						//NO such command
					}
				}else{
					command = "...";
				}
				
			}while(!command.equals("Q:"));
			connection.close();
			lobby.removeLostPlayer(player);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
