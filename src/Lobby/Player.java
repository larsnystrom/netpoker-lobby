package Lobby;

import java.net.Socket;

public class Player {
	private String name;
	private Socket connection;
	private boolean inGame;
	public Player(String name, Socket connection){
		this.name = name;
		this.connection = connection;
		inGame = false;
	}

	public String getName(){
		return name;
	}
	
	public boolean isInGame(){
		return inGame;
	}
	
	public void setInGame(boolean b){
		inGame = b;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public Socket getConnection(){
		return connection;
	}
	
	public boolean equals(Player player){
		return (player.getConnection().getInetAddress().equals(connection.getInetAddress()) && player.getConnection().getPort() == connection.getPort() || player.getName().equals(name));
	}
	
}
