package netpoker.lobby.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import netpoker.lobby.ClientCommands;
import netpoker.lobby.ServerCommands;

public class PlayerThread extends Thread {
	Socket connection;
	Lobby lobby;
	LobbyBox mailBox;

	public PlayerThread(Socket connection, Lobby lobby, LobbyBox mailBox) {
		this.connection = connection;
		this.lobby = lobby;
		this.mailBox = mailBox;
	}

	public void run() {
		InputStream input;
		try {
			Player player = new Player("new", connection);

			input = connection.getInputStream();
			OutputStream output = connection.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));

			boolean added = false;
			while (!added) {
				String name = reader.readLine();
				System.out.println(name);
				if (!lobby.findPlayer(name)) {
					player.setName(name);
					lobby.addIdlePlayer(player);
					output.write((ClientCommands.INITIALIZE
							+ ClientCommands.SPLITTER
							+ ClientCommands.NAMECONFIRMED
							+ ClientCommands.SPLITTER + name + "\n").getBytes());
					mailBox.write(ClientCommands.CHAT + ClientCommands.SPLITTER
							+ ClientCommands.ALL + ClientCommands.SPLITTER
							+ player.getName() + " joined the lobby\n");
					added = true;
					break;
				} else {
					output.write((ClientCommands.INITIALIZE
							+ ClientCommands.SPLITTER
							+ ClientCommands.NAMETAKEN + "\n").getBytes());
					output.flush();
				}

			}

