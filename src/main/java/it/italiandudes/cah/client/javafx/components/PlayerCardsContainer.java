package it.italiandudes.cah.client.javafx.components;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

@SuppressWarnings("unused")
public final class PlayerCardsContainer extends GridPane {

    // Attributes
    private int columns = 0;

    // Constructors
    public PlayerCardsContainer() {
        super();
        this.setStyle("-fx-border-color: black;-fx-border-radius: 4px;-fx-border-width: 2px;");
        this.setMinSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        this.setPrefSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        this.setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        this.setFocusTraversable(false);
        this.setPadding(new Insets(5));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setHgap(5);
    }

    // Methods
    public ArrayList<String> getWhiteCards() {
        ArrayList<String> whitecards = new ArrayList<>();
        for (Node node : getChildren()) {
            if (node instanceof WhiteCard) {
                whitecards.add(((WhiteCard) node).getText());
            }
        }
        return whitecards;
    }
    public JSONArray getWhiteCardsAsJSON() {
        JSONArray cards = new JSONArray();
        ArrayList<String> whitecards = getWhiteCards();
        for (String whitecard : whitecards) {
            cards.put(whitecard);
        }
        return cards;
    }
    public String getWhiteCardsAsJSONBase64Encoded() {
        return Base64.getEncoder().encodeToString(getWhiteCardsAsJSON().toString().getBytes(StandardCharsets.UTF_8));
    }
    public void addWhiteCard(@NotNull final WhiteCard whiteCard) {
        if (columns == 0) {
            this.add(whiteCard, 0, 0);
        } else {
            this.addColumn(columns, whiteCard);
        }
        ColumnConstraints constraints = new ColumnConstraints(GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE, Priority.SOMETIMES, HPos.CENTER, true);
        this.getColumnConstraints().add(constraints);
        columns++;
    }
    public void setWhiteCard(@NotNull final WhiteCard whiteCard, final int column) {
        for (Node node : this.getChildren()) {
            if (node instanceof WhiteCard && getRowIndex(node) == 0 && getColumnIndex(node) == column) {
                this.getChildren().remove(node);
                break;
            }
        }
        this.add(whiteCard, column, 0);
    }
}
