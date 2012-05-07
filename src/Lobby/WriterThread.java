package Lobby;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class WriterThread extends Thread {
	private Socket connection;
	
	public WriterThread(Socket connection) {
		this.connection = connection;
	}
	
	public void run(){
		InputStream input = System.in;
		OutputStream output;
		try {
			output = connection.getOutputStream();
			int i = input.read();
			while (i != -1 && connection.isConnected()) {
				output.write((char) i);
				output.flush();
				i = input.read();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
