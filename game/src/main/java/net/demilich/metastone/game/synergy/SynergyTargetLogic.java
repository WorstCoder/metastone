package net.demilich.metastone.game.synergy;


import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.logic.TargetLogic;

public class SynergyTargetLogic extends TargetLogic {

    public Entity findEntityById(GameContext context, int id) {
        int targetId = id;
        //Entity environmentResult = findInEnvironment(context, targetKey);
        //if (environmentResult != null) {
        //    return environmentResult;
        //}
        for (Player player : context.getPlayers()) {
            if (player.getHero().getId() == targetId) {
                return player.getHero();
            } else if (player.getHero().getWeapon() != null && player.getHero().getWeapon().getId() == targetId) {
                return player.getHero().getWeapon();
            }

            for (Actor minion : player.getMinions()) {
                if (minion.getId() == targetId) {
                    return minion;
                }
            }

            for (Entity entity : player.getGraveyard()) {
                if (entity.getId() == targetId) {
                    return entity;
                }
            }
            for (Entity entity : player.getSetAsideZone()) {
                if (entity.getId() == targetId) {
                    return entity;
                }
            }
        }

        Entity cardResult = findInCards(context.getPlayer1(), targetId);
        if (cardResult == null) {
            cardResult = findInCards(context.getPlayer2(), targetId);
        }
        if (cardResult != null) {
            return cardResult;
        }

        logger.error("Id " + targetId + " not found!");
        logger.error(context.toString());
        logger.error(context.getEnvironment().toString());
        throw new RuntimeException("Target not found exception: " + id);
    }

    private Entity findInCards(Player player, int targetId) {
        if (player.getHero().getHeroPower().getId() == targetId) {
            return player.getHero().getHeroPower();
        }
        for (Card card : player.getHand()) {
            if (card.getId() == targetId) {
                return card;
            }
        }
        for (Card card : player.getDeck()) {
            if (card.getId() == targetId) {
                return card;
            }
        }

        return null;
    }

}
