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
		for(Game game: games){
			game.send(message);
		}
		for(Player idlePlayer: idlePlayers){
			try {
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
			sb.append(game.toString() + "\n");
		}
		return sb.toString();
	}
	
	public boolean addGame(Game game){
		return games.add(game);
	}
	
	public boolean addIdlePlayer(Player player){
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
		return removePlayer(findGame(gameName), player);
	}
	
	public boolean removeLostPlayer(Player player){
		// Check which game a player is in and removes he/she from it.
		for(Game game: games){
			if(game.playerInGame(player)){
				return removePlayer(game, player);
			}
		}
		// If the player isn«t in any game he/she is removed from the idlePlayers-list instead.
		return idlePlayers.remove(player);
		
	}
	
	private boolean removePlayer(Game game, Player player){
		if(game != null){
			return game.removePlayer(player);
		}
		return false;
	}


	private Game findGame(String gameName){
		for(Game game: games){
			if(gameName.equals(game.getGamename()));
				return game;
		}
		return null;
	}
}
