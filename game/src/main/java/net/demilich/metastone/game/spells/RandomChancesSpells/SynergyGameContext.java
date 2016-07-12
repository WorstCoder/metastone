package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.TurnState;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.logic.GameLogic;
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

}
