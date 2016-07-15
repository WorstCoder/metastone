package net.demilich.metastone.game.synergy.spells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.DestroySpell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.synergy.SynergyGameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DestroyAllExceptOneSpellC extends DestroySpell {
	
	public static Logger logger = LoggerFactory.getLogger(DestroyAllExceptOneSpellC.class);

	@Override
	public void cast(GameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
		Entity minion = (Entity) desc.get(SpellArg.POSSIBILITY);
			onCast(context, player, null, null, minion);
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets){
		return (List<Object>)(List<?>)context.getLogic().getTargetLogic().resolveTargetKey(context, player, source, desc.getTarget());
	}
}
