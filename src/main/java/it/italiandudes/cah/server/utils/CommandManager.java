package it.italiandudes.cah.server.utils;

import it.italiandudes.cah.exceptions.connection.CommandException;
import it.italiandudes.cah.server.commands.Command;
import it.italiandudes.cah.server.commands.TesterCommand;
import it.italiandudes.idl.common.StringHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

public final class CommandManager {

    // Command Registry
    public static final HashMap<String, Class<? extends Command>> COMMANDS = new HashMap<>();

    // Command Parser
    public static Command parseCommand(@NotNull final String input) throws CommandException {
        String[] splitInput = StringHandler.parseString(input);
        String command = splitInput[0];
        String[] commandArgs;
        if (splitInput.length > 1) {
            commandArgs = Arrays.copyOfRange(splitInput, 1, splitInput.length);
        } else {
            commandArgs = new String[0];
        }
        return recognizeCommand(command, commandArgs);
    }
    private static Command recognizeCommand(@NotNull final String commandName, @NotNull final String[] args) throws CommandException {
        Class<? extends Command> command = COMMANDS.get(commandName);
        if (command == null) return null;
        try {
            return COMMANDS.get(commandName).getConstructor(String[].class).newInstance((Object) args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CommandException(e);
        }
    }

    // Methods
    public static void registerCommand(@NotNull final String commandName, @NotNull final Class<? extends Command> commandClass) {
        if (!COMMANDS.containsKey(commandName)) COMMANDS.put(commandName, commandClass);
    }
    public static void registerAllCommands() {
        TesterCommand.registerCommand();
    }
}
