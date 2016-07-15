package net.demilich.metastone.gui.synergies;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;

import java.io.IOException;
import java.util.List;

public class SynergiesView extends BorderPane implements EventHandler<ActionEvent> {

    @FXML
    protected Button backButton;

    @FXML
    protected Button startButton;


    private List<DeckFormat> deckFormats;

    private CardCollection allCards;

    private GameContext empty;

    private GameContext enemyDummies;

    public SynergiesView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SynergiesView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        startButton.setOnAction(this);
        backButton.setOnAction(this);
    }

    public void injectDeckFormats(List<DeckFormat> deckFormats) {
        this.deckFormats = deckFormats;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if(actionEvent.getSource() == startButton){
            GetRandomSpells rs = new GetRandomSpells();
            rs.GetRandoms();
            SynergiesMaker warlock = new SynergiesMaker(deckFormats.get(0),HeroClass.WARLOCK);
            warlock.start();
        }
        else if (actionEvent.getSource() == backButton) {
            NotificationProxy.sendNotification(GameNotification.MAIN_MENU);
        }
    }
}
