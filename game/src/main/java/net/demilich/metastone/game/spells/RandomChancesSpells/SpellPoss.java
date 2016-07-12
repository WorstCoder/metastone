package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.SpellUtils;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.valueprovider.RandomValueProvider;
import net.demilich.metastone.game.spells.desc.valueprovider.ValueProvider;
import net.demilich.metastone.game.spells.desc.valueprovider.ValueProviderArg;
import net.demilich.metastone.game.targeting.EntityReference;

import java.util.ArrayList;
import java.util.List;

public interface SpellPoss {

    default List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source) {
        List<Object> poss = new ArrayList<>();
        if (desc.contains(SpellArg.RANDOM_TARGET) && desc.contains(SpellArg.TARGET)) {
            List<Entity> targets =  context.getLogic().getTargetLogic().resolveTargetKey(context, player, source, desc.getTarget());
            List<Entity> validTargets = SpellUtils.getValidTargets(context, player, targets, desc.getEntityFilter());
            return (List<Object>) (List<?>)validTargets;
        }
        if (desc.contains(SpellArg.VALUE) && desc.get(SpellArg.VALUE).getClass() == RandomValueProvider.class
                || desc.contains(SpellArg.ATTACK_BONUS) && desc.get(SpellArg.ATTACK_BONUS).getClass() == RandomValueProvider.class) {
            int min = ((RandomValueProvider) desc.get(SpellArg.VALUE)).getMin();
            int max = ((RandomValueProvider) desc.get(SpellArg.VALUE)).getMax();
            for (int i = min; i <= max; i++) {
                poss.add(i);
            }
            return poss;
        }
        return null;
    }

}
