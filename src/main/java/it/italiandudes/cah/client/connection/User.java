package it.italiandudes.cah.client.connection;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;
import java.util.Objects;

public class User {

    // Attributes
    @NotNull private final Socket connection;
    @NotNull private final String username;

    // Constructors
    public User(@NotNull final Socket connection, @NotNull final String username) {
        if (connection.isClosed()) throw new IllegalArgumentException("The socket is closed!");
        this.connection = connection;
        this.username = username;
    }

    // Methods
    @NotNull
    public Socket getConnection() {
        return connection;
    }
    @NotNull
    public String getUsername() {
        return username;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getConnection(), user.getConnection()) && Objects.equals(getUsername(), user.getUsername());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getConnection(), getUsername());
    }
    @Override
    public String toString() {
        return username;
    }
}
