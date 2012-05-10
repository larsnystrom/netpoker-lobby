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
					mailBox.write(player.getName() + " joined the server\n");
					added = true;
					break;
				}else{
					output.write(("Name already taken, choose another one \n").getBytes());
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
					
					String toSend = null;
					if(command.equals(CommandHelper.idleCommands[0])){
						System.out.println("command: " + CommandHelper.idleCommands[0]);
						toSend = lobby.listGames() +"\n";
						output.write(toSend.getBytes());
					}else if(command.equals(CommandHelper.idleCommands[1])){
						System.out.println("command: " + CommandHelper.idleCommands[1]);
						if(commandExtra != null){
							lobby.send("(Lobby) " + player.getName() + ": " + commandExtra + "\n");
						}else{
							toSend = "No message typed \n";
							output.write(toSend.getBytes());
						}
						
					}else if(command.equals(CommandHelper.idleCommands[2])){
						System.out.println("command: " + CommandHelper.idleCommands[3]);
						toSend = CommandHelper.availableCommands() + "\n";
						output.write(toSend.getBytes());
					}else if(command.equals(CommandHelper.idleCommands[3])){
						System.out.println("command: " + CommandHelper.idleCommands[4]);
						if(!player.isInGame()){
							if(lobby.addPlayerToGame(commandExtra, player)){
								player.setInGame(true);
								toSend = "Joined game " + commandExtra + " \n";
							}else{
								toSend = "Couldn«t find game or game is already full \n";
							}
						}else{
							toSend = "You need to leave the game you are in before joining another one\n";
						}
						output.write(toSend.getBytes());
						if(player.isInGame()){
							Game game = lobby.findGameFromPlayer(player);
							game.send(player.getName() + " joined the game\n");
						}
						
					}else if(command.equals(CommandHelper.idleCommands[4])){
						System.out.println("command: " + CommandHelper.idleCommands[5]);
						if(!player.isInGame()){
							if(lobby.findGame(commandExtra) == null && lobby.addGame(new Game(commandExtra, player, 8))){
								player.setInGame(true);
								toSend = "Game: " + commandExtra + " created \n";
							}else{
								toSend = "Gamename already taken \n";
							}
						}else{
							toSend = "You need to leave the game you are in before creating another one\n";
						}
						output.write(toSend.getBytes());
						
					}else if(command.equals(CommandHelper.gameCommands[0])){
						System.out.println("command: " + CommandHelper.gameCommands[0]);
						if(player.isInGame()){
							Game game = lobby.findGameFromPlayer(player);
							if(game != null){
								toSend = game.listFullGame();
							}else{
								toSend = "couldn«t find game \n";
							}
							output.write(toSend.getBytes());
						}else{
							toSend = "You need to be in a game to use this command \n";
						}
					}else if(command.equals(CommandHelper.gameCommands[1])){
						System.out.println("command: " + CommandHelper.gameCommands[1]);
						if(player.isInGame()){
							if(commandExtra != null && commandExtra.charAt(0) != ' '){
								Game game = lobby.findGameFromPlayer(player);
								if(game != null){
									game.send("(" + game.getGamename() + ") " + player.getName() + ": " + commandExtra + "\n");
								}else{
									toSend = "couldn«t find game \n";
									output.write(toSend.getBytes());
								}
							}else{
								toSend = "No message typed \n";
								output.write(toSend.getBytes());
							}
						}else{
							toSend = "You need to be in a game to use this command \n";
							output.write(toSend.getBytes());
						}
						
						
					}else if(command.equals(CommandHelper.gameCommands[2])){
						System.out.println("command: " + CommandHelper.gameCommands[2]);
						if(player.isInGame()){
							if(lobby.removePlayerFromGame(commandExtra, player)){
								player.setInGame(false);
								toSend = "Left game " + commandExtra + " \n";
							}else{
								toSend = "Wrong gamename \n";
							}
						}else{
							toSend = "You need to be in a game to use this command \n";
						}						
						output.write(toSend.getBytes());
					}else{
						toSend = "No such command \n";
						output.write(toSend.getBytes());
					}
					output.flush();
				}else{
					break;
				}				
				
			}while(!command.equals("quit") && player.getConnection().isConnected());
			connection.close();
			if(lobby.removeLostPlayer(player)){
				player.setInGame(false);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
