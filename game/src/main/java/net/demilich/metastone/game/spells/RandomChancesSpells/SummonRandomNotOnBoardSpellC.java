package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;

import java.util.ArrayList;
import java.util.List;

public class SummonRandomNotOnBoardSpellC extends Spell {

	private static boolean alreadyOnBoard(List<Minion> minions, String id) {
		for (Minion minion : minions) {
			if (minion.getSourceCard().getCardId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {

		String randomMinionId = (String)desc.get(SpellArg.POSSIBILITY);
		MinionCard randomMinionCard = (MinionCard) CardCatalogue.getCardById(randomMinionId);
		context.getLogic().summon(player.getId(), randomMinionCard.summon());
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, net.demilich.metastone.game.spells.desc.SpellDesc desc, Entity source){
		String[] minionCardsId = (String[]) desc.get(SpellArg.CARDS);
		List<String> eligibleMinions = new ArrayList<String>();
		for (String minion : minionCardsId) {
			if (!alreadyOnBoard(player.getMinions(), minion)) {
				eligibleMinions.add(minion);
			}
		}
		return (List<Object>) (List<?>) eligibleMinions;
	}

}
