package net.demilich.metastone.game.synergy.spells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.*;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.TransformMinionSpell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;
import net.demilich.metastone.game.synergy.SynergyGameContext;

import java.util.List;
import java.util.Map;

public class TransformToRandomMinionSpellC extends TransformMinionSpell {

	public static net.demilich.metastone.game.spells.desc.SpellDesc create() {
		Map<SpellArg, Object> arguments = SpellDesc.build(TransformToRandomMinionSpellC.class);
		return new net.demilich.metastone.game.spells.desc.SpellDesc(arguments);
	}

	@Override
	protected void onCast(GameContext context, Player player, net.demilich.metastone.game.spells.desc.SpellDesc desc, Entity source, Entity target) {

		MinionCard randomCard = (MinionCard) desc.get(SpellArg.POSSIBILITY);
		if (randomCard != null) {
			SpellDesc transformMinionSpell = TransformMinionSpell.create(randomCard.getCardId());
			super.onCast(context, player, transformMinionSpell, source, target);
		}
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets){
		EntityFilter filter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);

		CardCollection allMinions = CardCatalogue.query(context.getDeckFormat(), CardType.MINION);
		CardCollection filteredMinions = new CardCollection();
		for (Card card : allMinions) {
			MinionCard minionCard = (MinionCard) card;
			if (filter == null || filter.matches(context, player, card)) {
				filteredMinions.add(minionCard);
			}
		}
		return (List<Object>)(List<?>) filteredMinions.toList();
	}

}
