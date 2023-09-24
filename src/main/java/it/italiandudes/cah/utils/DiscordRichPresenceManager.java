package it.italiandudes.cah.utils;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import it.italiandudes.cah.client.javafx.Client;
import org.jetbrains.annotations.Nullable;

public final class DiscordRichPresenceManager {

    // Classes
    public static final class States {
        public static final String MENU = "Nel Menu";
        public static final String SETTINGS = "Modificando le Impostazioni";
        public static final String GAME_MENU = "Selezionando il Server di Gioco";
        public static final String GAME_LOBBY = "In Lobby";
        public static final String GAME_MASTER_CHOOSER = "Aspettando la selezione del GM";
        public static final String IN_GAME_MASTER = "In gioco come Master";
        public static final String IN_GAME_PLAYER = "In gioco come Giocatore";
        public static final String WINNING_CARDS = "Guardando le scelte del giocatore vincente";
    }

    // Attributes
    public static final String APPLICATION_ID = "1153595404852658257";
    public static final String APPLICATION_IMAGE = "cah";
    private static DiscordRichPresence presence = null;

    // Rich Presence Initializer
    private static void initializeRichPresence() {
        try {
            if (!Client.getSettings().getBoolean(Defs.ClientSettingsKeys.ENABLE_DISCORD_RICH_PRESENCE)) return;
        } catch (Throwable e) {
            return;
        }
        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        lib.Discord_Initialize(APPLICATION_ID, handlers, true, null);
        presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.largeImageKey = APPLICATION_IMAGE;
        lib.Discord_UpdatePresence(presence);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    //noinspection BusyWait
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }
    public static void updateRichPresenceState(@Nullable final String state) {
        try {
            if (!Client.getSettings().getBoolean(Defs.ClientSettingsKeys.ENABLE_DISCORD_RICH_PRESENCE)) return;
        } catch (Throwable e) {
            return;
        }
        if (presence == null) initializeRichPresence();
        presence.state = state;
        DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
    }
    public static void shutdownRichPresence() {
        presence = null;
        DiscordRPC.INSTANCE.Discord_Shutdown();
    }
}
