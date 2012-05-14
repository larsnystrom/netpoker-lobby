package netpoker.lobby.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import netpoker.lobby.ClientCommands;

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
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void send(String message){
		for(Player player: players){
			try {
				System.out.println("Skickar till playerInGame: " + player.getName());
				OutputStream output = player.getConnection().getOutputStream();
				output.write(message.getBytes());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void sendHost(String message){
		Socket hostConnection = host.getConnection();
		try {
			OutputStream output = hostConnection.getOutputStream();
			output.write(message.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public boolean nameInGame(String playerName){
		boolean inGame = false;
		for(Player p : players){
			if(p.getName().equals(playerName)){
				inGame = true;
				break;
			}
		}
		return inGame;
	}
	
	public Player getHost(){
		return host;
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
		sb.append("Game name: "  + listGame() + ClientCommands.STRINGSPLITTER);
		sb.append("Host: " + host.getName() + ClientCommands.STRINGSPLITTER);
		for(int i = 0; i < players.size(); i++){
			int write = i + 1;
			sb.append("Player " + write + ": ");
			sb.append(players.get(i).getName() + ClientCommands.STRINGSPLITTER);
		}
		return sb.toString();
	}
	
	public boolean equals(Game game){
		return game.name.equals(name);
	}

}