			output.write((ClientCommands.CHAT + ClientCommands.SPLITTER
					+ ClientCommands.SYSTEM + ClientCommands.SPLITTER + "You joined the server\n")
					.getBytes());
			System.out.println("Initiated player");
			output.flush();
			output.write((ClientCommands.INITIALIZE + ClientCommands.SPLITTER
					+ ClientCommands.LIST + ClientCommands.SPLITTER
					+ lobby.listGames() + "\n").getBytes());
			output.flush();
			String[] commandSequence = new String[1];
			do {
				String line = reader.readLine();
				if (line != null) {
					commandSequence = line.split(ServerCommands.SPLITTER);
					String commandExtra = null;
					if (commandSequence.length > 1) {
						commandExtra = commandSequence[1];
						commandExtra = commandExtra.trim();
					}

					String toSend = null;
					if (commandSequence[0].equals(ServerCommands.LIST)) {
						toSend = ClientCommands.INITIALIZE
								+ ClientCommands.SPLITTER + ClientCommands.LIST
								+ ClientCommands.SPLITTER + lobby.listGames()
								+ "\n";
						System.out.println(toSend);
						output.write(toSend.getBytes());
					} else if (commandSequence[0]
							.equals(ServerCommands.MESSAGEALL)) {
						System.out.println("command: "
								+ ServerCommands.MESSAGEALL);
						toSend = ClientCommands.CHAT + ClientCommands.SPLITTER;
						if (commandExtra != null) {
							toSend = toSend + ClientCommands.ALL
									+ ClientCommands.SPLITTER
									+ player.getName() + ": " + commandExtra
									+ "\n";
							System.out.println(toSend);
							lobby.send(toSend);
						} else {
							toSend = toSend + ClientCommands.SYSTEM
									+ ClientCommands.SPLITTER
									+ "No message typed \n";
							System.out.println(toSend);
							output.write(toSend.getBytes());
						}

					} else if (commandSequence[0].equals(ServerCommands.HELP)) {
						// TODO

						// System.out.println("command: " +
						// ServerCommands.idleCommands[3]);
						// toSend = ServerCommands.availableCommands() + "\n";
						// output.write(toSend.getBytes());
					} else if (commandSequence[0].equals(ServerCommands.JOIN)) {
						if (!player.isInGame()) {
							if (lobby.addPlayerToGame(commandExtra, player)) {
								player.setInGame(true);
								toSend = ClientCommands.GAME
										+ ClientCommands.SPLITTER
										+ ClientCommands.JOIN
										+ ClientCommands.SPLITTER
										+ commandExtra + "\n";
							} else {
								toSend = ClientCommands.CHAT
										+ ClientCommands.SPLITTER
										+ ClientCommands.SYSTEM
										+ ClientCommands.SPLITTER
										+ "Couldn�t find game or game is already full \n";
							}
						} else {
							toSend = ClientCommands.CHAT
									+ ClientCommands.SPLITTER
									+ ClientCommands.SYSTEM
									+ ClientCommands.SPLITTER
									+ "You need to leave the game you are in before joining another one\n";
						}
						System.out.println(toSend);
						output.write(toSend.getBytes());
						if (player.isInGame()) {
							Game game = lobby.findGameFromPlayer(player);
							game.send(ClientCommands.GAME
									+ ClientCommands.SPLITTER
									+ ClientCommands.OTHERJOIN
									+ ClientCommands.SPLITTER
									+ game.getGamename()
									+ ClientCommands.SPLITTER
									+ player.getName() + " joined the game\n");
							System.out.println(ClientCommands.GAME
									+ ClientCommands.SPLITTER
									+ ClientCommands.OTHERJOIN
									+ ClientCommands.SPLITTER
									+ game.getGamename()
									+ ClientCommands.SPLITTER
									+ player.getName() + " joined the game\n");
						}

					} else if (commandSequence[0].equals(ServerCommands.CREATE)) {
						if (!player.isInGame()) {
							if (lobby.findGame(commandExtra) == null
									&& lobby.addGame(new Game(commandExtra,
											player, 4))) {
								player.setInGame(true);
								toSend = ClientCommands.GAME
										+ ClientCommands.SPLITTER
										+ ClientCommands.HOST
										+ ClientCommands.SPLITTER
										+ commandExtra + "\n";
							} else {
								toSend = ClientCommands.CHAT
										+ ClientCommands.SPLITTER
										+ ClientCommands.SYSTEM
										+ ClientCommands.SPLITTER
										+ "Gamename already taken \n";
							}
						} else {
							toSend = ClientCommands.CHAT
									+ ClientCommands.SPLITTER
									+ ClientCommands.SYSTEM
									+ ClientCommands.SPLITTER
									+ "You need to leave the game you are in before creating another one\n";
						}
						System.out.println(toSend);
						output.write(toSend.getBytes());
					} else if (commandSequence[0]
							.equals(ServerCommands.GAMEINFO)) {
						Game game = lobby.findGame(commandExtra);
						if (game != null) {
							toSend = ClientCommands.GAME
									+ ClientCommands.SPLITTER
									+ ClientCommands.INFO
									+ ClientCommands.SPLITTER
									+ game.listFullGame() + "\n";
						} else {
							toSend = ClientCommands.CHAT
									+ ClientCommands.SPLITTER
									+ ClientCommands.SYSTEM
									+ ClientCommands.SPLITTER
									+ "couldn�t find game \n";
						}
						System.out.println(toSend);
						output.write(toSend.getBytes());
					} else if (commandSequence[0]
							.equals(ServerCommands.MESSAGEGAME)) {
						if (player.isInGame()) {
							if (commandExtra != null
									&& commandExtra.charAt(0) != ' ') {
								Game game = lobby.findGameFromPlayer(player);
								if (game != null) {
									toSend = ClientCommands.CHAT
											+ ClientCommands.SPLITTER
											+ ClientCommands.GAMECHAT
											+ ClientCommands.SPLITTER
											+ player.getName() + ": "
											+ commandExtra + "\n";
									System.out.println(toSend);
									game.send(toSend);
								} else {
									toSend = ClientCommands.CHAT
											+ ClientCommands.SPLITTER
											+ ClientCommands.SYSTEM
											+ ClientCommands.SPLITTER
											+ "couldn�t find game \n";
									System.out.println(toSend);
									output.write(toSend.getBytes());
								}
							} else {
								toSend = ClientCommands.CHAT
										+ ClientCommands.SPLITTER
										+ ClientCommands.SYSTEM
										+ ClientCommands.SPLITTER
										+ "No message typed \n";
								System.out.println(toSend);
								output.write(toSend.getBytes());
							}
						} else {
							toSend = ClientCommands.CHAT
									+ ClientCommands.SPLITTER
									+ ClientCommands.SYSTEM
									+ ClientCommands.SPLITTER
									+ "You need to be in a game to use this command \n";
							System.out.println(toSend);
							output.write(toSend.getBytes());
						}

					} else if (commandSequence[0].equals(ServerCommands.LEAVE)) {
						if (player.isInGame()) {
							if (lobby
									.removePlayerFromGame(commandExtra, player)) {
								player.setInGame(false);
								toSend = ClientCommands.GAME
										+ ClientCommands.SPLITTER
										+ ClientCommands.LEAVE
										+ ClientCommands.SPLITTER
										+ commandExtra + "\n";
							} else {
								toSend = ClientCommands.CHAT
										+ ClientCommands.SPLITTER
										+ ClientCommands.SYSTEM
										+ ClientCommands.SPLITTER
										+ "Wrong gamename \n";
							}
						} else {
							toSend = ClientCommands.CHAT
									+ ClientCommands.SPLITTER
									+ ClientCommands.SYSTEM
									+ ClientCommands.SPLITTER
									+ "You need to be in a game to use this command \n";
						}
						System.out.println(toSend);
						output.write(toSend.getBytes());
					} else if (commandSequence[0].equals(ServerCommands.START)) {
						System.out.println("TCP server har fått starttecken");
						String gameName = commandSequence[1];
						int serverPort = Integer.parseInt(commandSequence[2]);
						InetAddress serverAddress = connection.getInetAddress();

						Game game = lobby.findGame(gameName);
						ArrayList<Player> players = game.getPlayers();

						StringBuilder sb = new StringBuilder();
						sb.append(ClientCommands.GAME);
						sb.append(ClientCommands.SPLITTER);
						sb.append(ClientCommands.START);
						sb.append(ClientCommands.SPLITTER);
						sb.append(serverAddress.getHostAddress());
						sb.append(ClientCommands.SPLITTER);
						sb.append(serverPort);

						int i = 0;
						for (Player p : players) {
							sb.append(ClientCommands.SPLITTER);
							sb.append(p.getName());
							i++;
						}

						while (i < 4) {
							sb.append(ClientCommands.SPLITTER);
							sb.append("Bot " + i);
						}
						sb.append("\n");
						game.send(sb.toString());
						

					} else if (commandSequence[0].equals(ServerCommands.SENDGAMEHOST)) {
						System.out.println("Sending client info to UDP server...");
						Game game = lobby.findGameFromPlayer(player);
						game.sendHost(ClientCommands.GAME
								+ ClientCommands.SPLITTER
								+ ClientCommands.PLAYERINFO
								+ ClientCommands.SPLITTER
								+ player.getName()
								+ ClientCommands.SPLITTER
								+ player.getConnection().getInetAddress()
										.getHostAddress()
								+ ClientCommands.SPLITTER + commandExtra + "\n");
						
						
						
					} else {
						toSend = ClientCommands.CHAT + ClientCommands.SPLITTER
								+ ClientCommands.SYSTEM
								+ ClientCommands.SPLITTER
								+ "No such command \n";
						System.out.println(toSend);
						output.write(toSend.getBytes());
					}
					output.flush();

				} else {
					break;
				}

			} while (!commandSequence[0].equals(ServerCommands.QUIT)
					&& player.getConnection().isConnected());
			connection.close();
			if (lobby.removeLostPlayer(player)) {
				player.setInGame(false);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
