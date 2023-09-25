package it.italiandudes.cah.server.commands;

import it.italiandudes.cah.server.utils.CommandManager;
import it.italiandudes.idl.common.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class TesterCommand extends Command {

    // Command Name
    public static final String COMMAND_NAME = "tester";

    // Command Constructor
    public TesterCommand(@NotNull final String[] args) {
        super(args);
    }

    // Command Register
    public static void registerCommand() {
        CommandManager.registerCommand(COMMAND_NAME, TesterCommand.class);
    }

    // Execute Command
    @Override
    public void executeCommand() {
        Logger.log("HELLO WORLD!");
    }

    // Return Command Name
    @Override
    public String toString() {
        return COMMAND_NAME;
    }
}
