package it.italiandudes.cah.client.javafx.controller.game.player;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ConfirmationAlert;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.components.WhiteCard;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMenu;
import it.italiandudes.cah.client.javafx.scene.game.player.SceneGamePlayerWaitMaster;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

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
                                            whiteCards[i].setMinSize(TextArea.USE_PREF_SIZE, TextArea.USE_PREF_SIZE);
                                            whiteCards[i].setPrefSize(240, 280);
                                            whiteCards[i].setMaxSize(TextArea.USE_PREF_SIZE, TextArea.USE_PREF_SIZE);
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
    private void toggleSelectCard() {
        if (toggleButtonSelectCard.isSelected()) {
            int selectedCards = 0;
            for (boolean b : selected) {
                if (b) selectedCards++;
            }
            if (selectedCards >= cardsToChoose) {
                toggleButtonSelectCard.setSelected(false);
                return;
            }
            toggleButtonSelectCard.setText("DESELEZIONA CARTA");
            labelSelected.setText("SELEZIONATA");
            selected[currentCardIndex] = true;
        } else {
            toggleButtonSelectCard.setText("SELEZIONA CARTA");
            selected[currentCardIndex] = false;
            labelSelected.setText("NON SELEZIONATA");
        }
    }
    @FXML
    private void detectLeftArrow(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.LEFT) previousCard();
    }
    @FXML
    private void detectRightArrow(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT) nextCard();
    }
    @FXML @SuppressWarnings("DuplicatedCode")
    private void previousCard() {
        for (Node node : gridPaneWhiteCard.getChildren()) {
            if (node instanceof WhiteCard) {
                gridPaneWhiteCard.getChildren().remove(node);
                break;
            }
        }
        if (currentCardIndex == 0) {
            currentCardIndex = whiteCards.length - 1;
        } else {
            currentCardIndex--;
        }
        gridPaneWhiteCard.add(whiteCards[currentCardIndex], 0, 1);
        if (selected[currentCardIndex]) {
            labelSelected.setText("SELEZIONATA");
            toggleButtonSelectCard.setText("DESELEZIONA CARTA");
            toggleButtonSelectCard.setSelected(true);
        } else {
            labelSelected.setText("NON SELEZIONATA");
            toggleButtonSelectCard.setText("SELEZIONA CARTA");
            toggleButtonSelectCard.setSelected(false);
        }
    }
    @FXML @SuppressWarnings("DuplicatedCode")
    private void nextCard() {
        for (Node node : gridPaneWhiteCard.getChildren()) {
            if (node instanceof WhiteCard) {
                gridPaneWhiteCard.getChildren().remove(node);
                break;
            }
        }
        if (currentCardIndex == whiteCards.length -1) {
            currentCardIndex = 0;
        } else {
            currentCardIndex++;
        }
        gridPaneWhiteCard.add(whiteCards[currentCardIndex], 0, 1);
        if (selected[currentCardIndex]) {
            labelSelected.setText("SELEZIONATA");
            toggleButtonSelectCard.setText("DESELEZIONA CARTA");
            toggleButtonSelectCard.setSelected(true);
        } else {
            labelSelected.setText("NON SELEZIONATA");
            toggleButtonSelectCard.setText("SELEZIONA CARTA");
            toggleButtonSelectCard.setSelected(false);
        }
    }
    @FXML
    private void confirmChoices() {
        int selectedCards = 0;
        for (boolean b : selected) {
            if (b) selectedCards++;
        }
        if (selectedCards != cardsToChoose) {
            new ErrorAlert("ERRORE", "Errore di Procedura", "Per inviare le carte al database occorre prima selezionare il numero di carte corretto.");
            return;
        }
        if (!new ConfirmationAlert("INVIO", "Invio Carte", "Continuando si invieranno le carte selezionate, vuoi continuare?").result) return;
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        JSONArray chosenCardIndexes = new JSONArray();
                        for (int i=0; i<selected.length; i++) {
                            if (selected[i]) chosenCardIndexes.put(i);
                        }
                        String chosenCards = Base64.getEncoder().encodeToString(chosenCardIndexes.toString().getBytes(StandardCharsets.UTF_8));
                        try {
                            RawSerializer.sendString(ConnectionManager.getUser().getMainConnection().getOutputStream(), Defs.Protocol.Player.CHOSEN_CARDS);
                            RawSerializer.sendString(ConnectionManager.getUser().getMainConnection().getOutputStream(), chosenCards);
                            Platform.runLater(() -> Client.getStage().setScene(SceneGamePlayerWaitMaster.getScene()));
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
