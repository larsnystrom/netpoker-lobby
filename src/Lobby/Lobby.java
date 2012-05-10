package Lobby;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Lobby {
	ArrayList<Game> games;
	ArrayList<Player> idlePlayers;
	
	public Lobby(){
		games = new ArrayList<Game>();
		idlePlayers = new ArrayList<Player>();
	}
	
	public void send(String message){
		System.out.println("SendAll");
		for(Game game: games){
			game.send(message);
		}
		for(Player idlePlayer: idlePlayers){
			try {
				System.out.println("Skickar till idlePlayer: " + idlePlayer.getName());
				OutputStream output = idlePlayer.getConnection().getOutputStream();
				output.write(message.getBytes());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
	
	public String listGames(){
		StringBuilder sb = new StringBuilder();
		sb.append("Games: \n");
		for(Game game: games){
			sb.append(game.getGamename() + "\n");
		}
		return sb.toString();
	}
	
	public boolean addGame(Game game){
		if(games.add(game)){
			idlePlayers.remove(game.getHost());
			return true;
		}
		return false;
	}
	
	public boolean addIdlePlayer(Player player){
		System.out.println("Adding idle player: " + player.getName());
		return idlePlayers.add(player);
	}
	
	public boolean addPlayerToGame(String gameName, Player player){
		boolean added = false;
		Game game = findGame(gameName);
		if(game != null){
			added = game.addPlayer(player);
			idlePlayers.remove(player);
		}
		return added;
	}
	
	public boolean removePlayerFromGame(String gameName, Player player){
		if(removePlayer(findGame(gameName), player)){
			idlePlayers.add(player);
			return true;
		}
		return false;
	}
	
	public boolean removeLostPlayer(Player player){
		// Check which game a player is in and removes he/she from it.
		for(Game game: games){
			if(game.playerInGame(player)){
				System.out.println("Removing idle player: " + player.getName());
				if(game.host.equals(player)){
					return closeGame(game, player);
				}
				return removePlayer(game, player);
			}
		}
		// If the player isn«t in any game he/she is removed from the idlePlayers-list instead.
		System.out.println("Removing idle player: " + player.getName());
		return idlePlayers.remove(player);
		
	}
	
	private boolean closeGame(Game game, Player player){
		removePlayer(game, player);
		game.send("Host gone, closing game \n");
		for(int i = 0; i < game.players.size(); i++){
			idlePlayers.add(game.players.remove(i));
		}
		games.remove(game);
		return true;
		
	}
	
	private boolean removePlayer(Game game, Player player){
		if(game != null){
			return game.removePlayer(player);
		}
		return false;
	}
	
	public boolean findPlayer(String playerName){
		boolean found = false;
		for(Player player: idlePlayers){
			if(player.getName().equals(playerName)){
				found = true;
				break;
			}
		}
		if(!found){
			for(Game game: games){
				if(game.nameInGame(playerName)){
					found = true;
					break;
				}
			}
		}
		return found;
	}

	public Game findGameFromPlayer(Player player){
		for(Game game: games){
			if(game.playerInGame(player)){
				return game;
			}
		}
		return null;
	}

	public Game findGame(String gameName){
		for(Game game: games){
			if(gameName.equals(game.getGamename())){
				System.out.println("Game att skapa: " + gameName + " game att jŠmfšra med: " + game.getGamename());
				return game;
			}
				
		}
		return null;
	}
}
