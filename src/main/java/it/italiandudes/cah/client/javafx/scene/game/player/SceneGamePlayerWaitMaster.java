package it.italiandudes.cah.client.javafx.scene.game.player;

import it.italiandudes.cah.client.javafx.JFXDefs;
import it.italiandudes.cah.client.javafx.util.ThemeHandler;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public final class SceneGamePlayerWaitMaster {

    //Scene Generator
    public static Scene getScene(){
        try {
            Scene scene = new Scene(FXMLLoader.load(Defs.Resources.get(JFXDefs.Resources.FXML.Game.Player.FXML_GAME_PLAYER_WAIT_MASTER)));
            ThemeHandler.loadConfigTheme(scene);
            return scene;
        }catch (IOException e){
            Logger.log(e);
            return null;
        }
    }
}
