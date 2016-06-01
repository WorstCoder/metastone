package net.demilich.metastone.gui.bestofdecks;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BestOfResultsWait extends BorderPane {

    @FXML
    private Label topLabel;

    @FXML
    private Label centerLabel;

    private List<String> centerList = Collections.synchronizedList(new ArrayList<>());

    public BestOfResultsWait(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BestOfResultsWait.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    public void setTopLabel(String text){
        this.topLabel.setText(text);
    }

    public void setCenterLabel(String text) {
        this.centerLabel.setText(text);
    }

    public void addRowToCenter(String text){
        centerList.add(text);
        String multiRow = new String();
        if(centerList.size()>5) {centerList.remove(0);}
        for(int i=centerList.size(); i>0; i--){
            multiRow+=centerList.get(i)+"\n";
        }

    }
}
