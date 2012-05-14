package netpoker.lobby.server;



public class ReaderThread extends Thread{
	private LobbyBox mailBox;
	private Lobby lobby;
	public ReaderThread(LobbyBox mailBox, Lobby lobby ){
		this.mailBox = mailBox;
		this.lobby = lobby;
	}
	
	public void run(){
		while(true){
			String message = mailBox.clear();
			lobby.send(message);
		}
		
	}

}
