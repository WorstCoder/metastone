package net.demilich.metastone.game.synergy.spells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.DamageSpell;
import net.demilich.metastone.game.spells.SpellUtils;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.valueprovider.RandomValueProvider;
import net.demilich.metastone.game.synergy.SynergyGameContext;

import java.util.ArrayList;
import java.util.List;

public class MissilesSpellC extends DamageSpell {

    @Override
    public void cast(GameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
        Object[] poss = (Object[]) desc.get(SpellArg.POSSIBILITY);

        Actor target = (Actor) poss[0];
        int damage = (Integer) poss[1];
        context.getLogic().damage(player, target, damage, source, true);
    }

    @Override
    protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
    }

    @Override
    public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
        List<Object[]> poss = new ArrayList<>();
        targets = context.getLogic().getTargetLogic().resolveTargetKey(context, player, source, desc.getTarget());
        List<Entity> validTargets = SpellUtils.getValidTargets(context, player, targets, desc.getEntityFilter());



        if (desc.contains(SpellArg.VALUE) && desc.get(SpellArg.VALUE).getClass() == RandomValueProvider.class) {
            int min = ((RandomValueProvider) desc.get(SpellArg.VALUE)).getMin();
            int max = ((RandomValueProvider) desc.get(SpellArg.VALUE)).getMax();
            for (int i = min; i <= max; i++) {
                for (Entity target : validTargets) {
                    Object[] newObj = new Object[2];
                    newObj[0] = target;
                    newObj[1] = i;
                    poss.add(newObj);
                }
            }
            return (List<Object>) (List<?>)poss;
        }

        for (Entity target : validTargets) {
            Object[] newObj = new Object[2];
            newObj[0] = target;
            newObj[1] = desc.get(SpellArg.VALUE);
            poss.add(newObj);
        }
        return (List<Object>)(List<?>)poss;
    }
}
