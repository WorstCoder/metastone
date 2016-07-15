package net.demilich.metastone.game.synergy.spells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.*;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.SpellUtils;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.synergy.SynergyGameContext;

import java.util.List;

public class SummonRandomMinionFromSpellC extends Spell {

    @Override
    protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {


        int boardPosition = SpellUtils.getBoardPosition(context, player, desc, source);
        MinionCard minionCard = (MinionCard) desc.get(SpellArg.POSSIBILITY);
        context.getLogic().summon(player.getId(), minionCard.summon(), null, boardPosition, false);
    }

    @Override
    public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
        Card fromCard = SpellUtils.getCard(context, desc);
        CardCollection allMinions = CardCatalogue.query(context.getDeckFormat(), CardType.MINION);
        CardCollection relevantMinions = new CardCollection();
        for (Card card : allMinions) {
            if (context.getLogic().getModifiedManaCost(player, fromCard) == card.getBaseManaCost()) {
                relevantMinions.add(card);
            }
        }
        return (List<Object>) (List<?>) relevantMinions.toList();
    }
}
