package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.*;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.SpellUtils;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;

import java.util.ArrayList;
import java.util.List;

public class SummonRandomMinionFilteredSpellC extends Spell {

	protected static MinionCard getRandomMatchingMinionCard(GameContext context, Player player, EntityFilter cardFilter, boolean includeUncollictible) {
		CardCollection relevantMinions = null;
		if (includeUncollictible) {
			relevantMinions = CardCatalogue.query(context.getDeckFormat(), card -> cardFilter.matches(context, player, card));
		} else {
			CardCollection allMinions = CardCatalogue.query(context.getDeckFormat(), CardType.MINION);
			relevantMinions = new CardCollection();
			for (Card card : allMinions) {
				if (cardFilter.matches(context, player, card)) {
					relevantMinions.add(card);
				}
			}
		}
		
		return (MinionCard) relevantMinions.getRandom();
	}


	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		//EntityFilter cardFilter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);
		//boolean includeUncollectible = desc.getBool(SpellArg.INCLUDE_UNCOLLECTIBLE);
				
		int boardPosition = SpellUtils.getBoardPosition(context, player, desc, source);
		MinionCard minionCard = (MinionCard)desc.get(SpellArg.POSSIBILITY);
		if (minionCard != null) {
			context.getLogic().summon(player.getId(), minionCard.summon(), null, boardPosition, false);
		}
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player,SpellDesc desc, Entity source, List<Entity> targets){
        List<Object> minions = new ArrayList<>();
        EntityFilter cardFilter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);
        CardCollection allMinions = CardCatalogue.query(context.getDeckFormat(), CardType.MINION);
        for (Card card : allMinions) {
            if (cardFilter.matches(context, player, card)) {
                minions.add(card);
            }
        }
        return minions;
	}

}
