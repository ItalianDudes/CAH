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

    // Lobby Timer Duration
    public static final int LOBBY_TIMER_DURATION_SECONDS = 10;

    // Jar App Position
    public static final String JAR_POSITION;
    static {
        try {
            JAR_POSITION = new File(CAH.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // Program Arguments
    public static final class ProgramArguments {
        public static final String SERVER = "-server";
    }

    // JSON Client Settings
    public static final class ClientSettingsKeys {
        public static final String ENABLE_DARK_MODE = "enableDarkMode";
        public static final String ENABLE_LOAD = "enableLoad";
        public static final String ENABLE_PASSIVE_LOAD = "enablePassiveLoad";
        public static final String COINS_INCREASE_LOAD = "coinsIncreaseLoad";
        public static final String ENABLE_DISCORD_RICH_PRESENCE = "enableDiscordRichPresence";
    }

    // JSON Server Settings
    public static final class ServerSettingsKeys {
        public static final String MAIN_PORT = "mainPort";
        public static final String SERVICE_PORT = "servicePort";
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
            public static final String IMAGE_WINNING_CUP = IMAGE_DIR + "winning_cup.png";
        }

        // SQL
        public static final class SQL {
            private static final String SQL_DIR = "/sql/";
            public static final String SQL_CARDS = SQL_DIR + "cards.sql";
        }
    }

    // Winning Results JSON Structure
    public static final class WinningResultsJSONStructure {
        public static final String WINNER_NAME = "winner_name";
        public static final String BLACK_CARD = "black_card";
        public static final String WHITE_CARDS = "white_cards";
    }

    // Game Cards JSON Structure
    public static final class GameCardsJSONStructure {
        public static final String BLACK_CARD = "black_card";
        public static final String CARDS_TO_CHOOSE = "cards_to_choose";
        public static final String WHITE_CARDS = "white_cards";
    }

    // Protocol
    public static final class Protocol {
        public static final int SOCKET_TIMEOUT_MILLIS = 10000;
        public static final class Login {
            public static final String MAIN_LOGIN = "main_login";
            public static final String SERVICE_LOGIN = "service_login";
            public static final int OK = 1;
            public static final int NOT_OK = 0;
            public static final int ERROR = -1;
        }
        public static final class Lobby {
            private static final String PREFIX_LOBBY = "lobby_";
            public static final String READY = PREFIX_LOBBY+"ready";
            public static final String NOT_READY = PREFIX_LOBBY+"not_ready";
            public static final String LIST = PREFIX_LOBBY+"list";
            public static final String START_INIT = PREFIX_LOBBY+"start_init";
            public static final String START_INTERRUPT = PREFIX_LOBBY+"start_interrupt";
            public static final String START = PREFIX_LOBBY+"start";
            public static final class ListJSON {
                public static final String LIST = "list";
                public static final String USERNAME = "username";
                public static final String IS_READY = "is_ready";
            }
        }
        public static final class Server {
            public static final String GAME_END = "game_end";
            public static final String WINNING_RESULT = "winning_result";
            public static final String CARDS = "cards";
        }
        public static final class Common {
            public static final String GOTO_LOBBY = "goto_lobby";
            public static final String DISCONNECT = "disconnect";
        }
        public static final class Player {
            public static final String CHOSEN_CARDS = "chosen_cards";
        }
        public static final class Master {
            public static final String WINNING_CARDS = "winning_cards";
            public static final String PLAYER_WHITE_CARDS = "player_white_cards";
        }
    }
}
