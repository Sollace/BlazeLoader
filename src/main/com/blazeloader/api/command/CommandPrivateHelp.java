package com.blazeloader.api.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommandSender;

public class CommandPrivateHelp extends CommandHelp {
	
	private final CommandHandler handler;
	
	public CommandPrivateHelp(CommandHandler commands) {
		super();
		handler = commands;
	}
	
    protected List getSortedPossibleCommands(ICommandSender par1ICommandSender) {
        List var2 = handler.getPossibleCommands(par1ICommandSender);
        Collections.sort(var2);
        return var2;
    }

    protected Map getCommands() {
        return handler.getCommands();
    }
}
