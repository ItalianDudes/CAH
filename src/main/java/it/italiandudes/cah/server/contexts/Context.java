package it.italiandudes.cah.server.contexts;

public abstract class Context extends Thread {

    // Constructor
    public Context() {
        this.setDaemon(true);
    }

    // Runnable
    public abstract void run();
}
