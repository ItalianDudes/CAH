package it.italiandudes.cah.client.javafx.controller.game.player;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMenu;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameWinnerMenu;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.ProtocolException;

public final class ControllerSceneGamePlayerWaitMaster {

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        if (ConnectionManager.isClosed()) {
            new ErrorAlert("ERRORE", "Errore di Connessione", "La connessione e' stata interrotta per cui non e' possibile procedere oltre. Ritorno al menu di selezione del server.");
            Client.getStage().setScene(SceneGameMenu.getScene());
            return;
        }
        waitForMaster();
    }

    // Methods
    private void waitForMaster() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            if (RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream()).equals(Defs.Protocol.Server.GAME_END)) {
                                Platform.runLater(() -> Client.getStage().setScene(SceneGameWinnerMenu.getScene()));
                            } else {
                                throw new ProtocolException("Protocol not respected");
                            }
                        } catch (ProtocolException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Protocollo", "Il protocollo non e' stato rispettato dal server.\nLa connessione e' stata interrotta.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        } catch (IOException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore durante la comunicazione con il server.\nLa connessione e' stata interrotta.");
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
