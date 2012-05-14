package netpoker.lobby.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.SystemColor;

import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.UIManager;

import netpoker.client.NetpokerClient;
import netpoker.lobby.ClientCommands;
import netpoker.lobby.ServerCommands;
import netpoker.server.ClientInfo;
import netpoker.server.NetpokerServer;

import java.awt.Font;

public class LobbyClientGUI {

	public JFrame frame;
	private JButton joinButton;
	private JButton startButton;
	private JFormattedTextField typedTextField;
	private JButton sendButton;
	private JMenuItem menuCreateGame;
	private JMenuItem menuLeaveGame;
	private JMenuItem menuExit;
	private JList gameList;
	private JTextArea chatTextArea;
	private JTextArea gameInfoTextArea;
	private boolean inGame;
	private boolean isHost;
	private int playersInHostedGame;
	private String gameUserIsIn;
	private String playerName;
	private DefaultListModel listModel;

	private Socket connection;
	private OutputStream output;
	

	private ClientInfo[] clients;
	private int playersSetup = 0;
	private NetpokerServer pokerServer;

	/**
	 * Create the application.
	 */
	public LobbyClientGUI(Socket connection) {
		this.connection = connection;
		try {
			output = connection.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initialize();
		setGUIActions();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		System.out.println("Initialize");
		inGame = false;
		isHost = false;
		playersInHostedGame = 0;
		gameUserIsIn = "";
		playerName = "";

		frame = new JFrame();
		frame.setBounds(100, 100, 500, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(
				UIManager.getColor("Button.background"));
		frame.getContentPane().setLayout(null);

		JPanel lobbyPanel = new JPanel();
		lobbyPanel.setBounds(6, 6, 488, 226);
		lobbyPanel.setBackground(UIManager.getColor("Button.background"));
		lobbyPanel
				.setBorder(new TitledBorder(new EtchedBorder(
						EtchedBorder.LOWERED, null, null), "Lobby",
						TitledBorder.CENTER, TitledBorder.TOP, null, new Color(
								0, 0, 0)));
		frame.getContentPane().add(lobbyPanel);
		lobbyPanel.setLayout(null);

		JLabel lblChat = new JLabel("Game List");
		lblChat.setBounds(20, 28, 70, 16);
		lobbyPanel.add(lblChat);

		JScrollPane gameListScrollPane = new JScrollPane();
		gameListScrollPane.setBounds(9, 46, 229, 174);
		lobbyPanel.add(gameListScrollPane);

		listModel = new DefaultListModel();
		gameList = new JList(listModel);
		gameList.setFont(new Font("Helvetica", Font.PLAIN, 11));
		gameListScrollPane.setViewportView(gameList);
		gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "Chat",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		chatPanel.setBackground(UIManager.getColor("Button.background"));
		chatPanel.setBounds(6, 244, 320, 128);
		frame.getContentPane().add(chatPanel);
		chatPanel.setLayout(null);

		JScrollPane chatScrollPane = new JScrollPane();
		chatScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatScrollPane.setViewportBorder(null);
		chatScrollPane.setBounds(6, 18, 308, 104);
		chatPanel.add(chatScrollPane);

		chatTextArea = new JTextArea();
		chatTextArea.setFont(new Font("Helvetica", Font.PLAIN, 11));
		chatScrollPane.setViewportView(chatTextArea);
		chatTextArea.setLineWrap(true);
		chatTextArea.setEditable(false);

		joinButton = new JButton("Join Game");
		joinButton.setBackground(SystemColor.textHighlight);
		joinButton.setBounds(338, 274, 145, 36);
		frame.getContentPane().add(joinButton);
		joinButton.setEnabled(false);

		startButton = new JButton("Start Game");
		startButton.setBackground(SystemColor.textHighlight);
		startButton.setBounds(338, 314, 145, 36);
		frame.getContentPane().add(startButton);
		startButton.setEnabled(false);

		typedTextField = new JFormattedTextField();
		typedTextField.setBounds(6, 377, 235, 23);
		frame.getContentPane().add(typedTextField);

		sendButton = new JButton("Send");
		sendButton.setBounds(253, 377, 68, 20);
		frame.getContentPane().add(sendButton);
		frame.setBounds(100, 100, 500, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(UIManager.getColor("Button.background"));
		frame.setJMenuBar(menuBar);

		JMenu menu = new JMenu("File");
		menu.setBackground(UIManager.getColor("Button.background"));
		menuBar.add(menu);

		menuCreateGame = new JMenuItem("Create Game");
		menuCreateGame.setOpaque(false);
		menu.add(menuCreateGame);

		menuLeaveGame = new JMenuItem("Leave Game");
		menuLeaveGame.setOpaque(false);
		menu.add(menuLeaveGame);

		JSeparator separator = new JSeparator();
		menu.add(separator);

		menuExit = new JMenuItem("Exit");
		menuExit.setOpaque(false);
		menu.add(menuExit);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(250, 46, 228, 174);
		lobbyPanel.add(scrollPane);

		gameInfoTextArea = new JTextArea();
		gameInfoTextArea.setFont(new Font("Helvetica", Font.PLAIN, 11));
		scrollPane.setViewportView(gameInfoTextArea);
		gameInfoTextArea.setLineWrap(true);
		gameInfoTextArea.setEditable(false);

		JLabel lblGameInfo = new JLabel("Game Info");
		lblGameInfo.setBounds(259, 28, 70, 16);
		lobbyPanel.add(lblGameInfo);
	}

	private void setGUIActions() {
		System.out.println("SetGUIActions");
		gameList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {

					if (gameList.getSelectedIndex() == -1) {
						joinButton.setEnabled(false);

					} else {
						if (!inGame) {
							joinButton.setEnabled(true);
						}
						getGameInfo((String) listModel.elementAt(gameList
								.getSelectedIndex()));

					}
				}
			}
		});

		joinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("join game clicked");
				String game = (String) gameList.getSelectedValue();
				if (game != null) {
					String action = ServerCommands.JOIN
							+ ServerCommands.SPLITTER + game + "\n";
					invokeAction(action);
				}

			}
		});

		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("start game clicked");

				pokerServer = new NetpokerServer();
				int port = pokerServer.getPort();

				clients = new ClientInfo[4];
				for (int i = 4; i > playersInHostedGame; i--) {
					clients[i] = new ClientInfo("Bot " + i, connection
							.getLocalAddress(), connection.getLocalPort());
				}

				String action = ServerCommands.START + ServerCommands.SPLITTER
						+ gameUserIsIn + ServerCommands.SPLITTER + port + "\n";
				
				System.out.println(action);
				
				invokeAction(action);
				System.out.println("UDP Server has sent it's port");
			}
		});

		typedTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					sendText();
				}
			}
		});

		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				sendText();
			}
		});

		menuCreateGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String gameName = JOptionPane
						.showInputDialog("Enter game name");
				String action = ServerCommands.CREATE + ServerCommands.SPLITTER
						+ gameName + "\n";
				invokeAction(action);
			}

		});

		menuLeaveGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (inGame) {
					String action = ServerCommands.LEAVE
							+ ServerCommands.SPLITTER + gameUserIsIn + "\n";
					invokeAction(action);
				}
			}
		});

		menuExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String action = ServerCommands.QUIT + "\n";
				invokeAction(action);
				System.exit(1);
			}
		});
	}

	private void getGameInfo(String gameName) {
		String action = ServerCommands.GAMEINFO + ServerCommands.SPLITTER
				+ gameName + "\n";
		invokeAction(action);
	}

	public void invokeAction(final String action) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (action != null && action.length() > 0) {
					if (connection.isConnected()) {
						try {
							output.write(action.getBytes());
							output.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						chatTextArea.append("Connection broken");
					}

				}
			}
		});
	}

	private void sendText() {
		if (typedTextField.getText().length() == 0) {
			chatTextArea.append("You need to type something before sending");
		} else if (typedTextField.getText().charAt(0) != ' ') {
			String action = null;
			if (inGame) {
				action = ServerCommands.MESSAGEGAME + ServerCommands.SPLITTER;
			} else {
				action = ServerCommands.MESSAGEALL + ServerCommands.SPLITTER;
			}

			action = action + typedTextField.getText() + "\n";
			typedTextField.setText("");
			invokeAction(action);

		}
	}

	public String updateGUI(String action) {
		String[] actionSequence = action.split(ClientCommands.SPLITTER);
		if (actionSequence[0].equals(ClientCommands.CHAT)) {
			updateChatArea(actionSequence);
		} else if (actionSequence[0].equals(ClientCommands.GAME)) {
			updateGameData(actionSequence);
		} else if (actionSequence[0].equals(ClientCommands.INITIALIZE)) {
			updateInitializeData(actionSequence);
		}
		return action;
	}

	private void updateGameData(String[] actionSequence) {
		if (actionSequence[1].equals(ClientCommands.ADD)) {
			listModel.addElement(actionSequence[2]);
		} else if (actionSequence[1].equals(ClientCommands.REMOVAL)) {
			if (listModel.contains(actionSequence[2])) {
				if (gameList.getSelectedIndex() != -1
						&& gameList.getSelectedIndex() == listModel
								.indexOf(actionSequence[2])) {
					gameInfoTextArea.setText("");
				}
				listModel.removeElement(actionSequence[2]);
			}
		} else if (actionSequence[1].equals(ClientCommands.JOIN)) {
			joinGame(actionSequence[2]);
		} else if (actionSequence[1].equals(ClientCommands.HOST)) {
			hostGame(actionSequence[2]);
		} else if (actionSequence[1].equals(ClientCommands.LEAVE)) {
			leaveGame(actionSequence[2]);
		} else if (actionSequence[1].equals(ClientCommands.JOIN)) {
			joinGame(actionSequence[2]);
		} else if (actionSequence[1].equals(ClientCommands.PLAYERINFO)) {
			System.out.println("Receiving username, address and port for client");
			String playerName = actionSequence[2];
			InetAddress clientAddress = null;
			try {
				clientAddress = InetAddress.getByName(actionSequence[3]);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int clientPort = Integer.parseInt(actionSequence[4]);

			clients[playersSetup] = new ClientInfo(playerName, clientAddress,
					clientPort);
			playersSetup++;
			
			if (playersSetup == playersInHostedGame) {
				pokerServer.startGame(clients);
			}
			
			System.out.println("We have " + playersSetup + " nbr of players setup");
			
		} else if (actionSequence[1].equals(ClientCommands.INFO)) {
			setGameInfo(actionSequence[2]);
		} else if (actionSequence[1].equals(ClientCommands.OTHERJOIN)) {
			getGameInfo(actionSequence[2]);
			if (isHost) {
				playersInHostedGame++;
				System.out.println("Spelare i spelet: "
						+ Integer.toString(playersInHostedGame));
				if (playersInHostedGame == 4) {
					startButton.setEnabled(true);
				}
			} else {
				System.out.println("Inte host");
			}
			chatTextArea.append("\n(" + gameUserIsIn + ")" + actionSequence[3]);
			chatTextArea.setCaretPosition(chatTextArea.getDocument()
					.getLength());

		} else if (actionSequence[1].equals(ClientCommands.OTHERLEAVE)) {
			getGameInfo(actionSequence[2]);
			if (isHost) {
				playersInHostedGame--;
				startButton.setEnabled(false);
			}
			chatTextArea.append(actionSequence[2] + ": " + actionSequence[3]);
		} else if (actionSequence[1].equals(ClientCommands.START)) {
			System.out.println("start game received, starting client...");
			InetAddress serverAddress = null;
			try {
				serverAddress = InetAddress.getByName(actionSequence[2]);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int serverPort = Integer.parseInt(actionSequence[3]);

			String[] playerNames = new String[4];
			int i;
			for (i = 4; i < actionSequence.length; i++) {
				playerNames[i - 4] = actionSequence[i];
			}

			if (i < 8) {
				System.out.println("Not enough players...");
			}

			NetpokerClient pokerClient = new NetpokerClient(playerName, playerNames,
					serverAddress, serverPort);

			int clientPort = pokerClient.getPortAddress();

			String action = ServerCommands.SENDGAMEHOST
					+ ServerCommands.SPLITTER + Integer.toString(clientPort)
					+ "\n";
			invokeAction(action);
			
			System.out.println("Client started, sending port to UDP server");

			// if (isHost && playersInHostedGame == 4) {
			// System.out.println("Startbutton clicked");
			// // Add Command Chain for starting a game
			// // invokeAction(action);
			// }

		}
	}

	private void updateChatArea(String[] actionSequence) {
		String message = "";
		if (actionSequence[1].equals(ClientCommands.ALL)) {
			message = "(Lobby)";
		} else if (actionSequence[1].equals(ClientCommands.SYSTEM)) {
			message = "(System)";
		} else if (actionSequence[1].equals(ClientCommands.GAMECHAT)) {
			message = "(" + gameUserIsIn + ")";
		}
		message = message + actionSequence[2];
		if (chatTextArea.getText().length() != 0) {
			message = "\n" + message;
		}
		chatTextArea.append(message);
		chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
	}

	private void updateInitializeData(String[] actionSequence) {
		if (actionSequence[1].equals(ClientCommands.NAMETAKEN)) {
			String line = "Name already taken, choose another one";
			if (chatTextArea.getText().length() != 0) {
				line = "\n" + line;
			}
			chatTextArea.append(line);
			setPlayerName();
		} else if (actionSequence[1].equals(ClientCommands.LIST)) {
			if (actionSequence.length > 2) {
				String[] games = actionSequence[2]
						.split(ClientCommands.STRINGSPLITTER);

				listModel.removeAllElements();
				for (int i = 0; i < games.length; i++) {
					listModel.addElement(games[i]);
				}
			}

		} else if (actionSequence[1].equals(ClientCommands.NAMECONFIRMED)) {
			playerName = actionSequence[2];
		}
	}

	private void joinGame(String gameName) {
		joinButton.setEnabled(false);
		getGameInfo(gameName);
		gameUserIsIn = gameName;
		inGame = true;
		isHost = false;
	}

	private void leaveGame(String gameName) {
		joinButton.setEnabled(true);
		if (gameList.getSelectedIndex() != -1
				&& gameList.getSelectedIndex() == listModel.indexOf(gameName)) {
			gameInfoTextArea.setText("");
		}
		gameInfoTextArea.setText("");

		gameUserIsIn = "";
		inGame = false;
		isHost = false;
	}

	private void hostGame(String gameName) {
		joinButton.setEnabled(false);
		getGameInfo(gameName);
		gameUserIsIn = gameName;
		playersInHostedGame = 1;
		inGame = true;
		isHost = true;
	}

	private void hostGone(String gameName) {
		joinButton.setEnabled(true);
		setGameInfo("");
		gameUserIsIn = "";
		inGame = false;
		isHost = false;
	}

	private void setGameInfo(String info) {
		String[] gameInfo = info.split(ClientCommands.STRINGSPLITTER);
		System.out.println(ClientCommands.STRINGSPLITTER);
		gameInfoTextArea.setText("");
		for (int i = 0; i < gameInfo.length; i++) {
			if (i == 0) {
				gameInfoTextArea.append(gameInfo[i]);
			} else {
				gameInfoTextArea.append("\n" + gameInfo[i]);
			}
		}

	}

	public void setPlayerName() {
		String name = null;
		while (name == null || name.length() == 0 || name.charAt(0) == ' ') {
			name = JOptionPane.showInputDialog("Enter player name");
		}

		if (connection.isConnected()) {
			try {
				output.write((name + "\n").getBytes());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			chatTextArea.append("Connection Broken");
		}

	}
}
