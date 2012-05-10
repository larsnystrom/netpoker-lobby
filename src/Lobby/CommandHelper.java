package Lobby;

public class CommandHelper {
	static String[] idleCommands = {"list", "message-all", "help", "join", "create",  "quit"};
	static String[] gameCommands = {"gameinfo", "message-game", "leave"};

	
	public static String availableCommands(){
		StringBuilder sb = new StringBuilder();
		sb.append("Available commands:\n");
		sb.append("\n<Idle commands>:\n");
		for(int i = 0; i < idleCommands.length; i ++){
			sb.append("'"+idleCommands[i] + ":' - ");
			sb.append(commandInfo(idleCommands[i]) + "\n");
		}
		sb.append("\n<Game extra commands> (you need to be in a game to use these)\n");
		for(int i = 0; i < gameCommands.length; i++){
			sb.append("'"+gameCommands[i] + ":' - ");
			sb.append(commandInfo(gameCommands[i]) + "\n");
		}
		sb.append("---------------------------------------- ");
		
		return sb.toString();
	}
	
	public static String commandInfo(String command){
		if(command == null){
			return "No such command";
		}
		String toSend = "";
		if(command.equals(idleCommands[0])){
			toSend = "List all available games";
		}else if(command.equals(idleCommands[1])){
			toSend = "Message all players on the server";
		}else if(command.equals(idleCommands[2])){
			toSend = "List all commands";
		}else if(command.equals(idleCommands[3])){
			toSend = "join <gamename>";
		}else if(command.equals(idleCommands[4])){
			toSend = "Create <gamename>";
		}else if(command.equals(idleCommands[5])){
			toSend = "Quit the program";
		}else if(command.equals(gameCommands[0])){
			toSend = "Some info about the game";
		}else if(command.equals(gameCommands[1])){
			toSend = "Message all players in the game";
		}else if(command.equals(gameCommands[2])){
			toSend = "Leave <gamename>";
		}else{
			toSend = "No such command";
		}

		return toSend;
	}
}
