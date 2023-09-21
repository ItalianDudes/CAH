package it.italiandudes.cah.client.javafx.controller.game;

import it.italiandudes.cah.client.connection.ConnectionManager;
import it.italiandudes.cah.client.javafx.Client;
import it.italiandudes.cah.client.javafx.alert.ConfirmationAlert;
import it.italiandudes.cah.client.javafx.alert.ErrorAlert;
import it.italiandudes.cah.client.javafx.scene.SceneLoading;
import it.italiandudes.cah.client.javafx.scene.game.SceneGameLobby;
import it.italiandudes.cah.client.javafx.util.UIElementConfigurator;
import it.italiandudes.cah.exceptions.connection.InvalidAddressException;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.cah.utils.DiscordRichPresenceManager;
import it.italiandudes.idl.common.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public final class ControllerSceneGameMenu {

    // Graphic Elements
    @FXML private TextField textFieldUsername;
    @FXML private TextField textFieldServerAddress;
    @FXML private Spinner<Integer> spinnerMainPort;
    @FXML private Spinner<Integer> spinnerServicePort;
    @FXML private ListView<String> listViewServerList;

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
        spinnerMainPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 0, 1));
        spinnerServicePort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 1, 1));
        spinnerMainPort.getEditor().setTextFormatter(UIElementConfigurator.configureNewIntegerTextFormatter());
        spinnerServicePort.getEditor().setTextFormatter(UIElementConfigurator.configureNewIntegerTextFormatter());
        loadServerList();
        DiscordRichPresenceManager.updateRichPresenceState(DiscordRichPresenceManager.States.GAME_MENU);
    }

    // EDT
    private void loadServerList() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            Client.getServers().getJSONArray(Defs.ServersKeys.LIST);
                        } catch (JSONException e) {
                            Client.getServers().put(Defs.ServersKeys.LIST, new JSONArray());
                            return null;
                        }
                        ArrayList<String> addresses = new ArrayList<>();
                        for (int i=0; i<Client.getServers().getJSONArray(Defs.ServersKeys.LIST).length(); i++) {
                            String address = Client.getServers().getJSONArray(Defs.ServersKeys.LIST).getJSONObject(i).getString(Defs.ServersKeys.ADDRESS);
                            if (!addresses.contains(address)) addresses.add(address);
                        }
                        Platform.runLater(() -> listViewServerList.setItems(FXCollections.observableList(addresses)));
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void detectEnterOnElement(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && listViewServerList.getSelectionModel().getSelectedItem() != null) {
            textFieldServerAddress.setText(listViewServerList.getSelectionModel().getSelectedItem());
        }
    }
    @FXML
    private void detectDoubleClickOnElement(@NotNull final MouseEvent event) {
        if (event.getClickCount() >= 2 && listViewServerList.getSelectionModel().getSelectedItem() != null) {
            textFieldServerAddress.setText(listViewServerList.getSelectionModel().getSelectedItem());
        }
    }
    @FXML
    private void connectToServer() {
        String username = textFieldUsername.getText();
        if (username == null || username.replace(" ", "").isEmpty()) {
            new ErrorAlert("ERRORE", "Errore di Procedura", "Il campo nome utente non puo' essere vuoto.");
            return;
        }
        String serverAddress = textFieldServerAddress.getText();
        if (serverAddress == null || serverAddress.replace(" ", "").isEmpty()) {
            new ErrorAlert("ERRORE", "Errore di Procedura", "Il campo indirizzo non puo' essere vuoto. Deve contenere un indirizzo server nel formato <indirizzo>:<porta>.");
            return;
        }
        int mainPort = Integer.parseInt(spinnerMainPort.getEditor().getText());
        int servicePort = Integer.parseInt(spinnerServicePort.getEditor().getText());
        Scene thisScene = Client.getStage().getScene();
        Client.getStage().setScene(SceneLoading.getScene());
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            if (!ConnectionManager.setServerConnection(serverAddress, mainPort, servicePort, username)) {
                                Platform.runLater(() -> {
                                    if (new ConfirmationAlert("ERRORE", "Errore Interno", "E' stata trovata una connessione gia' aperta nell'applicazione. Questa circostanza e' tipicamente anomala e potrebbe essere causata un errore. Vuoi riprovare a connetterti chiudendo la connessione precedente?").result) {
                                        ConnectionManager.closeConnection();
                                        connectToServer();
                                    } else {
                                        Logger.log("Application closed due to an irregular open connection on server choosing page.");
                                        System.exit(0);
                                    }
                                });
                                return null;
                            }
                            JSONObject o = new JSONObject();
                            o.put(Defs.ServersKeys.ADDRESS, serverAddress);
                            Client.getServers().getJSONArray(Defs.ServersKeys.LIST).put(o);
                            try {
                                Client.writeJSONServers();
                            } catch (IOException e) {
                                Logger.log(e);
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di I/O", "Si e' verificato un errore durante la scrittura della lista dei server."));
                            }
                            Platform.runLater(() -> Client.getStage().setScene(SceneGameLobby.getScene()));
                        } catch (InvalidAddressException e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di I/O", "L'indirizzo inserito non e' valido. La porta deve essere un numero intero compreso tra 0 e 65535.");
                                Client.getStage().setScene(thisScene);
                            });
                            return null;
                        } catch (UnknownHostException e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore durante la risoluzione dell'indirizzo. L'indirizzo potrebbe non esistere oppure potrebbe essersi verificato un errore durante la connessione.");
                                Client.getStage().setScene(thisScene);
                            });
                            return null;
                        } catch (SocketTimeoutException e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Timeout", "Il tempo per eseguire la connessione e' scaduto. La connessione e' stata chiusa.");
                                Client.getStage().setScene(thisScene);
                            });
                            return null;
                        } catch (ProtocolException e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Protocollo", "Protocollo non rispettato. Verifica di non aver scambiato la porta principale e quella di servizio o di aver inserito le porte corrette.");
                                Client.getStage().setScene(thisScene);
                            });
                            return null;
                        }catch (IOException e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore durante la connessione.");
                                Client.getStage().setScene(thisScene);
                            });
                            return null;
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void deleteServerFromList() {
        String address = listViewServerList.getSelectionModel().getSelectedItem();
        if (address != null) {
            listViewServerList.getItems().remove(listViewServerList.getSelectionModel().getSelectedItem());
            new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() {
                            for (int i=0; i<Client.getServers().length(); i++) {
                                if (Client.getServers().getJSONArray(Defs.ServersKeys.LIST).getString(i).equals(address)) {
                                    Client.getServers().getJSONArray(Defs.ServersKeys.LIST).remove(i);
                                    try {
                                        Client.writeJSONServers();
                                    } catch (IOException e) {
                                        Logger.log(e);
                                        Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di I/O", "Si e' verificato un errore durante la scrittura della lista dei server."));
                                    }
                                }
                            }
                            return null;
                        }
                    };
                }
            }.start();
        }
    }

}
