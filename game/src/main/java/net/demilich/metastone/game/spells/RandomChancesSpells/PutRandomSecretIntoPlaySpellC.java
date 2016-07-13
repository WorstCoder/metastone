package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.cards.SecretCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.trigger.secrets.Secret;

import java.util.List;

public class PutRandomSecretIntoPlaySpellC extends Spell {

	private CardCollection findSecretCards(CardCollection cardCollection) {
		CardCollection secretCards = new CardCollection();
		for (Card card : cardCollection) {
			if (card.hasAttribute(Attribute.SECRET)) {
				secretCards.add(card);
			}
		}
		return secretCards;
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		int howMany = desc.getValue(SpellArg.HOW_MANY, context, player, target, source, 1);
		for (int i = 0; i < howMany; i++) {
			SecretCard secretCard = (SecretCard)desc.get(SpellArg.POSSIBILITY);
			if (!context.getLogic().canPlaySecret(player, secretCard)) {
				return;
			}
			SpellDesc secretSpellDesc = secretCard.getSpell();
			Secret secret = (Secret) secretSpellDesc.get(SpellArg.SECRET);
			context.getLogic().playSecret(player, secret, false);
			context.getLogic().removeCard(player.getId(), secretCard);
			player.getDeck().remove(secretCard);

		}
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets){
		CardCollection secretCards = findSecretCards(player.getDeck());
		return (List<Object>)(List<?>) secretCards.toList();

	}

}