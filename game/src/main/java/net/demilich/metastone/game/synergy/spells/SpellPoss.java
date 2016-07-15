package net.demilich.metastone.game.synergy.spells;

import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.MissilesSpell;
import net.demilich.metastone.game.spells.SpellUtils;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.valueprovider.RandomValueProvider;
import net.demilich.metastone.game.synergy.SynergyGameContext;

import java.util.ArrayList;
import java.util.List;

public interface SpellPoss {

    default List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
        List<Object> poss = new ArrayList<>();
        if (desc.contains(SpellArg.RANDOM_TARGET) && desc.contains(SpellArg.TARGET) || desc.getSpellClass()== MissilesSpell.class) {
            targets =  context.getLogic().getTargetLogic().resolveTargetKey(context, player, source, desc.getTarget());
            List<Entity> validTargets = SpellUtils.getValidTargets(context, player, targets, desc.getEntityFilter());
            return (List<Object>) (List<?>)validTargets;
        }
        if (desc.contains(SpellArg.VALUE) && desc.get(SpellArg.VALUE).getClass() == RandomValueProvider.class
                || desc.contains(SpellArg.ATTACK_BONUS) && desc.get(SpellArg.ATTACK_BONUS).getClass() == RandomValueProvider.class) {

            int min;
            int max;
            if(desc.contains(SpellArg.ATTACK_BONUS)){
                min = ((RandomValueProvider) desc.get(SpellArg.ATTACK_BONUS)).getMin();
                max = ((RandomValueProvider) desc.get(SpellArg.ATTACK_BONUS)).getMax();
                 }else{
            min = ((RandomValueProvider) desc.get(SpellArg.VALUE)).getMin();
            max = ((RandomValueProvider) desc.get(SpellArg.VALUE)).getMax();}
            for (int i = min; i <= max; i++) {
                SpellDesc newSpell = desc.clone();
                if(desc.contains(SpellArg.ATTACK_BONUS)){
                    newSpell.delArg(SpellArg.ATTACK_BONUS);
                    newSpell.putArg(SpellArg.ATTACK_BONUS,i);
                }else{
                newSpell.delArg(SpellArg.VALUE);
                newSpell.putArg(SpellArg.VALUE,i);}
                poss.add(newSpell);
            }
            return poss;
        }
        return null;
    }

}
