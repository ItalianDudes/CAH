package it.italiandudes.cah.server.connection;

import it.italiandudes.cah.server.contexts.Context;
import it.italiandudes.cah.server.contexts.ContextLobby;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class ContextManager {

    // Attributes
    private static final HashMap<String, Context> USER_CONTEXT_MAP = new HashMap<>();

    // Methods
    public static Context getUserContextByUsername(@NotNull final String username) {
        if (USER_CONTEXT_MAP.containsKey(username)) return USER_CONTEXT_MAP.get(username);
        return null;
    }
    public static void addUserToLobby(@NotNull final String username) {
        if (USER_CONTEXT_MAP.containsKey(username)) return;
        USER_CONTEXT_MAP.put(username, ContextLobby.getInstance());
    }
}
