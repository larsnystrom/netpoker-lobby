package Lobby;

import java.net.Socket;

public class Player {
	private String name;
	private Socket connection;
	
	public Player(String name, Socket connection){
		this.name = name;
		this.connection = connection;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public Socket getConnection(){
		return connection;
	}
	
	public boolean equals(Player player){
		return (player.getConnection().getInetAddress().equals(connection.getInetAddress()) && player.getConnection().getPort() == connection.getPort());
	}
	
}
