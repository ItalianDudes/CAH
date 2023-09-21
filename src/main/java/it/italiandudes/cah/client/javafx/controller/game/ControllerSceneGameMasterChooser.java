package it.italiandudes.cah.client.javafx.controller.game;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMenu;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import javafx.fxml.FXML;

public final class ControllerSceneGameMasterChooser {

    // Attributes

    // Graphic Elements

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        if (ConnectionManager.isClosed()) {
            new ErrorAlert("ERRORE", "Errore di Connessione", "La connessione non e' stabilita per cui non e' possibile raggiungere la lobby. Ritorno al menu di selezione del server.");
            Client.getStage().setScene(SceneGameMenu.getScene());
            return;
        }
        DiscordRichPresenceManager.updateRichPresenceState(DiscordRichPresenceManager.States.GAME_MASTER_CHOOSER);

    }

    // EDT
}
