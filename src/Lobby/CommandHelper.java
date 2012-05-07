package Lobby;

public class CommandHelper {
	static String[] commands = {"L:", "MA:", "MG:", "help", "Join", "Q"};

	public CommandHelper(){
		
	}
	
	public static String availableCommands(){
		StringBuilder sb = new StringBuilder();
		sb.append("Available commands:\n");
		for(int i = 0; i < commands.length; i ++){
			sb.append(commands[i] + ": ");
			sb.append(commandInfo(commands[i]) + "\n");
		}		
		return sb.toString();
	}
	
	public static String commandInfo(String command){
		if(command == null){
			return "No such command";
		}
		int i;
		for(i = 0; i < commands.length; i++){
			if(command.equals(commands[i])){
				break;
			}
		}
		String s = "";
		switch(i){
		case(0):
			s = "List all available games";
			break;
		case(1):
			s = "Message all players in the lobby";
			break;
		case(2):
			s = "Message all players in a game";
			break;
		case(3):
			s = "List all commands";
			break;
		case(4):
			s = "Join <gamename>";
			break;
		case(5):
			s = "Quit";
		default:
			s = "No such command";	
		}
		
		return s;
	}
}
