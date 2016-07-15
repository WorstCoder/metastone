package net.demilich.metastone.game.synergy;

import net.demilich.metastone.game.Environment;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.TurnState;
import net.demilich.metastone.game.cards.costmodifier.CardCostModifier;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.spells.trigger.IGameEventListener;
import net.demilich.metastone.game.spells.trigger.TriggerLayer;
import net.demilich.metastone.game.targeting.EntityReference;

import java.util.List;

public class SynergyGameContext extends GameContext {

    protected SynergyGameLogic logic;
    private Player player1;
    private Player player2;
    private DeckFormat deckFormat;
    protected TriggerManagerC triggerManager = new TriggerManagerC();

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

    @Override
    public void addTrigger(IGameEventListener trigger) {
        triggerManager.addTrigger(trigger);
    }

    @Override
    public void fireGameEvent(GameEvent gameEvent, TriggerLayer layer) {
        if (ignoreEvents()) {
            return;
        }
        gameEvent.setTriggerLayer(layer);
        try {
            triggerManager.fireGameEvent(gameEvent);
        } catch(Exception e) {
            logger.error("Error while processing gameEvent {}", gameEvent);
            logic.panicDump();
            throw e;
        }

    }

    @Override
    public List<IGameEventListener> getTriggersAssociatedWith(EntityReference entityReference) {
        return triggerManager.getTriggersAssociatedWith(entityReference);
    }

    @Override
    public void removeTrigger(IGameEventListener trigger) {
        triggerManager.removeTrigger(trigger);
    }

    @Override
    public void removeTriggersAssociatedWith(EntityReference entityReference) {
        triggerManager.removeTriggersAssociatedWith(entityReference);
    }

    @Override
    public SynergyGameLogic getLogic() {
        return logic;
    }

    @Override
    public SynergyGameContext clone() {
        SynergyGameLogic logicClone = getLogic().clone();
        Player player1Clone = getPlayer1().clone();
        // player1Clone.getDeckName().shuffle();
        Player player2Clone = getPlayer2().clone();
        // player2Clone.getDeckName().shuffle();
        SynergyGameContext clone = new SynergyGameContext(player1Clone, player2Clone, logicClone, deckFormat);
        clone.triggerManager = triggerManager.clone();
        clone.activePlayer = activePlayer;
        clone.turn = turn;
        clone.actionsThisTurn = actionsThisTurn;
        clone.result = result;
        clone.turnState = turnState;
        clone.winner = logicClone.getWinner(player1Clone, player2Clone);
        clone.cardCostModifiers.clear();
        for (CardCostModifier cardCostModifier : cardCostModifiers) {
            clone.cardCostModifiers.add(cardCostModifier.clone());
        }
        for (Environment key : getEnvironment().keySet()) {
            clone.getEnvironment().put(key, getEnvironment().get(key));
        }
        clone.getLogic().setLoggingEnabled(false);
        return clone;
    }
}
