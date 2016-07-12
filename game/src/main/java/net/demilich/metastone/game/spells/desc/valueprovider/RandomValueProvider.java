package net.demilich.metastone.game.spells.desc.valueprovider;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;

public class RandomValueProvider extends ValueProvider {

	public RandomValueProvider(ValueProviderDesc desc) {
		super(desc);
	}

	@Override
	protected int provideValue(GameContext context, Player player, Entity target, Entity host) {
		int min = desc.getInt(ValueProviderArg.MIN);
		int max = desc.getInt(ValueProviderArg.MAX);
		int diff = max - min;
		return min + context.getLogic().random(diff + 1);
	}

	public int getMin(){
		return desc.getInt(ValueProviderArg.MIN);
	}

	public int getMax(){
		return desc.getInt(ValueProviderArg.MAX);
	}
}
