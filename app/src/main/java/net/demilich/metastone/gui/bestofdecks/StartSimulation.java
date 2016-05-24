package net.demilich.metastone.gui.bestofdecks;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.deckbuilder.DeckFormatProxy;
import net.demilich.metastone.gui.simulationmode.PlayerConfigView;

import java.util.List;

public class StartSimulation {
    PlayerConfig playerConfig1;
    PlayerConfig playerConfig2;


    public StartSimulation(List<Deck> decks, int nuberOfGames, DeckFormat format){
        playerConfig1 = new PlayerConfig(decks.get(0),new GameStateValueBehaviour());
        playerConfig2 = new PlayerConfig(decks.get(0),new GameStateValueBehaviour());

        Player player1 = new Player(playerConfig1);
        Player player2 = new Player(playerConfig2);


        GameContext newGame = new GameContext(player1, player2, new GameLogic(), format);
        newGame.play();
        newGame.dispose();
    }
}
