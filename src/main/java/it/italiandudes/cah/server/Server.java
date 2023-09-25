package it.italiandudes.cah.server;

import it.italiandudes.cah.exceptions.connection.CommandException;
import it.italiandudes.cah.server.commands.Command;
import it.italiandudes.cah.server.connection.UserManager;
import it.italiandudes.cah.server.db.DBManager;
import it.italiandudes.cah.server.utils.CommandManager;
import it.italiandudes.cah.server.connection.ConnectionListener;
import it.italiandudes.cah.server.utils.ServerDefs;
import it.italiandudes.cah.server.utils.ServerSettings;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.InfoFlags;
import it.italiandudes.idl.common.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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

        // Connect to Database
        try {
            String databasePath = serverSettings.getString(Defs.ServerSettingsKeys.DATABASE_PATH);
            if (!databasePath.endsWith(Defs.DB_EXTENSION)) throw new IOException("Database extension is incorrect");
            File databaseFilePointer = new File(databasePath);
            if (databaseFilePointer.exists() && databaseFilePointer.isFile()) {
                DBManager.connectToDB(databaseFilePointer);
            } else {
                DBManager.createDB(databaseFilePointer.getAbsolutePath());
            }
        } catch (SQLException | IOException e) {
            Logger.log("INITIALIZATION FAILED: DATABASE", new InfoFlags(true, true));
            Logger.log(e);
            DBManager.closeConnection();
            return;
        }

        // Open Connection Listener
        try {
            connectionListener = new ConnectionListener(serverSettings.getInt(Defs.ServerSettingsKeys.MAIN_PORT), serverSettings.getInt(Defs.ServerSettingsKeys.SERVICE_PORT));
        } catch (IOException e) {
            Logger.log("INITIALIZATION FAILED: CONNECTION LISTENER", new InfoFlags(true, true));
            Logger.log(e);
            return;
        }

        // Register Commands
        CommandManager.registerAllCommands();

        // Initialize Command Reader
        Scanner scan = new Scanner(System.in);
        String buffer;
        Logger.log("SERVER ONLINE\nMAIN PORT: " + serverSettings.getInt(Defs.ServerSettingsKeys.MAIN_PORT) + "\nSERVICE PORT: " + serverSettings.getInt(Defs.ServerSettingsKeys.SERVICE_PORT));
        while (true) {
            buffer = scan.nextLine();
            if (buffer.equals(ServerDefs.CommandInput.STOP)) break;
            try {
                if (!buffer.replace(" ", "").isEmpty()) {
                    Command command = CommandManager.parseCommand(buffer);
                    if (command != null) command.executeCommand();
                }
            } catch (CommandException e) {
                Logger.log(e);
            }
        }

        // Close Connection Listener
        connectionListener.stopListeners();

        // Disconnect all users
        UserManager.removeAllUsers();

        // Disconnect from Database
        DBManager.closeConnection();
    }

    // Methods
    public static JSONObject getServerSettings() {
        return serverSettings;
    }
    public static ConnectionListener getConnectionListener() {
        return connectionListener;
    }

}
