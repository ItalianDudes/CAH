package it.italiandudes.cah.client.javafx.controller.game;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.connection.User;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.data.LobbyUser;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMasterChooser;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameMenu;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public final class ControllerSceneGameLobby {

    // Attributes
    private final Timeline timer = new Timeline();
    private LobbyUser lobbyUser;
    private boolean safeInterrupt = false;
    private int seconds = Defs.LOBBY_TIMER_DURATION_SECONDS;

    // Graphic Elements
    @FXML private Label labelTimer;
    @FXML private ListView<LobbyUser> listViewUsers;
    @FXML private Button buttonReady;

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        if (ConnectionManager.isClosed()) {
            new ErrorAlert("ERRORE", "Errore di Connessione", "La connessione e' stata interrotta per cui non e' possibile raggiungere la lobby. Ritorno al menu di selezione del server.");
            Client.getStage().setScene(SceneGameMenu.getScene());
            return;
        }
        DiscordRichPresenceManager.updateRichPresenceState(DiscordRichPresenceManager.States.GAME_LOBBY);
        listViewUsers.setCellFactory(lv -> new ListCell<LobbyUser>() {
            private final ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> updateColor();
            private void updateColor() {
                if (getItem().isReady()) {
                    setTextFill(Color.LIMEGREEN);
                } else {
                    setTextFill(Color.RED);
                }
            }
            @Override
            protected void updateItem(LobbyUser user, boolean empty) {
                LobbyUser oldUser = getItem();
                if (oldUser != null) {
                    oldUser.isReadyProperty().removeListener(changeListener);
                }
                super.updateItem(user, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(user.getUsername());
                    user.isReadyProperty().addListener(changeListener);
                    updateColor();
                }
            }
        });
        lobbyUser = new LobbyUser(ConnectionManager.getUser().getUsername());
        listViewUsers.getItems().add(lobbyUser);
        timer.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(1), (e) -> {
            seconds--;
            labelTimer.setText("Countdown: "+seconds);
            if (seconds<=0) timer.stop();
        });
        timer.getKeyFrames().add(frame);
        labelTimer.setText("Countdown: "+seconds);
        startUpdateThread();
        startServerListenerThread();
    }

    // Methods
    private void updateList() throws IOException {
        RawSerializer.sendString(ConnectionManager.getUser().getServiceConnection().getOutputStream(), Defs.Protocol.Lobby.LIST);
        JSONObject users = new JSONObject(RawSerializer.receiveString(ConnectionManager.getUser().getServiceConnection().getInputStream()));
        JSONArray list = users.getJSONArray(Defs.Protocol.Lobby.ListJSON.LIST);
        ObservableList<LobbyUser> fxList = listViewUsers.getItems();
        ArrayList<LobbyUser> jsonList = new ArrayList<>();
        for (int i=0; i<list.length(); i++) {
            LobbyUser lobbyUser = new LobbyUser(list.getJSONObject(i).getString(Defs.Protocol.Lobby.ListJSON.USERNAME), list.getJSONObject(i).getBoolean(Defs.Protocol.Lobby.ListJSON.IS_READY));
            jsonList.add(lobbyUser);
            if (fxList.contains(lobbyUser)) {
                fxList.get(fxList.indexOf(lobbyUser)).isReadyProperty().setValue(lobbyUser.isReady());
            } else {
                fxList.add(lobbyUser);
            }
        }
        fxList.removeIf(user -> !jsonList.contains(user));
        Platform.runLater(() -> listViewUsers.refresh());
    }

    // EDT
    private void startServerListenerThread() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            String status;
                            do {
                                status = RawSerializer.receiveString(ConnectionManager.getUser().getMainConnection().getInputStream());
                                switch (status) {
                                    case Defs.Protocol.Lobby.START_INIT:
                                        timer.playFromStart();
                                        break;

                                    case Defs.Protocol.Lobby.START_INTERRUPT:
                                        timer.stop();
                                        seconds = Defs.LOBBY_TIMER_DURATION_SECONDS;
                                        Platform.runLater(() -> labelTimer.setText("Countdown: "+seconds));
                                        break;

                                    case Defs.Protocol.Lobby.START:
                                        timer.stop();
                                        safeInterrupt = true;
                                        Platform.runLater(() -> Client.getStage().setScene(SceneGameMasterChooser.getScene()));
                                        break;

                                    default:
                                        throw new IOException("Protocol not respected!");
                                }
                            } while (!safeInterrupt);
                        } catch (IOException e) {
                            Logger.log(e);
                            safeInterrupt = true;
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Protocollo", "Il server non ha rispettato il protocollo.\nLa connessione e' terminata.");
                                Client.getStage().setScene(SceneGameMenu.getScene());
                            });
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    private void startUpdateThread() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            while (!safeInterrupt) {
                                updateList();
                                //noinspection BusyWait
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException ignored) {
                        } catch (IOException | JSONException e) {
                            Logger.log(e);
                            safeInterrupt = true;
                            ConnectionManager.closeConnection();
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore di connessione: la connessione e' terminata.");
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
    private void toggleReady() {
        if (lobbyUser.isReady()) {
            buttonReady.setText("PRONTO");
            lobbyUser.isReadyProperty().setValue(false);
            new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() {
                            try {
                                User user = ConnectionManager.getUser();
                                if (user == null) throw new IOException();
                                RawSerializer.sendString(user.getMainConnection().getOutputStream(), Defs.Protocol.Lobby.NOT_READY);
                            } catch (IOException e) {
                                Logger.log(e);
                                safeInterrupt = true;
                                ConnectionManager.closeConnection();
                                Platform.runLater(() -> {
                                    new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore di connessione: la connessione e' terminata.");
                                    Client.getStage().setScene(SceneGameMenu.getScene());
                                });
                            }
                            return null;
                        }
                    };
                }
            };
        } else {
            buttonReady.setText("NON PRONTO");
            lobbyUser.isReadyProperty().setValue(true);
            new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() {
                            try {
                                User user = ConnectionManager.getUser();
                                if (user == null) throw new IOException();
                                RawSerializer.sendString(user.getMainConnection().getOutputStream(), Defs.Protocol.Lobby.READY);
                            } catch (IOException e) {
                                Logger.log(e);
                                safeInterrupt = true;
                                ConnectionManager.closeConnection();
                                Platform.runLater(() -> {
                                    new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore di connessione: la connessione e' terminata.");
                                    Client.getStage().setScene(SceneGameMenu.getScene());
                                });
                            }
                            return null;
                        }
                    };
                }
            };
        }
        listViewUsers.refresh();
    }
}
