package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.EntityType;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;

import java.util.ArrayList;
import java.util.List;

public class ResurrectSpellC extends Spell {

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {

			Minion resurrectedMinion = (Minion)desc.get(SpellArg.POSSIBILITY);
			MinionCard minionCard = (MinionCard) resurrectedMinion.getSourceCard();
			context.getLogic().summon(player.getId(), minionCard.summon());
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets){
		List<Minion> deadMinions = new ArrayList<>();
		EntityFilter cardFilter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);
		List<Entity> graveyard = new ArrayList<Entity>();
		graveyard.addAll(player.getGraveyard());
		for (Entity deadEntity : graveyard) {
			if (deadEntity.getEntityType() == EntityType.MINION) {
				if (cardFilter == null || cardFilter.matches(context, player, deadEntity)) {
					deadMinions.add((Minion) deadEntity);
				}
			}
		}
		return (List<Object>)(List<?>) deadMinions;
	}

}
