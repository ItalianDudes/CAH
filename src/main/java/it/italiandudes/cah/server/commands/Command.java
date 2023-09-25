package it.italiandudes.cah.server.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public abstract class Command {

    // Attributes
    @Nullable protected final String[] args;

    // Constructors
    protected Command(@NotNull final String[] args) {
        this.args = args.clone();
    }

    // Methods
    public abstract void executeCommand();
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Command)) return false;
        Command command = (Command) o;
        return Arrays.equals(args, command.args);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(args);
    }
    public abstract String toString();
}
