package it.italiandudes.cah.client.javafx.controller.game;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.components.PlayerCardsContainer;
import it.italiandudes.cah.client.javafx.components.WhiteCard;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameLobby;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMenu;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class ControllerSceneGameWinnerMenu {

    // Graphic Elements
    @FXML private Label labelWinnerName;
    @FXML private TextArea textAreaBlackCard;
    @FXML private GridPane gridPaneWinnerChoices;
    private PlayerCardsContainer winningCardContainer;

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        if (ConnectionManager.isClosed()) {
            new ErrorAlert("ERRORE", "Errore di Connessione", "La connessione e' stata interrotta per cui non e' possibile procedere oltre. Ritorno al menu di selezione del server.");
            Client.getStage().setScene(SceneGameMenu.getScene());
            return;
        }
        DiscordRichPresenceManager.updateRichPresenceState(DiscordRichPresenceManager.States.WINNING_CARDS);
        initFields();
        Platform.runLater(() -> textAreaBlackCard.lookup(".content").setStyle("-fx-background-color: black;"));
    }

    // Methods
    private void initFields() {
        winningCardContainer = new PlayerCardsContainer();
        gridPaneWinnerChoices.add(winningCardContainer, 1, 0);
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            if (RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream()).equals(Defs.Protocol.Server.WINNING_RESULT)) {
                                String resultsBase64 = RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream());
                                JSONObject results = new JSONObject(new String(Base64.getDecoder().decode(resultsBase64), StandardCharsets.UTF_8));
                                Platform.runLater(() -> {
                                    try {
                                        String winnerName = results.getString(Defs.WinningResultsJSONStructure.WINNER_NAME);
                                        String blackCard = results.getString(Defs.WinningResultsJSONStructure.BLACK_CARD);
                                        JSONArray whiteCards = results.getJSONArray(Defs.WinningResultsJSONStructure.WHITE_CARDS);
                                        Platform.runLater(() -> {
                                            labelWinnerName.setText(winnerName);
                                            textAreaBlackCard.setText(blackCard);
                                            for (int i=0; i<whiteCards.length(); i++) {
                                                winningCardContainer.addWhiteCard(new WhiteCard(whiteCards.getString(i)));
                                            }
                                        });
                                    } catch (JSONException e) {
                                        Logger.log(e);
                                        ConnectionManager.closeConnection();
                                        new ErrorAlert("ERRORE", "Errore di I/O", "I dati ricevuti sono corrotti, non validi o non conformi.\nLa Connessione e' terminata.");
                                        Client.getStage().setScene(SceneGameMenu.getScene());
                                    }
                                });
                            } else {
                                throw new ProtocolException("Protocol not respected");
                            }
                        } catch (IllegalArgumentException | JSONException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di I/O", "I dati ricevuti sono corrotti, non validi o non conformi.\nLa Connessione e' terminata.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        } catch (ProtocolException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Protocollo", "Il server non ha rispettato il protocollo.\nLa Connessione e' terminata.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        } catch (IOException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore di connessione.\nLa Connessione e' terminata.");
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
    @FXML
    private void gotoLobby() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            RawSerializer.sendString(ConnectionManager.getUser().getMainConnection().getOutputStream(), Defs.Protocol.Common.GOTO_LOBBY);
                            Platform.runLater(() -> Client.getStage().setScene(SceneGameLobby.getScene()));
                        } catch (IOException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore di connessione.\nLa Connessione e' terminata.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void gotoMenu() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            RawSerializer.sendString(ConnectionManager.getUser().getMainConnection().getOutputStream(), Defs.Protocol.Common.DISCONNECT);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> Client.getStage().setScene(SceneGameMenu.getScene()));
                        } catch (IOException e) {
                            Logger.log(e);
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore di connessione.\nLa Connessione e' terminata.");
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
