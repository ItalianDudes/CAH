package it.italiandudes.cah.client.javafx.controller.game.master;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.JFXDefs;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.components.PlayerCardsContainer;
import it.italiandudes.cah.client.javafx.components.WhiteCard;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMenu;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameWinnerMenu;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ControllerSceneGameMasterWaitWhiteCards {

    // Attributes
    private int playerNumber;
    private static PlayerCardsContainer winnerCardContainer = null;

    // Graphic Elements
    @FXML private TextArea textAreaBlackCard;
    @FXML private GridPane gridPanePlayerCardsContainer;
    private PlayerCardsContainer[] playerCardsContainers;

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        if (ConnectionManager.isClosed()) {
            new ErrorAlert("ERRORE", "Errore di Connessione", "La connessione e' stata interrotta per cui non e' possibile procedere oltre. Ritorno al menu di selezione del server.");
            Client.getStage().setScene(SceneGameMenu.getScene());
            return;
        }
        DiscordRichPresenceManager.updateRichPresenceState(DiscordRichPresenceManager.States.IN_GAME_MASTER);
        initChooserGridPane();
        waitForPlayerCards();
        Platform.runLater(() -> textAreaBlackCard.lookup(".content").setStyle("-fx-background-color: black;"));
    }

    // Methods
    private void waitForPlayerCards() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        String base64cards = null;
                        try {
                            if (RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream()).equals(Defs.Protocol.Master.PLAYER_WHITE_CARDS)) {
                                base64cards = RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream());
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
                        try {
                            String jsonCardsString = new String(Base64.getDecoder().decode(base64cards), StandardCharsets.UTF_8);
                            JSONArray playerChoices = new JSONArray(jsonCardsString);
                            if (playerChoices.length() != playerCardsContainers.length) throw new JSONException("Player number mismatch");
                            AtomicBoolean error = new AtomicBoolean(false);
                            for (int i=0; i<playerChoices.length(); i++) {
                                if (error.get()) return null;
                                JSONArray cards = playerChoices.getJSONArray(i) ;
                                int finalI = i;
                                Platform.runLater(() -> {
                                    for (int j=0; j<cards.length(); j++) {
                                        try {
                                            playerCardsContainers[finalI].addWhiteCard(new WhiteCard(cards.getString(j)));
                                        } catch (JSONException e) {
                                            error.set(true);
                                            Logger.log(e);
                                            ConnectionManager.closeConnection();
                                            new ErrorAlert("ERRORE", "Errore di I/O", "I dati ricevuti sono corrotti, non validi o non conformi al protocollo.\nLa connessione e' stata interrotta.");
                                            Client.getStage().setScene(SceneGameMenu.getScene());
                                            return;
                                        }
                                    }
                                });
                            }
                        } catch (IllegalArgumentException | JSONException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di I/O", "I dati ricevuti sono corrotti, non validi o non conformi al protocollo.\nLa connessione e' stata interrotta.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    private void initChooserGridPane() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            playerNumber = RawSerializer.receiveInt(ConnectionManager.getUser().getMainConnection().getInputStream());
                            if (playerNumber < 1) throw new ProtocolException("Protocol not respected");
                            playerCardsContainers = new PlayerCardsContainer[playerNumber];
                            Platform.runLater(() -> {
                                playerCardsContainers[0] = new PlayerCardsContainer();
                                gridPanePlayerCardsContainer.add(playerCardsContainers[0], 0, 0);
                                RowConstraints firstRowConstraints = gridPanePlayerCardsContainer.getRowConstraints().get(0);
                                firstRowConstraints.setMinHeight(GridPane.USE_COMPUTED_SIZE);
                                firstRowConstraints.setPrefHeight(GridPane.USE_COMPUTED_SIZE);
                                firstRowConstraints.setMaxHeight(GridPane.USE_COMPUTED_SIZE);
                                firstRowConstraints.setVgrow(Priority.SOMETIMES);
                                firstRowConstraints.setValignment(VPos.CENTER);
                                firstRowConstraints.setFillHeight(true);
                                for (int i = 1; i < playerNumber; i++) {
                                    playerCardsContainers[i] = new PlayerCardsContainer();
                                    gridPanePlayerCardsContainer.addRow(i, playerCardsContainers[i]);
                                    gridPanePlayerCardsContainer.getRowConstraints().add(new RowConstraints(GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE, Priority.SOMETIMES, VPos.CENTER, true));
                                }
                                for (int i = 0; i < playerNumber; i++) {
                                    ImageView img = new ImageView(JFXDefs.PreloadedResources.WINNING_CUP);
                                    img.setFitWidth(30);
                                    img.setFitHeight(30);
                                    Button btn = new Button(null, img);
                                    btn.setMaxHeight(Double.MAX_VALUE);
                                    btn.setStyle("-fx-border-color: black;-fx-border-radius: 2px;-fx-border-width: 2px;");
                                    int finalI = i;
                                    btn.setOnAction((e) -> {
                                        if (winnerCardContainer == null) {
                                            winnerCardContainer = playerCardsContainers[finalI];
                                            new Service<Void>() {
                                                @Override
                                                protected Task<Void> createTask() {
                                                    return new Task<Void>() {
                                                        @Override
                                                        protected Void call() {
                                                            try {
                                                                RawSerializer.sendString(ConnectionManager.getUser().getMainConnection().getOutputStream(), Defs.Protocol.Master.WINNING_CARDS);
                                                                RawSerializer.sendString(ConnectionManager.getUser().getMainConnection().getOutputStream(), winnerCardContainer.getWhiteCardsAsJSONBase64Encoded());
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
                                    });
                                    gridPanePlayerCardsContainer.add(btn, 1, i);
                                }
                            });
                        } catch (ProtocolException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Protocollo", "Si e' verificato un errore di protocollo da parte del server.\nLa connessione e' stata terminata.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        } catch (IOException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore durante la comunicazione con il server.\nLa connessione e' stata terminata.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        }
                        return null;
                    }
                };
            }
        }.start();
    }

    // EDT
}
