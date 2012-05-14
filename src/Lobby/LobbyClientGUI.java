package Lobby;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JToolBar;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.JLayeredPane;
import javax.swing.border.TitledBorder;
import javax.swing.JTextPane;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;
import java.awt.SystemColor;

import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListModel;
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
import java.net.Socket;

import javax.swing.DropMode;
import javax.swing.AbstractListModel;
import javax.swing.UIManager;

public class LobbyClientGUI {

	public JFrame frame;
	private JButton joinButton;
	private JButton startButton;
	private JFormattedTextField typedTextField;
	private JButton sendButton;
	private JMenuItem menuCreateGame;
	private JMenuItem menuLeaveGame;
	private JMenuItem menuExit;
	private JButton updateButton;
	private JList gameList;
	private JTextArea chatTextArea;
	private JTextArea gameInfoTextArea;
	private boolean inGame;
	private boolean isHost;
	private int playersInHostedGame;
	private String gameUserIsIn;
	private DefaultListModel listModel;
	
	
	private Socket connection;
	private OutputStream output;

	/**
	 * Create the application.
	 */
	public LobbyClientGUI(String baseState, Socket connection) {
		this.connection = connection;		
		try {
			output = connection.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initialize();
		setGUIActions();
		setPlayerName();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		System.out.println("Initialize");
		frame = new JFrame();
		frame.getContentPane().setBackground(UIManager.getColor("Button.background"));
		frame.getContentPane().setLayout(null);
		
		JPanel lobbyPanel = new JPanel();
		lobbyPanel.setBounds(6, 6, 488, 226);
		lobbyPanel.setBackground(UIManager.getColor("Button.background"));
		lobbyPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Lobby", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		frame.getContentPane().add(lobbyPanel);
		lobbyPanel.setLayout(null);
		lobbyPanel.setVisible(true);
		
		JLabel lblChat = new JLabel("Game List");
		lblChat.setBounds(20, 28, 70, 16);
		lobbyPanel.add(lblChat);
		
		JScrollPane gameListScrollPane = new JScrollPane();
		gameListScrollPane.setBounds(9, 46, 229, 154);
		lobbyPanel.add(gameListScrollPane);
		
		listModel = new DefaultListModel();
		gameList = new JList(listModel);
		gameListScrollPane.setViewportView(gameList);
		gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		chatPanel.setBackground(UIManager.getColor("Button.background"));
		chatPanel.setBounds(6, 244, 320, 128);
		frame.getContentPane().add(chatPanel);
		chatPanel.setLayout(null);
		
		JScrollPane chatScrollPane = new JScrollPane();
		chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatScrollPane.setViewportBorder(null);
		chatScrollPane.setBounds(6, 18, 308, 104);
		chatPanel.add(chatScrollPane);
		
		chatTextArea = new JTextArea();
		chatScrollPane.setViewportView(chatTextArea);
		
		joinButton = new JButton("Join Game");
		joinButton.setBackground(SystemColor.textHighlight);
		joinButton.setBounds(338, 274, 145, 36);
		frame.getContentPane().add(joinButton);
		
		startButton = new JButton("Start Game");
		startButton.setBackground(SystemColor.textHighlight);
		startButton.setBounds(338, 314, 145, 36);
		frame.getContentPane().add(startButton);
		
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
		scrollPane.setViewportView(gameInfoTextArea);
		
		JLabel lblGameInfo = new JLabel("Game Info");
		lblGameInfo.setBounds(259, 28, 70, 16);
		lobbyPanel.add(lblGameInfo);
		
		updateButton = new JButton("Update");
		updateButton.setBounds(139, 201, 88, 19);
		lobbyPanel.add(updateButton);
	}
	
	private void setGUIActions(){
		System.out.println("SetGUIActions");
		gameList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {

			        if (gameList.getSelectedIndex() == -1) {
			        	joinButton.setEnabled(true);

			        } else {
			        	joinButton.setEnabled(false);
			        }
			    }
			}
		});
		
		
		joinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String game = (String) gameList.getSelectedValue();
				if(game != null){
					String action = ServerCommands.JOIN + ServerCommands.SPLITTER + game + "\n";
					invokeAction(action);
				}
				
			}
		});
		
	
		typedTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ENTER){
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

		menuCreateGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String gameName = JOptionPane.showInputDialog("Enter game name");
				String action = ServerCommands.CREATE + ServerCommands.SPLITTER + gameName + "\n";
				invokeAction(action);
			}
		});

		menuLeaveGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(inGame){
					String action = ServerCommands.LEAVE + ServerCommands.SPLITTER + gameUserIsIn + "\n";
					invokeAction(action);
				}
			}
		});
		menuExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String action = ServerCommands.QUIT + "\n";
				invokeAction(action);
			}
		});
	}
	
	private void getGameInfo(String gameName){
		String action = ServerCommands.GAMEINFO + ServerCommands.SPLITTER + gameName + "\n";
		invokeAction(action);
	}
	
	public void invokeAction(String action){
		if(action != null && action.length() > 0){
			if(connection.isConnected()){
				try {
					output.write(action.getBytes());
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				chatTextArea.append("Connection broken");
			}

			
		}
	}
	

	
	private void sendText(){
		if(typedTextField.getText().length() == 0){
			chatTextArea.append("You need to type something before sending");
		}else if(typedTextField.getText().charAt(0) != ' '){
			String action = null;
			if(inGame){
				action = ServerCommands.MESSAGEGAME + ServerCommands.SPLITTER;
			}else{
				action =ServerCommands.MESSAGEALL + ServerCommands.SPLITTER;
			}
			
			action = action + typedTextField.getText() + "\n";
			invokeAction(action);
			
		}
	}
	
	public synchronized String updateGUI(String action){
		String[] actionSequence = action.split(ClientCommands.SPLITTER);
		if(actionSequence[0].equals(ClientCommands.CHAT)){
			updateChatArea(actionSequence);
		}else if(actionSequence[0].equals(ClientCommands.GAME)){
			updateGameData(actionSequence);
		}else if(actionSequence[0].equals(ClientCommands.INITIALIZE)){
			updateInitializeData(actionSequence);
		}
		notifyAll();
		return action;
	}

	private void updateGameData(String[] actionSequence) {
		if(actionSequence[1].equals(ClientCommands.ADD)){
			listModel.addElement(actionSequence[2]);
		}else if(actionSequence[1].equals(ClientCommands.REMOVAL)){
			if(listModel.contains(actionSequence[2])){
				listModel.removeElement(actionSequence[2]);
			}
		}else if(actionSequence[1].equals(ClientCommands.JOIN)){
			joinGame(actionSequence[2]);			
		}else if(actionSequence[1].equals(ClientCommands.HOST)){
			hostGame(actionSequence[2]);
		}else if(actionSequence[1].equals(ClientCommands.LEAVE)){
			leaveGame(actionSequence[2]);
		}else if(actionSequence[1].equals(ClientCommands.HOSTGONE)){
			hostGone(actionSequence[2]);
		}else if(actionSequence[1].equals(ClientCommands.INFO)){
			setGameInfo(actionSequence[2]);
		}else if(actionSequence[1].equals(ClientCommands.OTHERJOIN)){
			getGameInfo(actionSequence[2]);
			if(isHost){
				int playersInGame = addPlayerToGame();
				if(playersInGame == 4){
					startButton.setEnabled(true);
				}
			}
			chatTextArea.append(actionSequence[2] + ": " + actionSequence[3]);
			
		}else if(actionSequence[1].equals(ClientCommands.OTHERLEAVE)){
			getGameInfo(actionSequence[2]);
			if(isHost){
				startButton.setEnabled(false);
			}
			chatTextArea.append(actionSequence[2] + ": " + actionSequence[3]);
		}else if(actionSequence[1].equals(ClientCommands.START)){
			if(isHost && playersInHostedGame == 4){
				String action = "message-game: Game starting in 10 Seconds";
				invokeAction(action);
			}
			
		}		
	}
	
	private void updateChatArea(String[] actionSequence) {
		String message = "";
		if(actionSequence[1].equals(ClientCommands.ALL)){
			message = "(Lobby)";
		}else if(actionSequence[1].equals(ClientCommands.SYSTEM)){
			message = "(System)";
		}else if(actionSequence[1].equals(ClientCommands.GAMECHAT)){
			message = "(" + gameUserIsIn + ")";
		}
		message = message + actionSequence[2];
		chatTextArea.append(message);
	}
	
	private void updateInitializeData(String[] actionSequence) {
		if(actionSequence[1].equals(ClientCommands.NAMETAKEN)){
			chatTextArea.append("Name already taken, choose another one");
			setPlayerName();
		}else if(actionSequence[1].equals(ClientCommands.LIST)){
			String[] games = actionSequence[2].split("\n");
			listModel.removeAllElements();
			for(int i = 0; i < games.length; i++){
				listModel.addElement(games[i]);
			}
		}
	}
	
	private void joinGame(String gameName){
		joinButton.setEnabled(false);
		getGameInfo(gameName);
		gameUserIsIn = gameName;
		inGame = true;
		isHost = false;
	}
	
	private void leaveGame(String gameName){
		joinButton.setEnabled(true);
		getGameInfo(gameName);
		gameUserIsIn = "";
		inGame = false;
		isHost = false;
	}
	
	private void hostGame(String gameName){
		joinButton.setEnabled(false);
		getGameInfo(gameName);
		gameUserIsIn = gameName;
		inGame = true;
		isHost = true;
	}
	
	private void hostGone(String gameName){
		joinButton.setEnabled(true);
		setGameInfo("");
		gameUserIsIn = "";
		inGame = false;
		isHost = false;
	}
	
	private int addPlayerToGame(){
		return playersInHostedGame++;
	}
	
	private int removePlayerFromGame(){
		return playersInHostedGame--;
	}
	
	private void setGameInfo(String info){
		gameInfoTextArea.setText(info);
	}
	
	private void setPlayerName(){
		String name = "bagge";
//		while (name == null || name.length() == 0 || name.charAt(0) == ' '){
//			name = JOptionPane.showInputDialog("Enter player name");
//		}
		
		if(connection.isConnected()){
			try {
				output.write(name.getBytes());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			chatTextArea.append("Connection Broken");
		}

		
	}


}
