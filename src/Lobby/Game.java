package Lobby;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Game {
	protected String name;
	protected ArrayList<Player> players;
	protected Player host;
	protected int gameSize;
	
	public Game(String name, Player host, int gameSize){
		this.name = name;
		this.host = host;
		this.gameSize = gameSize;
		players = new ArrayList<Player>();
		players.add(host);
	}
	
	public boolean full(){
		return false;
		
	}
	
	public void send(String message){
		for(Player player: players){
			try {
				OutputStream output = player.getConnection().getOutputStream();
				output.write(message.getBytes());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean addPlayer(Player player){
		if(players.size() >= gameSize){
			return false;
		}
		return players.add(player);
	}
	
	public boolean removePlayer(Player player){
		return players.remove(player);
	}
	
	public boolean playerInGame(Player player){
		boolean inGame = false;
		for(Player p : players){
			if(p.equals(player)){
				inGame = true;
			}
		}
		return inGame;
	}
	
	public String getGamename(){
		return name;
	}
	public void start(){
		// Add code to connect players to host.
	}
	
	public String listGame(){
		return name + "(" + players.size() + "/" + Integer.toString(gameSize) + ")";
	}
	
	public String listFullGame(){
		StringBuilder sb = new StringBuilder();
		sb.append("Game name: " + name + "\n");
		for(int i = 0; i < players.size(); i++){
			sb.append("Player " + i + ": ");
			sb.append(players.get(i).toString() + "\n");
		}
		return sb.toString();

	}

}