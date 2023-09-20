package it.italiandudes.cah.utils;

import it.italiandudes.cah.CAH;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public final class Defs {

    // App File Name
    public static final String APP_FILE_NAME = "CAH";

    // DB Version
    public static final String DB_VERSION = "1.0";

    // Jar App Position
    public static final String JAR_POSITION;
    static {
        try {
            JAR_POSITION = new File(CAH.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // JSON Settings
    public static final class SettingsKeys {
        public static final String ENABLE_DARK_MODE = "enableDarkMode";
        public static final String ENABLE_LOAD = "enableLoad";
        public static final String ENABLE_PASSIVE_LOAD = "enablePassiveLoad";
        public static final String COINS_INCREASE_LOAD = "coinsIncreaseLoad";
        public static final String ENABLE_DISCORD_RICH_PRESENCE = "enableDiscordRichPresence";
    }

    // JSON Server
    public static final class ServersKeys {
        public static final String LIST = "list";
        public static final String ADDRESS = "address";
    }

    // Resources Location
    public static final class Resources {

        //Resource Getters
        public static URL get(@NotNull final String resourceConst) {
            return Objects.requireNonNull(CAH.class.getResource(resourceConst));
        }
        public static InputStream getAsStream(@NotNull final String resourceConst) {
            return Objects.requireNonNull(CAH.class.getResourceAsStream(resourceConst));
        }

        // JSON
        public static final class JSON {
            public static final String JSON_DIR = "/json/";
            public static final String JSON_CLIENT_SETTINGS = "client_settings.json";
            public static final String JSON_CLIENT_SERVERS = "client_servers.json";
            public static final String JSON_SERVER_SETTINGS = "server_settings.json";
            public static final String DEFAULT_JSON_CLIENT_SETTINGS = JSON_DIR + JSON_CLIENT_SETTINGS;
            public static final String DEFAULT_JSON_SERVER_SETTINGS = JSON_DIR + JSON_SERVER_SETTINGS;
        }

        // Images
        public static final class Image {
            private static final String IMAGE_DIR = "/image/";
            public static final String IMAGE_DARK_MODE = IMAGE_DIR + "dark_mode.png";
            public static final String IMAGE_LIGHT_MODE = IMAGE_DIR + "light_mode.png";
            public static final String IMAGE_TICK = IMAGE_DIR + "tick.png";
            public static final String IMAGE_CROSS = IMAGE_DIR + "cross.png";
            public static final String IMAGE_WUMPUS = IMAGE_DIR + "wumpus.png";
            public static final String IMAGE_NO_WUMPUS = IMAGE_DIR + "no_wumpus.png";
        }

        // SQL
        public static final class SQL {
            private static final String SQL_DIR = "/sql/";
            public static final String SQL_CARDS = SQL_DIR + "cards.sql";
        }
    }

    // Protocol
    public static final class Protocol {
        public static final int SOCKET_TIMEOUT_MILLIS = 10000;
        public static final class Login {
            public static final String LOGIN = "login";
        }
        public static final class Lobby {
            private static final String PREFIX_LOBBY = "lobby_";
            public static final String READY = PREFIX_LOBBY+"ready";
            public static final String NOT_READY = PREFIX_LOBBY+"not_ready";
            public static final String LIST = PREFIX_LOBBY+"list";
            public static final class ListJSON {
                public static final String LIST = "list";
                public static final String USERNAME = "username";
                public static final String IS_READY = "is_ready";
            }
        }
    }
}
