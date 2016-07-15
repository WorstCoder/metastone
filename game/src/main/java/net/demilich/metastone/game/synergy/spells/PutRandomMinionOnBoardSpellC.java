package net.demilich.metastone.game.synergy.spells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;
import net.demilich.metastone.game.synergy.SynergyGameContext;
import net.demilich.metastone.game.targeting.CardLocation;

import java.util.List;
import java.util.stream.Collectors;

public class PutRandomMinionOnBoardSpellC extends Spell {

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		EntityFilter cardFilter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);
		CardLocation cardLocation = (CardLocation) desc.get(SpellArg.CARD_LOCATION);
		MinionCard minionCard = (MinionCard)desc.get(SpellArg.POSSIBILITY);
		if (minionCard == null) {
			return;
		}

		// we need to remove the card temporarily here, because there are card interactions like Starving Buzzard + Desert Camel
		// which could result in the card being drawn while a minion is summoned
		if (cardLocation == CardLocation.DECK) {
			player.getDeck().remove(minionCard);
		}

		boolean summonSuccess = context.getLogic().summon(player.getId(), minionCard.summon());

		// re-add the card here if we removed it before
		if (cardLocation == CardLocation.DECK) {
			player.getDeck().add(minionCard);
		}

		if (summonSuccess) {
			if (cardLocation == CardLocation.HAND) {
				context.getLogic().removeCard(player.getId(), minionCard);
			} else {
				context.getLogic().removeCardFromDeck(player.getId(), minionCard);
			}
		}
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets){
		CardLocation cardLocation = (CardLocation) desc.get(SpellArg.CARD_LOCATION);
		EntityFilter cardFilter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);

		CardCollection collection = cardLocation == CardLocation.HAND ? player.getHand() : player.getDeck();
		if (cardFilter == null) {
			return (List<Object>)(List<?>) collection.toList();
		} else {
			return (List<Object>)(List<?>)  collection.toList().stream().filter(card -> cardFilter.matches(context, player, card)).collect(Collectors.toList());
		}
	}

}
