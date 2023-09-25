package it.italiandudes.cah.exceptions.connection;

import org.jetbrains.annotations.NotNull;

public final class CommandException extends Exception {
    public CommandException() {
        super();
    }
    public CommandException(@NotNull final String message) {
        super(message);
    }
    public CommandException(@NotNull final String message, @NotNull final Throwable throwable) {
        super(message, throwable);
    }
    public CommandException(@NotNull final Throwable throwable) {
        super(throwable);
    }
}
