package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.game.targeting.CardLocation;

public class SynergyGameLogic extends GameLogic {

    public void init(int playerId){
        Player player = context.getPlayer(playerId);
        player.getHero().setId(idFactory.generateId());
        player.getHero().setOwner(player.getId());
        player.getHero().setMaxHp(player.getHero().getAttributeValue(Attribute.BASE_HP));
        player.getHero().setHp(player.getHero().getAttributeValue(Attribute.BASE_HP));

        player.getHero().getHeroPower().setId(idFactory.generateId());
        assignCardIds(player.getDeck());
        assignCardIds(player.getHand());

        log("Setting hero hp to {} for {}", player.getHero().getHp(), player.getName());

        player.getDeck().shuffle();
    }

    @Override
    protected void assignCardIds(CardCollection cardCollection) {
        for (Card card : cardCollection) {
            card.setId(idFactory.generateId());
            if(card.getLocation()==null) card.setLocation(CardLocation.DECK);
        }
    }

}
