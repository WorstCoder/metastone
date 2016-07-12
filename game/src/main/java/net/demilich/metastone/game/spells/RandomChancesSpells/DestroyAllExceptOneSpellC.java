package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.DestroySpell;
import net.demilich.metastone.game.spells.SpellUtils;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DestroyAllExceptOneSpellC extends DestroySpell {
	
	public static Logger logger = LoggerFactory.getLogger(DestroyAllExceptOneSpellC.class);

	@Override
	public void cast(GameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
		Entity minion = (Entity) desc.get(SpellArg.POSSIBILITY);
			onCast(context, player, null, null, minion);
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source){
		return (List<Object>)(List<?>)context.getLogic().getTargetLogic().resolveTargetKey(context, player, source, desc.getTarget());
	}


}
