package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.DamageSpell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MultiTargetDamageSpellC extends DamageSpell {

	public static net.demilich.metastone.game.spells.desc.SpellDesc create(int damage, int targets) {
		Map<SpellArg, Object> arguments = SpellDesc.build(MultiTargetDamageSpellC.class);
		arguments.put(SpellArg.VALUE, damage);
		arguments.put(SpellArg.HOW_MANY, targets);
		return new net.demilich.metastone.game.spells.desc.SpellDesc(arguments);
	}

	@Override
	protected void onCast(GameContext context, Player player, net.demilich.metastone.game.spells.desc.SpellDesc desc, Entity source, Entity target) {
		int damage = desc.getValue(SpellArg.VALUE, context, player, target, source, 0);
		int targets = desc.getValue(SpellArg.HOW_MANY, context, player, target, source, 2);
		List<Minion> validTargets = (List<Minion>)desc.get(SpellArg.POSSIBILITY);

		for(Minion minion : validTargets){
			context.getLogic().damage(player, minion, damage, source);
		}
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targetsx){
		List<Minion> validTargets = new ArrayList<>(context.getOpponent(player).getMinions());
		List<List<Minion>> targets = new ArrayList<>();
		for(int i=0;i<validTargets.size();i++){
			for(int j=i;j<validTargets.size();j++){
				if(i!=j) targets.add(new ArrayList<>(Arrays.asList(validTargets.get(i),validTargets.get(j))));
			}
		}
		return (List<Object>)(List<?>) targets;
	}
}
