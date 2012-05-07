package Lobby;
import java.io.IOException;
import java.io.OutputStream;


public class ReaderThread extends Thread{
	private MailBox mailBox;
	private Lobby lobby;
	public ReaderThread(MailBox mailBox, Lobby lobby ){
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
