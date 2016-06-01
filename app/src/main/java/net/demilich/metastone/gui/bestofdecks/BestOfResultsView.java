package net.demilich.metastone.gui.bestofdecks;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.statistics.GameStatistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BestOfResultsView extends BorderPane {

    @FXML
    private TableView<DeckStats> tableView;

    @FXML
    private Button backButton;


    public BestOfResultsView(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BestOfResultsView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        //heroPowerUsed.getId();
        backButton.setOnAction(event -> NotificationProxy.sendNotification(GameNotification.BEST_OF_MAIN_VIEW));
    }

    public void FillTable(Map statsMap){
        ObservableList<DeckStats> decksStats = FXCollections.observableArrayList();
        for(Object key:statsMap.keySet()){
            decksStats.add(new DeckStats((GameStatistics)statsMap.get(key)));
        }

        tableView.setItems(decksStats);

        for(TableColumn column:tableView.getColumns()){
            column.setCellValueFactory(new PropertyValueFactory(column.getId()));
        }
    }

}
