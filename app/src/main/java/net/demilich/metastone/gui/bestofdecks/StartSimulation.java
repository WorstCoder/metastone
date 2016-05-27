package net.demilich.metastone.gui.bestofdecks;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.deckbuilder.DeckFormatProxy;
import net.demilich.metastone.gui.simulationmode.PlayerConfigView;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StartSimulation {
    private PlayerConfig playerConfig1;
    private PlayerConfig playerConfig2;
    private List<Deck> decks;
    private DeckFormat format;
    private Player player1;
    private Player player2;
    private int nuberOfGames;



    public StartSimulation(List<Deck> decks, int nuberOfGames, DeckFormat format, IBehaviour behaviour){
        this.decks=decks;
        this.nuberOfGames=nuberOfGames;
        this.format=format;
        playerConfig1 = new PlayerConfig(decks.get(0),behaviour);
        playerConfig2 = new PlayerConfig(decks.get(0),behaviour);

        for (Card card : CardCatalogue.getHeroes()) {
            if(playerConfig1.getDeck().getHeroClass().toString() == card.getClassRestriction().name())
            {playerConfig1.setHeroCard((HeroCard)card);}
            if(playerConfig2.getDeck().getHeroClass().toString() == card.getClassRestriction().name())
            {playerConfig2.setHeroCard((HeroCard)card);}
        }
    }

    public void Simulation(){
        for(Deck deck1:decks) {
            playerConfig1.setDeck(deck1);
            SetHero(playerConfig1);
            player1 = new Player(playerConfig1);
            for(Deck deck2:decks) {
                if(deck1==deck2) continue;
                playerConfig2.setDeck(deck2);
                SetHero(playerConfig2);
                player2 = new Player(playerConfig2);
                for(int i=0;i<nuberOfGames;i++){
                    GameContext newGame = new GameContext(player1, player2, new GameLogic(), format);
                    newGame.play();
                    newGame.dispose();
                }
            }
        }
    }

    private void SetHero(PlayerConfig config){
        if (config.getDeck().getHeroClass().toString() == config.getHeroCard().getClassRestriction().name()) return;
        for (Card card : CardCatalogue.getHeroes()) {
            if (config.getDeck().getHeroClass().toString() == card.getClassRestriction().name()) {
                config.setHeroCard((HeroCard) card);
            }
        }

    }
}

