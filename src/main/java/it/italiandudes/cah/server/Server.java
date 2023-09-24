package it.italiandudes.cah.server;

import it.italiandudes.cah.server.util.ServerDefs;
import it.italiandudes.idl.common.Logger;

import java.util.Scanner;

public final class Server {

    public static void start() {
        // TODO: Load server settings
        // TODO: Open DB Connection
        // TODO: Open Socket Listener

        Scanner scan = new Scanner(System.in);
        String buffer;
        while (true) {
            buffer = scan.nextLine();
            if (buffer.equals(ServerDefs.CommandInput.STOP)) break;
            // TODO: parse commands
            Logger.log(buffer);
        }

        // TODO: Close Socket Listener
        // TODO: Close open connections
        // TODO: Close DB Connection
    }

}
