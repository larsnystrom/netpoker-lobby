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
					
					if(command.equals("L")){
						System.out.println("Command: " + " L");
					}else if(command.equals("MA")){
						System.out.println("Command: " + " MA");				
					}else if(command.equals("MG")){
						System.out.println("Command: " + " MG");
					}else if(command.equals("help")){
						System.out.println("Command: " + " help");
					}else if(command.equals("join")){
						System.out.println("Command: " + " join");
					}else if(command.equals("Q")){
						System.out.println("Command: " + " Q");
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
