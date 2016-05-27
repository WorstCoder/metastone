package net.demilich.metastone.gui.bestofdecks;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.behaviour.NoAggressionBehaviour;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;
import net.demilich.metastone.game.behaviour.human.HumanBehaviour;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFactory;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.heroes.MetaHero;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.gui.IconFactory;
import net.demilich.metastone.gui.common.BehaviourStringConverter;
import net.demilich.metastone.gui.common.DeckStringConverter;
import net.demilich.metastone.gui.common.HeroStringConverter;
import net.demilich.metastone.gui.playmode.config.PlayerConfigType;

public class BestOfDecksConfigView extends VBox {

    @FXML
    protected Label heroNameLabel;

    @FXML
    protected ImageView heroIcon;

    @FXML
    public CheckBox include;

    private String url;

    private HeroClass hero;

    private final PlayerConfig playerConfig = new PlayerConfig();

    private List<Deck> decks = new ArrayList<Deck>();

    private PlayerConfigType selectionHint;

    private DeckFormat deckFormat;


    public BestOfDecksConfigView(HeroClass hero) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BestOfDecksConfigView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        switch (hero){
            case DRUID: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=4"; break;
            case HUNTER: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=8"; break;
            case MAGE: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=16"; break;
            case PALADIN: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=32"; break;
            case PRIEST: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=64"; break;
            case ROGUE: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=128"; break;
            case SHAMAN: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=256"; break;
            case WARLOCK: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=512"; break;
            case WARRIOR: url="http://www.hearthpwn.com/decks?filter-deck-tag=1&filter-class=1024"; break;
            default: url="http://www.hearthpwn.com/decks"; break;
        }

        this.hero = hero;
        Image heroPortrait = new Image(IconFactory.getHeroIconUrl(hero));
        heroIcon.setImage(heroPortrait);
        heroNameLabel.setText(hero.name());
    }

    List<Deck> GetDecks(int numberOfDecks){return new DecksReader(url,numberOfDecks, hero).GetDecks();}

}
