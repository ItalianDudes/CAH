package it.italiandudes.cah;

import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.server.Server;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import it.italiandudes.idl.common.InfoFlags;
import it.italiandudes.idl.common.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;

public final class CAH {

    // Main Method
    public static void main(String[] args) {

        // Initializing the logger
        try {
            Logger.init();
        } catch (IOException e) {
            Logger.log("An error has occurred during Logger initialization, exit...");
        }

        // Configure the shutdown hooks
        Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));
        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRichPresenceManager::shutdownRichPresence));

        if (Arrays.stream(args).anyMatch(Predicate.isEqual(Defs.ProgramArguments.SERVER))) { // Start the server
            Server.start();
        } else { // Start the client
            try {
                Client.start(args);
            } catch (NoClassDefFoundError e) {
                Logger.log("ERROR: TO RUN THIS JAR YOU NEED JAVA 8 WITH BUILT-IN JAVAFX!", new InfoFlags(true, true, true, true));
                Logger.log(e);
                System.exit(0);
            }
        }
    }
}
