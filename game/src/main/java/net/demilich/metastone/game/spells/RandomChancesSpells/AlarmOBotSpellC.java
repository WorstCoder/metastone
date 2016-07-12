package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.ReturnMinionToHandSpell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;

import java.util.List;

public class AlarmOBotSpellC extends ReturnMinionToHandSpell {

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		// Check to see if there is a minion before returning to hand!
		// If there is no minion, do not activate!
		if (!player.getHand().hasCardOfType(CardType.MINION)) {
			return;
		}
		// Summon a random minion and remove the corresponding card
		// before adding Alarm-o-bot to your hand!

		//CardCollection MinionCards = player.getHand();
		//MinionCard randomMinionCard = (MinionCard) player.getHand().getRandomOfType(CardType.MINION);
		Object possibility = desc.get(SpellArg.POSSIBILITY);
		context.getLogic().removeCard(player.getId(), (MinionCard)possibility);
		context.getLogic().summon(player.getId(), ((MinionCard)possibility).summon());
		// return Alarm-o-bot to hand (Now it's safe!)
		super.onCast(context, player, desc, source, target);
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source){
        return (List<Object>)(List<?>)player.getHand().toList();
    }

}

