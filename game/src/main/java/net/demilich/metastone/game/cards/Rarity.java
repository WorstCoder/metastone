package net.demilich.metastone.game.cards;

public enum Rarity {
	FREE,
	COMMON,
	RARE,
	EPIC,
	LEGENDARY,
	ANY;
	
	public boolean isRarity(Rarity rarity) {
		if (this == FREE && rarity == COMMON) {
			return true;
		} else if (this == rarity) {
			return true;
		} else if (rarity == ANY) {return true;}
		return false;
	}

}
