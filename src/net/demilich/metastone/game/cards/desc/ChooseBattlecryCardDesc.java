package net.demilich.metastone.game.cards.desc;

import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.ChooseBattlecryCard;
import net.demilich.metastone.game.spells.desc.BattlecryDesc;

public class ChooseBattlecryCardDesc extends MinionCardDesc {
	
	public BattlecryDesc option1;
	public BattlecryDesc option2;

	@Override
	public Card createInstance() {
		return new ChooseBattlecryCard(this);
	}


}
