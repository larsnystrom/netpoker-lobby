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
	LobbyBox mailBox;
	
	
	public PlayerThread(Socket connection, Lobby lobby, LobbyBox mailBox){
		this.connection = connection;
		this.lobby = lobby;
		this.mailBox = mailBox;
	}
	
	public void run(){
		InputStream input;
		try {
			Player player = new Player("new" ,connection);
			
			input = connection.getInputStream();
			OutputStream output = connection.getOutputStream();
			BufferedReader reader= new BufferedReader(new InputStreamReader(input));
			
			boolean added = false;
			while(!added){
				String name = reader.readLine();
				if(!lobby.findPlayer(name)){
					player.setName(name);
					lobby.addIdlePlayer(player);
					added = true;
					break;
				}else{
					output.write(("Name already taken \n").getBytes());
					output.flush();
				}
				
			}
			
			
			output.write((CommandHelper.availableCommands() + "\n").getBytes());
			System.out.println("Skickat commands");
			output.flush();
			String command = " ";
			do{
				String line = reader.readLine();
				if(line != null){
					String[] splittedLine = line.split(":");
					command = splittedLine[0];
					String commandExtra = null;
					if(splittedLine.length > 1){
						commandExtra = splittedLine[1];
						commandExtra = commandExtra.trim();
					}
					
						
					if(command.equals(CommandHelper.commands[0])){
						output.write((lobby.listGames() +"\n").getBytes());
						output.flush();
					}else if(command.equals(CommandHelper.commands[1])){
						
						if(commandExtra != null){
							lobby.send(commandExtra);
						}else{
							output.write(("No message typed \n").getBytes());
							output.flush();
						}
						
					}else if(command.equals(CommandHelper.commands[2])){
						if(commandExtra != null && commandExtra.charAt(0) != ' '){
							mailBox.write(commandExtra + "\n");
						}else{
							output.write(("No message typed \n").getBytes());
							output.flush();
						}
					}else if(command.equals(CommandHelper.commands[3])){
						output.write((CommandHelper.availableCommands() + "\n").getBytes());
						output.flush();
					}else if(command.equals(CommandHelper.commands[4])){
						if(lobby.addPlayerToGame(commandExtra, player)){
							output.write(("Joined game " + commandExtra + " \n").getBytes());
							output.flush();
						}else{
							output.write(("Couldn«t find game or game is already full \n").getBytes());
							output.flush();
						}
					}else if(command.equals(CommandHelper.commands[5])){
						if(lobby.findGame(commandExtra) == null){
							lobby.addGame(new Game(commandExtra, player, 8));
							output.write(("Game: " + commandExtra + " created \n").getBytes());
							output.flush();
						}else{
							output.write(("Gamename already taken \n").getBytes());
							output.flush();
						}
					}else if(command.equals(CommandHelper.commands[6])){
						if(lobby.removePlayerFromGame(commandExtra, player)){
							output.write(("Left game " + commandExtra + " \n").getBytes());
							output.flush();
						}else{
							output.write(("Wrong gamename \n").getBytes());
							output.flush();
						}
					}else{
						output.write(("No such command \n").getBytes());
						output.flush();
					}
				}else{
					break;
				}				
				
			}while(!command.equals("quit") && player.getConnection().isConnected());
			connection.close();
			lobby.removeLostPlayer(player);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
