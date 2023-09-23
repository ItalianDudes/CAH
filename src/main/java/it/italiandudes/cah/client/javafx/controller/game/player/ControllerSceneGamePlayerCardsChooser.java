package it.italiandudes.cah.client.javafx.controller.game.player;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.components.WhiteCard;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Arrays;

public final class ControllerSceneGamePlayerCardsChooser {

    // Attributes
    private WhiteCard[] whiteCards;
    private boolean[] selected;
    private int currentCardIndex = -1;
    private int cardsToChoose = -1;

    // Graphic Elements
    @FXML private TextArea textAreaBlackCard;
    @FXML private GridPane gridPaneWhiteCard;
    @FXML private Label labelSelected;
    @FXML private ToggleButton toggleButtonSelectCard;

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        if (ConnectionManager.isClosed()) {
            new ErrorAlert("ERRORE", "Errore di Connessione", "La connessione e' stata interrotta per cui non e' possibile procedere oltre. Ritorno al menu di selezione del server.");
            Client.getStage().setScene(SceneGameMenu.getScene());
            return;
        }
        DiscordRichPresenceManager.updateRichPresenceState(DiscordRichPresenceManager.States.IN_GAME_PLAYER);
        getWhiteCards();
        Platform.runLater(() -> textAreaBlackCard.lookup(".content").setStyle("-fx-background-color: black;"));
    }

    // Methods
    private void getWhiteCards() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            if (RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream()).equals(Defs.Protocol.Server.CARDS)) {
                                JSONObject cards = new JSONObject(RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream()));
                                 String blackCard = cards.getString(Defs.GameCardsJSONStructure.BLACK_CARD);
                                 cardsToChoose = cards.getInt(Defs.GameCardsJSONStructure.CARDS_TO_CHOOSE);
                                JSONArray cardsArray = cards.getJSONArray(Defs.GameCardsJSONStructure.WHITE_CARDS);
                                whiteCards = new WhiteCard[cardsArray.length()];
                                selected = new boolean[cardsArray.length()];
                                Arrays.fill(selected, false);
                                Platform.runLater(() -> {
                                    try {
                                        for (int i = 0; i < cardsArray.length(); i++) {
                                            whiteCards[i] = new WhiteCard(cardsArray.getString(i));
                                            whiteCards[i].setStyle(whiteCards[i].getStyle() + "-fx-border-color: black;-fx-border-radius: 2px;-fx-border-width: 2px;");
                                        }
                                        textAreaBlackCard.setText(blackCard);
                                        gridPaneWhiteCard.add(whiteCards[0], 0, 1);
                                        currentCardIndex = 0;
                                    } catch (JSONException e) {
                                        Logger.log(e);
                                        ConnectionManager.closeConnection();
                                        new ErrorAlert("ERRORE", "Errore di I/O", "I dati ricevuti sono corrotti, non validi o non conformi al protocollo.\nLa connessione e' stata interrotta.");
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
                                new ErrorAlert("ERRORE", "Errore di I/O", "I dati ricevuti sono corrotti, non validi o non conformi al protocollo.\nLa connessione e' stata interrotta.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
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

    // EDT
    @FXML
    private void toggleSelectCard() {}
    @FXML
    private void detectLeftArrow() {}
    @FXML
    private void detectRightArrow() {}
    @FXML
    private void previousCard() {}
    @FXML
    private void nextCard() {}
    @FXML
    private void confirmChoices() {}
}
