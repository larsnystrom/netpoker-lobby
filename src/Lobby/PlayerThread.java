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
					mailBox.write(ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.ALL + ClientCommands.SPLITTER + player.getName() + " joined the lobby\n");
					added = true;
					break;
				}else{
					output.write((ClientCommands.INITIALIZE + ClientCommands.SPLITTER + ClientCommands.NAMETAKEN + "\n").getBytes());
					output.flush();
				}
				
			}
			
			
			output.write((ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "You joined the server\n").getBytes());
			System.out.println("Initiated player");
			output.flush();
			String[] commandSequence = new String[1];
			do{
				String line = reader.readLine();
				if(line != null){
					commandSequence = line.split(ServerCommands.SPLITTER);
					String commandExtra = null;
					if(commandSequence.length > 1){
						commandExtra = commandSequence[1];
						commandExtra = commandExtra.trim();
					}
					
					String toSend = null;
					if(commandSequence[0].equals(ServerCommands.LIST)){
						System.out.println("command: " + ServerCommands.LIST);
						toSend = ClientCommands.INITIALIZE + ClientCommands.SPLITTER + ClientCommands.LIST + ClientCommands.SPLITTER + lobby.listGames() +"\n";
						output.write(toSend.getBytes());
					}else if(commandSequence[0].equals(ServerCommands.MESSAGEALL)){
						System.out.println("command: " + ServerCommands.MESSAGEALL);
						toSend = ClientCommands.CHAT + ClientCommands.SPLITTER;
						if(commandExtra != null){
							toSend = toSend + ClientCommands.ALL + ClientCommands.SPLITTER + player.getName() + ": " + commandExtra + "\n";
							lobby.send(toSend);
						}else{
							toSend = toSend + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "No message typed \n";
							output.write(toSend.getBytes());
						}
						
					}else if(commandSequence[0].equals(ServerCommands.HELP)){
						//TODO
						
//						System.out.println("command: " + ServerCommands.idleCommands[3]);
//						toSend = ServerCommands.availableCommands() + "\n";
//						output.write(toSend.getBytes());
					}else if(commandSequence[0].equals(ServerCommands.JOIN)){
						System.out.println("command: " + ServerCommands.JOIN);
						if(!player.isInGame()){
							if(lobby.addPlayerToGame(commandExtra, player)){
								player.setInGame(true);
								toSend = ClientCommands.GAME + ClientCommands.SPLITTER + ClientCommands.JOIN + ClientCommands.SPLITTER + commandExtra + "\n";
							}else{
								toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "Couldn«t find game or game is already full \n";
							}
						}else{
							toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "You need to leave the game you are in before joining another one\n";
						}
						output.write(toSend.getBytes());
						if(player.isInGame()){
							Game game = lobby.findGameFromPlayer(player);
							game.send(ClientCommands.GAME + ClientCommands.SPLITTER + ClientCommands.OTHERJOIN + ClientCommands.SPLITTER + game.getGamename() + ClientCommands.SPLITTER + player.getName() + " joined the game\n");
						}
						
					}else if(commandSequence[0].equals(ServerCommands.CREATE)){
						System.out.println("command: " + ServerCommands.CREATE);
						if(!player.isInGame()){
							if(lobby.findGame(commandExtra) == null && lobby.addGame(new Game(commandExtra, player, 8))){
								player.setInGame(true);
								toSend = ClientCommands.GAME + ClientCommands.SPLITTER + ClientCommands.HOST + ClientCommands.SPLITTER + commandExtra + "\n"; 
							}else{
								toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "Gamename already taken \n";
							}
						}else{
							toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "You need to leave the game you are in before creating another one\n";
						}
						output.write(toSend.getBytes());					 
					}else if(commandSequence[0].equals(ServerCommands.GAMEINFO)){
						System.out.println("command: " + ServerCommands.GAMEINFO);
						if(player.isInGame()){
							Game game = lobby.findGame(commandExtra);
							if(game != null){
								toSend = ClientCommands.GAME + ClientCommands.SPLITTER + ClientCommands.INFO + ClientCommands.SPLITTER + game.listFullGame() + "\n";
							}else{
								toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "couldn«t find game \n";
							}
						}else{
							toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "You need to be in a game to use this command \n";
						}
						output.write(toSend.getBytes());
					}else if(commandSequence[0].equals(ServerCommands.MESSAGEGAME)){
						System.out.println("command: " + ServerCommands.MESSAGEGAME);
						if(player.isInGame()){
							if(commandExtra != null && commandExtra.charAt(0) != ' '){
								Game game = lobby.findGameFromPlayer(player);
								if(game != null){
									toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.GAMECHAT + ClientCommands.SPLITTER + player.getName() + ": " + commandExtra + "\n";
									game.send(toSend);
								}else{
									toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "couldn«t find game \n";
									output.write(toSend.getBytes());
								}
							}else{
								toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "No message typed \n";
								output.write(toSend.getBytes());
							}
						}else{
							toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "You need to be in a game to use this command \n";
							output.write(toSend.getBytes());
						}
						
						
					}else if(commandSequence[0].equals(ServerCommands.LEAVE)){
						System.out.println("command: " + ServerCommands.LEAVE);
						if(player.isInGame()){
							if(lobby.removePlayerFromGame(commandExtra, player)){
								player.setInGame(false);
								toSend = ClientCommands.GAME + ClientCommands.SPLITTER + ClientCommands.LEAVE + ClientCommands.SPLITTER + commandExtra + "\n";
							}else{
								toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "Wrong gamename \n";
							}
						}else{
							toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "You need to be in a game to use this command \n";
						}						
						output.write(toSend.getBytes());
					}else if(commandSequence[0].equals(ServerCommands.START)){
						//TODO
					}else{
						toSend = ClientCommands.CHAT + ClientCommands.SPLITTER + ClientCommands.SYSTEM + ClientCommands.SPLITTER + "No such command \n";
						output.write(toSend.getBytes());
					}
					output.flush();
					
				}else{
					break;
				}				
				
			}while(!commandSequence[0].equals(ServerCommands.QUIT) && player.getConnection().isConnected());
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
