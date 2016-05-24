package net.demilich.metastone.gui.bestofdecks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.behaviour.NoAggressionBehaviour;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;
import net.demilich.metastone.game.behaviour.human.HumanBehaviour;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.gui.common.BehaviourStringConverter;
import net.demilich.metastone.gui.common.DeckFormatStringConverter;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.gui.gameconfig.PlayerConfigView;
import net.demilich.metastone.gui.playmode.config.PlayerConfigType;

public class BestOfDecksView extends BorderPane implements EventHandler<ActionEvent> {

    @FXML
    protected Button startButton;

    @FXML
    protected Button backButton;

    @FXML
    protected ComboBox<Integer> numberOfGamesBox;

    @FXML
    protected ComboBox<IBehaviour> behaviourBox;

    @FXML
    protected TilePane decksTile;

    protected  List<Deck> heroesDecks = new ArrayList<>();

    private List<DeckFormat> deckFormats;

    public BestOfDecksView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BestOfDecksView2.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        decksTile.setHgap(4);
        decksTile.setVgap(4);
        decksTile.setPrefColumns(4);

        //behaviourBox.setConverter(new BehaviourStringConverter());
        //setupBehaviours();

        for(HeroClass hero : HeroClass.values()){
            if(hero != HeroClass.ANY && hero != HeroClass.DECK_COLLECTION && hero!= HeroClass.OPPONENT && hero!= HeroClass.BOSS){
            BestOfDecksConfigView configView = new BestOfDecksConfigView(hero);
            decksTile.getChildren().add(configView);}
        }

        startButton.setOnAction(this);
        backButton.setOnAction(this);
        setupNumberOfGamesBox();

    }

    /*private void setupDeckFormats() {
        ObservableList<DeckFormat> deckFormatList = FXCollections.observableArrayList();

        for (DeckFormat deckFormat : deckFormats) {
            deckFormatList.add(deckFormat);
        }

        formatBox.setItems(deckFormatList);
        formatBox.getSelectionModel().selectFirst();
    }*/

    private void setDeckFormats(DeckFormat newDeckFormat) {
        //player1Config.setDeckFormat(newDeckFormat);
        //player2Config.setDeckFormat(newDeckFormat);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() == startButton) {
            System.out.println(":D");
            for(Node node : decksTile.getChildren()){
                BestOfDecksConfigView config = (BestOfDecksConfigView)node;
                if(config.include.isSelected()){
                    heroesDecks.addAll(config.GetDecks());
                }
            }
            DeckFormat df = deckFormats.get(0);
            //StartSimulation(heroesDecks,1,df);
            //GameConfig gameConfig = new GameConfig();
            //gameConfig.setNumberOfGames(numberOfGamesBox.getSelectionModel().getSelectedItem());
            //gameConfig.setPlayerConfig1(player1Config.getPlayerConfig());
            //gameConfig.setPlayerConfig2(player2Config.getPlayerConfig());
            //gameConfig.setDeckFormat(formatBox.getValue());
            //NotificationProxy.sendNotification(GameNotification.COMMIT_SIMULATIONMODE_CONFIG, gameConfig);
        } else if (actionEvent.getSource() == backButton) {
            NotificationProxy.sendNotification(GameNotification.MAIN_MENU);
        }
    }

    /*public void injectDecks(List<Deck> decks) {
        player1Config.injectDecks(decks);
        player2Config.injectDecks(decks);
    }*/

    public void injectDeckFormats(List<DeckFormat> deckFormats) {
		this.deckFormats = deckFormats;
	}

    public void setupBehaviours() {
        ObservableList<IBehaviour> behaviourList = FXCollections.observableArrayList();

        behaviourList.add(new GameStateValueBehaviour());
        behaviourList.add(new PlayRandomBehaviour());
        behaviourList.add(new GreedyOptimizeMove(new WeightedHeuristic()));
        behaviourList.add(new NoAggressionBehaviour());

        behaviourBox.setItems(behaviourList);
        behaviourBox.getSelectionModel().select(0);
    }


    private void setupNumberOfGamesBox() {
        ObservableList<Integer> numberOfGamesEntries = FXCollections.observableArrayList();
        numberOfGamesEntries.add(1);
        numberOfGamesEntries.add(2);
        numberOfGamesEntries.add(4);
        numberOfGamesEntries.add(8);
        numberOfGamesEntries.add(16);
        numberOfGamesEntries.add(32);
        numberOfGamesBox.setItems(numberOfGamesEntries);
        numberOfGamesBox.getSelectionModel().select(0);
    }

}
