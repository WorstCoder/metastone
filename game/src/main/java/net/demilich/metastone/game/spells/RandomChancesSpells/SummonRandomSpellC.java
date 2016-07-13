package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SummonRandomSpellC extends Spell {

	public static net.demilich.metastone.game.spells.desc.SpellDesc create(MinionCard... minionCards) {
		Map<SpellArg, Object> arguments = SpellDesc.build(SummonRandomSpellC.class);
		arguments.put(SpellArg.CARD, minionCards);
		return new net.demilich.metastone.game.spells.desc.SpellDesc(arguments);
	}

	@Override
	protected void onCast(GameContext context, Player player, net.demilich.metastone.game.spells.desc.SpellDesc desc, Entity source, Entity target) {

		String randomMinionId = (String)desc.get(SpellArg.POSSIBILITY);
		MinionCard randomMinionCard = (MinionCard) CardCatalogue.getCardById(randomMinionId);
		context.getLogic().summon(player.getId(), randomMinionCard.summon());
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player,SpellDesc desc, Entity source, List<Entity> targets){
		return (List<Object>)(List<?>) new ArrayList<String>(Arrays.asList((String[]) desc.get(SpellArg.CARDS)));
	}

}
