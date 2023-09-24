package it.italiandudes.cah.client.connection;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;
import java.util.Objects;

public final class User {

    // Attributes
    @NotNull private final Socket mainConnection;
    @NotNull private final Socket serviceConnection;
    @NotNull private final String username;

    // Constructors
    public User(@NotNull final Socket mainConnection, @NotNull final Socket serviceConnection, @NotNull final String username) {
        if (mainConnection.isClosed()) throw new IllegalArgumentException("The main socket is closed!");
        if (serviceConnection.isClosed()) throw new IllegalArgumentException("The service socket is closed!");
        this.mainConnection = mainConnection;
        this.serviceConnection = serviceConnection;
        this.username = username;
    }

    // Methods
    @NotNull
    public Socket getMainConnection() {
        return mainConnection;
    }
    @NotNull
    public Socket getServiceConnection() {
        return serviceConnection;
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
        return Objects.equals(getMainConnection(), user.getMainConnection()) && Objects.equals(getServiceConnection(), user.getServiceConnection()) && Objects.equals(getUsername(), user.getUsername());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getMainConnection(), getServiceConnection(), getUsername());
    }

    @Override
    public String toString() {
        return username;
    }
}
