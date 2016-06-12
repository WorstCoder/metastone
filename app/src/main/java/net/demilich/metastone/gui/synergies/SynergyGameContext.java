package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.TurnState;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.logic.GameLogic;

public class SynergyGameContext extends GameContext {

    private SynergyGameLogic logic;
    private Player player1;
    private Player player2;
    private DeckFormat deckFormat;

    public SynergyGameContext(Player player1, Player player2, SynergyGameLogic logic, DeckFormat deckFormat){
        super(player1, player2, logic, deckFormat);

        this.player1=player1;
        this.player2=player2;
        this.logic=logic;
        this.deckFormat=deckFormat;
    }

    public void setActivePlayer(Player player){
        activePlayer=player.getId();
    }

    @Override
    public void startTurn(int playerId) {
        turn++;
        logic.startTurn(playerId);
        onGameStateChanged();
        actionsThisTurn = 0;
        turnState = TurnState.TURN_IN_PROGRESS;
    }

}
