package it.italiandudes.cah.server.util;

import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.JarHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

public final class ServerConfiguration {

    // Methods
    @Nullable
    public static JSONObject readServerSettings(@NotNull final String settingsPath) throws IOException {
        return readServerSettings(settingsPath, false);
    }
    @Nullable
    public static JSONObject readServerSettings(@NotNull final String settingsPath, boolean skipValidation) throws IOException {
        if (settingsPath.replace(" ", "").isEmpty()) return null;
        return readServerSettings(new File(settingsPath), skipValidation);
    }
    @NotNull
    public static JSONObject readServerSettings(@NotNull final File settingsFilePointer) throws IOException {
        return readServerSettings(settingsFilePointer, false);
    }
    @NotNull
    public static JSONObject readServerSettings(@NotNull final File settingsFilePointer, boolean skipValidation) throws IOException, JSONException {
        if (!settingsFilePointer.exists() || !settingsFilePointer.isFile()) {
            throw new FileNotFoundException("\"" + settingsFilePointer.getAbsolutePath() + "\" doesn't exist");
        }
        try (FileInputStream inputStream = new FileInputStream(settingsFilePointer)) {
            if (skipValidation) return new JSONObject(new JSONTokener(inputStream));
            else return validateServerSettings(new JSONObject(new JSONTokener(inputStream)));
        }
    }
    @NotNull
    public static JSONObject readDefaultServerSettings() throws IOException {
        File serverSettingsFilePointer = new File(Defs.Resources.JSON.JSON_SERVER_SETTINGS);
        JarHandler.copyFileFromJar(new File(Defs.JAR_POSITION), Defs.Resources.JSON.JSON_SERVER_SETTINGS, serverSettingsFilePointer, true);
        return readServerSettings(serverSettingsFilePointer, true);
    }
    @NotNull
    public static JSONObject validateServerSettings(@NotNull final JSONObject serverSettings) throws IOException {
        JSONObject defaultServerSettings;
        try (InputStream inputStream = Defs.Resources.getAsStream(Defs.Resources.JSON.DEFAULT_JSON_SERVER_SETTINGS)) {
            defaultServerSettings = new JSONObject(new JSONTokener(inputStream));
        }
        try {
            serverSettings.getInt(Defs.ServerSettingsKeys.MAIN_PORT);
        } catch (JSONException e) {
            serverSettings.put(Defs.ServerSettingsKeys.MAIN_PORT, defaultServerSettings.getInt(Defs.ServerSettingsKeys.MAIN_PORT));
        }
        try {
            serverSettings.get(Defs.ServerSettingsKeys.SERVICE_PORT);
        } catch (JSONException e) {
            serverSettings.put(Defs.ServerSettingsKeys.SERVICE_PORT, defaultServerSettings.getInt(Defs.ServerSettingsKeys.SERVICE_PORT));
        }
        return serverSettings;
    }
}
