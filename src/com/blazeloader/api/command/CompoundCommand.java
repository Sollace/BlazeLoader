package com.blazeloader.api.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

/**
 * A special type of command that encapsulates a private set of commands and its own /help.
 */
public abstract class CompoundCommand extends BLCommandBase {

	private CommandHandler handler = new CommandHandler();
	private final String name;
	
	public CompoundCommand(String nm, boolean includeHelp, boolean autoRegister) {
		super(autoRegister);
		name = nm;
		if (includeHelp) {
			registerCommand(new CommandPrivateHelp(handler));
		}
	}
	
	public String getCommandName() {
		return name;
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " /{command} {argumants}";
	}

	public void processCommand(ICommandSender sender, String[] args) throws WrongUsageException {
		if (args.length > 0) {
			String commandName = args[0];
			String raw = "";
			for (int i = 0; i < args.length; i++) {
				if (raw != "") raw += " ";
				raw += args[i];
			}
			handler.executeCommand(sender, raw);
		} else {
			throw new WrongUsageException(getCommandUsage(sender));
		}
	}
	
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
    	if (args.length < 2) {
	    	if (args.length == 0) {
	    		List<ICommand> commands = handler.getPossibleCommands(sender);
	    		Collections.sort(commands);
		    	List<String> result = new ArrayList<String>();
		    	for (ICommand i : commands) {
		    		result.add("/" + i.getCommandName());
		    	}
		        return result;
	    	} else {
	    		for (int i = 0; i < args.length; i++) args[i] = args[i].replace("/", "");
	    		return handler.getTabCompletionOptions(sender, args[0], pos);
	    	}
	    	
    	}
    	
		Map<String, ICommand> commands = getCommands();
		ICommand command = getCommands().get(args[0].substring(1));
		if (command != null && command.canCommandSenderUseCommand(sender)) {
			return command.addTabCompletionOptions(sender, dropFirstString(args), pos);
		}
    	return null;
    }
	
	/**
	 * Executes one of the contained commands with a raw input string.
	 * 
	 * @param sender		Entity executing this command
	 * @param rawCommand	Command string to execute.
	 * 
	 * @return	Number of executions.
	 */
	public int executeCommand(ICommandSender sender, String rawCommand) {
		return handler.executeCommand(sender, rawCommand);
	}
	
	/**
	 * Returns all the commands from this CompoundCommand that a given sender can use. 
	 * @param sender	An entity that can execute commands
	 * @return	List of possible commands.
	 */
	public List<ICommand> getPossibleCommands(ICommandSender sender) {
		return handler.getPossibleCommands(sender);
	}
	
    /**
     * returns a map of string to commads. All commands are returned, not just ones which someone has permission to use.
     */
    public Map<String, ICommand> getCommands() {
        return handler.getCommands();
    }
	
    /**
     * Registers a full set of new commands to this collection.
     */
	public void registerCommands(ICommand... commands) {
		for (ICommand i : commands) {
			registerCommand(i);
		}
	}
    
    /**
     * Register a new command to this collection.
     * @return The command object that has been registered.
     */
	public ICommand registerCommand(ICommand command) {
		return handler.registerCommand(command);
	}
}