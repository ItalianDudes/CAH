package it.italiandudes.cah.client.javafx.controller.game;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMenu;
import it.italiandudes.cah.client.javafx.scene.game.master.SceneGameMasterWaitWhiteCards;
import it.italiandudes.cah.client.javafx.scene.game.player.SceneGamePlayerCardsChooser;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.io.IOException;

public final class ControllerSceneGameMasterChooser {

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        if (ConnectionManager.isClosed()) {
            new ErrorAlert("ERRORE", "Errore di Connessione", "La connessione e' stata interrotta per cui non e' possibile procedere oltre. Ritorno al menu di selezione del server.");
            Client.getStage().setScene(SceneGameMenu.getScene());
            return;
        }
        DiscordRichPresenceManager.updateRichPresenceState(DiscordRichPresenceManager.States.GAME_MASTER_CHOOSER);
        waitForRole();
    }

    // Methods
    private void waitForRole() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            boolean isMaster = RawSerializer.receiveBoolean(ConnectionManager.getUser().getMainConnection().getInputStream());
                            if (isMaster) {
                                Client.getStage().setScene(SceneGameMasterWaitWhiteCards.getScene());
                            } else {
                                Client.getStage().setScene(SceneGamePlayerCardsChooser.getScene());
                            }
                        } catch (IOException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore durante la comunicazione.\nLa connessione e' stata chiusa.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
}
