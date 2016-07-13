package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.SpellUtils;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;
import net.demilich.metastone.game.targeting.EntityReference;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomlyCastSpellC extends Spell {

    public static SpellDesc create(EntityReference target, SpellDesc... spells) {
        Map<SpellArg, Object> arguments = SpellDesc.build(RandomlyCastSpellC.class);
        arguments.put(SpellArg.SPELLS, spells);
        arguments.put(SpellArg.TARGET, target);
        return new SpellDesc(arguments);
    }

    @Override
    protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
        SpellDesc[] spells = (SpellDesc[]) desc.get(SpellArg.SPELLS);
        List<Object[]> list = (List<Object[]>)desc.get(SpellArg.POSSIBILITY);

        for(Object[] arr:list){
            SpellUtils.castChildSpell(context, player, spells[(Integer)arr[1]], source, (Entity)arr[0]);
        }
    }

    @Override
    public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
        SpellDesc[] spells = (SpellDesc[]) desc.get(SpellArg.SPELLS);
        targets =  context.getLogic().getTargetLogic().resolveTargetKey(context, player, source, desc.getTarget());
        List<List<Object[]>> poss = new ArrayList<>();
        List<List<Object[]>> tempPoss = new ArrayList<>();

        List<Object[]> jprdl = new ArrayList<>();
        for(Entity target:targets){
            Object[] minion= new Object[2];
            minion[0]=target;
            minion[1]=0;
            jprdl.add(minion);
        }
        poss.add(jprdl);

        for (int i = 0; i < targets.size(); i++) {
            for (List<Object[]> list : poss) {
                Entity entity = (Entity) list.get(i)[0];
                for (int j = 0; j < spells.length; j++) {
                    List<Object[]> newList = new ArrayList<>(list);
                    for(int x=0; x<newList.size(); x++){
                        newList.set(x,list.get(x).clone());
                    }
                    if (!((Entity) newList.get(i)[0]).hasAttribute((Attribute) spells[j].get(SpellArg.ATTRIBUTE))) {
                        newList.get(i)[1] = new Integer(j);
                        tempPoss.add(newList);
                    }
                }
            }
            poss.clear();
            poss.addAll(tempPoss);
            tempPoss.clear();
        }
        return (List<Object>) (List<?>) poss;
    }
}