package it.italiandudes.cah.client.javafx;

import it.italiandudes.cah.client.javafx.scene.SceneMainMenu;
import it.italiandudes.cah.client.javafx.util.ThemeHandler;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.idl.common.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@SuppressWarnings("unused")
public final class Client extends Application {

    // Attributes
    private static Clipboard SYSTEM_CLIPBOARD = null;
    private static Stage stage = null;
    private static JSONObject SETTINGS = null;
    private static JSONObject SERVERS = null;
    private static Color COLOR_THEME_BACKGROUND = null;

    // JavaFX Application Main
    @Override
    public void start(Stage stage) {
        SYSTEM_CLIPBOARD = Clipboard.getSystemClipboard();
        Client.stage = stage;
        stage.setTitle(JFXDefs.AppInfo.NAME);
        stage.getIcons().add(JFXDefs.AppInfo.LOGO);
        stage.setScene(SceneMainMenu.getScene());
        stage.show();
        stage.setX((JFXDefs.SystemGraphicInfo.SCREEN_WIDTH - stage.getWidth()) / 2);
        stage.setY((JFXDefs.SystemGraphicInfo.SCREEN_HEIGHT - stage.getHeight()) / 2);
        stage.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            stage.hide();
            Logger.log("JavaFX Window Close Event fired, exiting Java process...");
            System.exit(0);
        });
        COLOR_THEME_BACKGROUND = (Color) ((Region) Client.getStage().getScene().lookup(".root")).getBackground().getFills().get(0).getFill();

        // Notice into the logs that the application started Successfully
        Logger.log("Application started successfully!");
    }

    // Start Methods
    public static void start(String[] args) {
        loadSettingsFile();
        loadServersFile();
        launch(args);
    }

    // Settings Loader
    public static void loadServersFile() {
        File serversFile = new File(Defs.Resources.JSON.JSON_CLIENT_SERVERS);
        if (!serversFile.exists() || !serversFile.isFile()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                serversFile.createNewFile();
            } catch (IOException e) {
                Logger.log(e);
            }
            SERVERS = new JSONObject();
            return;
        }
        try {
            Scanner inFile = new Scanner(serversFile);
            StringBuilder builder = new StringBuilder();
            while (inFile.hasNext()) {
                builder.append(inFile.nextLine()).append('\n');
            }
            inFile.close();
            SERVERS = new JSONObject(builder.toString());
        } catch (FileNotFoundException e) {
            Logger.log(e);
            SERVERS = new JSONObject();
        } catch (JSONException e) {
            SERVERS = new JSONObject();
        }
    }
    public static void loadSettingsFile() {
        File settingsFile = new File(Defs.Resources.JSON.JSON_CLIENT_SETTINGS);
        if (!settingsFile.exists() || !settingsFile.isFile()) {
            try {
                JarHandler.copyFileFromJar(new File(Defs.JAR_POSITION), Defs.Resources.JSON.DEFAULT_JSON_CLIENT_SETTINGS, settingsFile, true);
            } catch (IOException e) {
                Logger.log(e);
                return;
            }
        }
        try {
            Scanner inFile = new Scanner(settingsFile);
            StringBuilder builder = new StringBuilder();
            while (inFile.hasNext()) {
                builder.append(inFile.nextLine()).append('\n');
            }
            inFile.close();
            SETTINGS = new JSONObject(builder.toString());
            fixJSONSettings();
        } catch (IOException e) {
            Logger.log(e);
            return;
        }
        ThemeHandler.setConfigTheme();
    }

    // Settings Checker
    private static void fixJSONSettings() throws JSONException, IOException {
        try {
            SETTINGS.getBoolean(Defs.ClientSettingsKeys.ENABLE_DARK_MODE);
        } catch (JSONException e) {
            SETTINGS.remove(Defs.ClientSettingsKeys.ENABLE_DARK_MODE);
            SETTINGS.put(Defs.ClientSettingsKeys.ENABLE_DARK_MODE, true);
        }
        try {
            SETTINGS.getBoolean(Defs.ClientSettingsKeys.ENABLE_LOAD);
        } catch (JSONException e) {
            SETTINGS.remove(Defs.ClientSettingsKeys.ENABLE_LOAD);
            SETTINGS.put(Defs.ClientSettingsKeys.ENABLE_LOAD, true);
        }
        try {
            SETTINGS.getBoolean(Defs.ClientSettingsKeys.ENABLE_PASSIVE_LOAD);
        } catch (JSONException e) {
            SETTINGS.remove(Defs.ClientSettingsKeys.ENABLE_PASSIVE_LOAD);
            SETTINGS.put(Defs.ClientSettingsKeys.ENABLE_PASSIVE_LOAD, true);
        }
        try {
            SETTINGS.getBoolean(Defs.ClientSettingsKeys.COINS_INCREASE_LOAD);
        } catch (JSONException e) {
            SETTINGS.remove(Defs.ClientSettingsKeys.COINS_INCREASE_LOAD);
            SETTINGS.put(Defs.ClientSettingsKeys.COINS_INCREASE_LOAD, true);
        }
        try {
            SETTINGS.getBoolean(Defs.ClientSettingsKeys.ENABLE_DISCORD_RICH_PRESENCE);
        } catch (JSONException e) {
            SETTINGS.remove(Defs.ClientSettingsKeys.ENABLE_DISCORD_RICH_PRESENCE);
            SETTINGS.put(Defs.ClientSettingsKeys.ENABLE_DISCORD_RICH_PRESENCE, true);
        }
        writeJSONSettings();
    }
    public static void writeJSONServers() throws IOException {
        FileWriter writer = new FileWriter(Defs.Resources.JSON.JSON_CLIENT_SERVERS);
        writer.append(SERVERS.toString(2));
        writer.close();
    }
    public static void writeJSONSettings() throws IOException {
        FileWriter writer = new FileWriter(Defs.Resources.JSON.JSON_CLIENT_SETTINGS);
        writer.append(SETTINGS.toString(2));
        writer.close();
    }

    // Methods
    @NotNull
    public static Clipboard getSystemClipboard() {
        return SYSTEM_CLIPBOARD;
    }
    @NotNull
    public static Stage getStage(){
        return stage;
    }
    @NotNull
    public static Stage initPopupStage(Scene scene) {
        Stage popupStage = new Stage();
        popupStage.getIcons().add(JFXDefs.AppInfo.LOGO);
        popupStage.setTitle(JFXDefs.AppInfo.NAME);
        popupStage.initOwner(getStage());
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(scene);
        return popupStage;
    }
    @NotNull
    public static JSONObject getSettings() {
        return SETTINGS;
    }
    @NotNull
    public static JSONObject getServers() {
        return SERVERS;
    }
    @NotNull
    public static Color getBackgroundThemeColor() {
        return COLOR_THEME_BACKGROUND;
    }
    public static void updateBackgroundThemeColor() {
        COLOR_THEME_BACKGROUND = (Color) ((Region) Client.getStage().getScene().lookup(".root")).getBackground().getFills().get(0).getFill();
    }

}
