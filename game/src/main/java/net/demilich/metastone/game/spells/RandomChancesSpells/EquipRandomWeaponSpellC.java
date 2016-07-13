package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.cards.WeaponCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.weapons.Weapon;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.TargetPlayer;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

import java.util.List;
import java.util.Map;

public class EquipRandomWeaponSpellC extends Spell {

	public static SpellDesc create(TargetPlayer targetPlayer) {
		Map<SpellArg, Object> arguments = SpellDesc.build(EquipRandomWeaponSpellC.class);
		arguments.put(SpellArg.TARGET_PLAYER, targetPlayer);
		arguments.put(SpellArg.TARGET, EntityReference.NONE);
		return new net.demilich.metastone.game.spells.desc.SpellDesc(arguments);
	}

	@Override
	protected void onCast(GameContext context, Player player,SpellDesc desc, Entity source, Entity target) {
		//CardCollection allWeapons = CardCatalogue.query(context.getDeckFormat(), CardType.WEAPON);
		Object possibility = desc.get(SpellArg.POSSIBILITY);
		WeaponCard weaponCard = (WeaponCard)possibility;
		Weapon weapon = weaponCard.getWeapon();
		weapon.setBattlecry(null);

		context.getLogic().equipWeapon(player.getId(), weapon);
	}

	@Override
	public List<Object> getPossibilities(SynergyGameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets){
		return (List<Object>)(List<?>)CardCatalogue.query(context.getDeckFormat(), CardType.WEAPON).toList();
	}
}
