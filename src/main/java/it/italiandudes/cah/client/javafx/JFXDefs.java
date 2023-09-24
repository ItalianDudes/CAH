package it.italiandudes.cah.client.javafx;

import it.italiandudes.cah.utils.Defs;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;

public final class JFXDefs {

    //App Info
    public static final class AppInfo {
        public static final String NAME = "Cards Against Humanity";
        public static final Image LOGO = new Image(Defs.Resources.get(Resources.Image.IMAGE_LOGO).toString());
    }

    // System Info
    public static final class SystemGraphicInfo {
        public static final Rectangle2D SCREEN_RESOLUTION = Screen.getPrimary().getBounds();
        public static final double SCREEN_WIDTH = SCREEN_RESOLUTION.getWidth();
        public static final double SCREEN_HEIGHT = SCREEN_RESOLUTION.getHeight();
    }

    //Resource Locations
    public static final class Resources {

        //FXML Location
        public static final class FXML {
            private static final String FXML_DIR = "/fxml/";
            public static final String FXML_LOADING = FXML_DIR + "SceneLoading.fxml";
            public static final String FXML_MAIN_MENU = FXML_DIR + "SceneMainMenu.fxml";
            public static final String FXML_SETTINGS_EDITOR = FXML_DIR + "SceneSettingsEditor.fxml";
            public static final class Game {
                private static final String GAME_DIR = FXML_DIR + "game/";
                public static final String FXML_GAME_MENU = GAME_DIR + "SceneGameMenu.fxml";
                public static final String FXML_GAME_LOBBY = GAME_DIR + "SceneGameLobby.fxml";
                public static final String FXML_GAME_MASTER_CHOOSER = GAME_DIR + "SceneGameMasterChooser.fxml";
                public static final String FXML_GAME_WINNER_MENU = GAME_DIR + "SceneGameWinnerMenu.fxml";
                public static final class Master {
                    private static final String MASTER_DIR = GAME_DIR + "master/";
                    public static final String FXML_GAME_MASTER_WAIT_CARDS = MASTER_DIR + "SceneGameMasterWaitWhiteCards.fxml";
                }
                public static final class Player {
                    private static final String PLAYER_DIR = GAME_DIR + "player/";
                    public static final String FXML_GAME_PLAYER_CARDS_CHOOSER = PLAYER_DIR + "SceneGamePlayerCardsChooser.fxml";
                    public static final String FXML_GAME_PLAYER_WAIT_MASTER = PLAYER_DIR + "SceneGamePlayerWaitMaster.fxml";
                }
            }
        }

        // CSS Location
        public static final class CSS {
            private static final String CSS_DIR = "/css/";
            public static final String CSS_LIGHT_THEME = CSS_DIR + "light_theme.css";
            public static final String CSS_DARK_THEME = CSS_DIR + "dark_theme.css";
        }

        //Image Location
        public static final class Image {
            private static final String IMAGE_DIR = "/image/";
            public static final String IMAGE_LOGO = IMAGE_DIR+"app-logo.png";
        }

    }

}
