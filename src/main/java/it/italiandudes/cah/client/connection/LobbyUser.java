package it.italiandudes.cah.client.connection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class LobbyUser {

    // Attributes
    @NotNull private final StringProperty username;
    @NotNull private final BooleanProperty ready;

    // Constructors
    public LobbyUser(@NotNull final String username) {
        this.username = new SimpleStringProperty(username);
        this.ready = new SimpleBooleanProperty(false);
    }
    public LobbyUser(@NotNull final String username, final boolean ready) {
        this.username = new SimpleStringProperty(username);
        this.ready = new SimpleBooleanProperty(ready);
    }

    // Methods
    @NotNull
    public BooleanProperty isReadyProperty() {
        return ready;
    }
    @NotNull
    public String getUsername() {
        return username.getValue();
    }
    public boolean isReady() {
        return ready.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LobbyUser)) return false;
        LobbyUser lobbyUser = (LobbyUser) o;
        return Objects.equals(getUsername(), lobbyUser.getUsername());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
    @Override
    public String toString() {
        return username.getValue();
    }
}
