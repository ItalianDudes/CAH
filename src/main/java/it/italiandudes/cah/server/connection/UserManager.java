package it.italiandudes.cah.server.connection;

import it.italiandudes.cah.client.connection.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;

public final class UserManager {

    // Attributes
    private static final HashMap<String, User> USER_MAP = new HashMap<>();

    // Methods
    @Nullable
    public static User getUserByUsername(@NotNull final String username) {
        return USER_MAP.get(username);
    }
    public static boolean insertNewUser(@NotNull final User user) {
        if (getUserByUsername(user.getUsername()) != null) return false;
        USER_MAP.put(user.getUsername(), user);
        return true;
    }
    public static void removeUserByUsername(@NotNull final String username) {
        User user = getUserByUsername(username);
        if (user == null) return;
        try {
            user.getMainConnection().close();
        } catch (IOException ignored) {}
        try {
            user.getServiceConnection().close();
        } catch (IOException ignored) {}
        USER_MAP.remove(username);
    }
    public static int getConnectedUsers() {
        return USER_MAP.size();
    }
    public static boolean containsUserByUsername(@NotNull final String username) {
        return getUserByUsername(username) != null;
    }
    public static void removeAllUsers() {
        for (String username : USER_MAP.keySet()) {
            removeUserByUsername(username);
        }
    }
}
