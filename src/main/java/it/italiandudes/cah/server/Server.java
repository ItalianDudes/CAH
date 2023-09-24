package it.italiandudes.cah.server;

import it.italiandudes.cah.server.connection.ConnectionListener;
import it.italiandudes.cah.server.connection.UserManager;
import it.italiandudes.cah.server.db.DBManager;
import it.italiandudes.cah.server.util.ServerDefs;
import it.italiandudes.cah.server.util.ServerSettings;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.InfoFlags;
import it.italiandudes.idl.common.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public final class Server {

    // Attributes
    private static ConnectionListener connectionListener;
    private static JSONObject serverSettings;

    // Start
    public static void start() {
        // Read Server Settings
        try {
            File serverSettingsFilePointer = new File(Defs.Resources.JSON.JSON_SERVER_SETTINGS);
            if (serverSettingsFilePointer.exists() && serverSettingsFilePointer.isFile()) {
                serverSettings = ServerSettings.readServerSettings(serverSettingsFilePointer);
            } else {
                serverSettings = ServerSettings.readDefaultServerSettings();
            }
        } catch (IOException e) {
            Logger.log("INITIALIZATION FAILED: SETTINGS", new InfoFlags(true, true));
            Logger.log(e);
            return;
        }

        // TODO: Open DB Connection

        // Open Connection Listener
        try {
            connectionListener = new ConnectionListener(serverSettings.getInt(Defs.ServerSettingsKeys.MAIN_PORT), serverSettings.getInt(Defs.ServerSettingsKeys.SERVICE_PORT));
        } catch (IOException e) {
            Logger.log("INITIALIZATION FAILED: CONNECTION LISTENER", new InfoFlags(true, true));
            Logger.log(e);
            return;
        }

        Scanner scan = new Scanner(System.in);
        String buffer;
        while (true) {
            buffer = scan.nextLine();
            if (buffer.equals(ServerDefs.CommandInput.STOP)) break;
            // TODO: parse commands
            Logger.log(buffer);
        }

        connectionListener.stopListeners();
        UserManager.removeAllUsers();
        DBManager.closeConnection();
    }

}
