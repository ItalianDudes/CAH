package it.italiandudes.cah.client.javafx.controller;

import it.italiandudes.cah.client.javafx.Client;
import javafx.fxml.FXML;

public final class ControllerSceneLoading {

    //Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
    }
}