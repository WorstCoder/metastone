package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DiscardSpellC extends Spell {

    public static final int ALL_CARDS = -1;

    public static SpellDesc create() {
        return create(1);
    }

    public static SpellDesc create(int numberOfCards) {
        Map<SpellArg, Object> arguments = SpellDesc.build(DiscardSpellC.class);
        arguments.put(SpellArg.VALUE, numberOfCards);
        arguments.put(SpellArg.TARGET, EntityReference.NONE);
        return new SpellDesc(arguments);
    }

    @Override
    protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
        int numberOfCards = desc.getValue(SpellArg.VALUE, context, player, target, source, 1);
        int cardCount = numberOfCards == ALL_CARDS ? player.getHand().getCount() : numberOfCards;

        if ((int) desc.get(SpellArg.VALUE) == 1) {
            context.getLogic().discardCard(player, (Card) desc.get(SpellArg.POSSIBILITY));
        } else {
            for (int i = 0; i < cardCount; i++) {
                Card randomHandCard = ((List<Card>)desc.get(SpellArg.POSSIBILITY)).get(i);
                if (randomHandCard == null) {
                    return;
                }
                context.getLogic().discardCard(player, randomHandCard);
            }
        }
    }

    @Override
    public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
        CardCollection cards = player.getHand();
        List<List<Card>> cardsToDiscard = new ArrayList<>();

        if ((int) desc.get(SpellArg.VALUE) == 1) return (List<Object>) (List<?>) cards.toList();
        if ((int) desc.get(SpellArg.VALUE) == -1) {
            cardsToDiscard.add(cards.toList());
            return (List<Object>) (List<?>)cardsToDiscard ;}


        for (int i = 0; i < cards.toList().size(); i++) {
            for (int j = i; i < cards.toList().size(); j++) {
                Card card = cards.get(i);
                Card card1 = cards.get(j);
                if (card != card1) cardsToDiscard.add(new ArrayList<>(Arrays.asList(card, card1)));
            }
        }
        return (List<Object>) (List<?>) cardsToDiscard;
    }

}
