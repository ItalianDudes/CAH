package it.italiandudes.cah.server.contexts;

public class ContextLobby extends Context {

    // Instance
    private static ContextLobby CONTEXT = null;

    // Attributes

    // Constructors
    private ContextLobby() {
        setDaemon(true);
    }

    // Get Instance
    public static ContextLobby getInstance() {
        if (CONTEXT != null) return CONTEXT;
        CONTEXT = new ContextLobby();
        CONTEXT.start();
        return CONTEXT;
    }

    // Runnable
    @Override
    public void run() {
        // TODO: implement ContextLobby runnable
    }
}
