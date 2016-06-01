package net.demilich.metastone.gui.bestofdecks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
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
import net.demilich.metastone.gui.playmode.config.PlayerConfigType;

public class BestOfDecksView extends BorderPane implements EventHandler<ActionEvent> {

    @FXML
    protected Button startButton;

    @FXML
    protected Button backButton;

    @FXML
    protected ComboBox<Integer> numberOfGamesBox;

    @FXML
    protected ComboBox<Integer> numberOfDecksBox;

    @FXML
    protected ComboBox<IBehaviour> behaviourBox;

    @FXML
    protected TilePane decksTile;

    protected  List<Deck> heroesDecks = new ArrayList<>();

    private List<DeckFormat> deckFormats;
    private Map calcStats;

    public BestOfDecksView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BestOfDecksViewNEW.fxml"));
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

        behaviourBox.setConverter(new BehaviourStringConverter());
        setupBehaviours();

        for(HeroClass hero : HeroClass.values()){
            if(hero != HeroClass.ANY && hero != HeroClass.DECK_COLLECTION && hero!= HeroClass.OPPONENT && hero!= HeroClass.BOSS){
            BestOfDecksConfigView configView = new BestOfDecksConfigView(hero);
            decksTile.getChildren().add(configView);}
        }

        startButton.setOnAction(this);
        backButton.setOnAction(this);
        setupNumberOfGamesBox();
        setupNumberOfDecksBox();

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
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    NotificationProxy.sendNotification(GameNotification.BEST_OF_WAIT_SHOW);
                    NotificationProxy.sendNotification(GameNotification.BEST_OF_WAIT_UPDATE_TOP,"Downloading Decks");
                    getDecks();
                    StartSimulation simulation = new StartSimulation(heroesDecks,numberOfGamesBox.getSelectionModel().getSelectedItem(),deckFormats.get(0),behaviourBox.getSelectionModel().getSelectedItem());
                    simulation.Simulation();
                    List<BestOfResults> simStats = simulation.getResults();
                    calcStats = new StatsCalc(simStats).getResults();
                    NotificationProxy.sendNotification(GameNotification.BEST_OF_GET_RESULTS,calcStats);
                    NotificationProxy.sendNotification(GameNotification.BEST_OF_RESULTS);
                }
            });
            th.setDaemon(true);
            th.start();

        } else if (actionEvent.getSource() == backButton) {
            NotificationProxy.sendNotification(GameNotification.MAIN_MENU);
        }
    }

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

    private void setupNumberOfDecksBox() {
        ObservableList<Integer> numberOfDecksEntries = FXCollections.observableArrayList();
        numberOfDecksEntries.add(1);
        numberOfDecksEntries.add(2);
        numberOfDecksEntries.add(5);
        numberOfDecksEntries.add(10);
        numberOfDecksEntries.add(25);
        //numberOfDecksEntries.add(50);
       // numberOfDecksEntries.add(100);
        numberOfDecksBox.setItems(numberOfDecksEntries);
        numberOfDecksBox.getSelectionModel().select(0);
    }

    private void getDecks(){
        //Make threads pool
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
        //Make list for all calls results
        List<Future<List<Deck>>> futureDecks = new ArrayList<>();
        for(Node node : decksTile.getChildren()){
            BestOfDecksConfigView config = (BestOfDecksConfigView)node;
            if(config.include.isSelected()){
                //Make new reader callable
                Callable<List<Deck>> readerCall = new DecksReader(config.getUrl(),config.getHero(), numberOfDecksBox.getSelectionModel().getSelectedItem());
                //Execute reader
                Future<List<Deck>> future = executor.submit(readerCall);
                //Add executed reader to list, to make return accessible
                futureDecks.add(future);
            }
        }
        //Get all decks form future list
        for(Future<List<Deck>> decks:futureDecks){
            try {
                heroesDecks.addAll(decks.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
