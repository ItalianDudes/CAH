package it.italiandudes.cah.client.javafx.components;

import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public final class WhiteCard extends TextArea {

    // Constructors
    public WhiteCard(@NotNull final String cardText) {
        super(cardText);
        setMinSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        setPrefSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        this.setFocusTraversable(false);
        getStyleClass().clear();
        getStylesheets().clear();
        setStyle("-fx-border-color: transparent;-fx-text-fill: black;-fx-background-color: white;-fx-background: white;");
        setWrapText(true);
        setEditable(false);
    }
}
