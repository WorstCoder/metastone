package net.demilich.metastone.game.synergy;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.heroes.Hero;
import net.demilich.metastone.game.entities.minions.Minion;

import java.util.*;

public class Averager {


	public static SynergyGameContext average(List<SynergyGameContext> contexts) {

		int games = contexts.size();
		Sum playerSum = new Sum();
		Sum opponentSum = new Sum();

		for (SynergyGameContext context : contexts) {
			Player player = context.getActivePlayer();
			Player opponent = context.getOpponent(player);

			List<Entity> playerEntities = (List<Entity>) (List<?>) player.clone().getMinions();
			playerEntities.add(player.getHero());
			playerSum.add(playerEntities);

			List<Entity> opponentEntities = (List<Entity>) (List<?>) opponent.clone().getMinions();
			opponentEntities.add(opponent.getHero());
			opponentSum.add(opponentEntities);
		}

		playerSum.divide(games);
		playerSum.removeZeros();
		playerSum.removeZombies();

		opponentSum.divide(games);
		opponentSum.removeZeros();
		opponentSum.removeZombies();

		SynergyGameContext contextClone = contexts.get(0).clone();

		Player player = contextClone.getActivePlayer();
		player.setHero(playerSum.getHero());
		player.getMinions().clear();
		player.getMinions().addAll(playerSum.getMinions());

		Player opponent = contextClone.getOpponent(player);
		opponent.setHero(playerSum.getHero());
		opponent.getMinions().clear();
		opponent.getMinions().addAll(playerSum.getMinions());

		return contextClone;
	}


	private static class Sum {
		Map<Integer, Entity> allEntities = new HashMap<>();

		public void add(List<Entity> entities) {
			for (Entity entity : entities) {
				entity.setAttribute(Attribute.ALIVE, 1);
				if (!allEntities.containsKey(entity.getId())) {
					allEntities.put(entity.getId(), entity);
				} else {
					Entity sumEntity = allEntities.get(entity.getId());
					Map<Attribute, Object> attributes = entity.getAttributes();

					for (Attribute attr : attributes.keySet()) {
						if (sumEntity.getAttribute(attr) instanceof Number)
							sumEntity.modifyAttribute(attr, entity.getAttributeValue(attr));
					}
				}

			}

		}

		public void divide(Integer games) {
			for (Integer id : allEntities.keySet()) {
				Entity entity = allEntities.get(id);

				for (Attribute attr : entity.getAttributes().keySet()) {
					if (entity.getAttribute(attr) instanceof Number) {
						if (attr != Attribute.ALIVE) {
							entity.setAttribute(attr, (int) Math.rint((double)entity.getAttributeValue(attr) / (double)entity.getAttributeValue(Attribute.ALIVE)));
						} else {
							entity.setAttribute(attr, (int) Math.rint(entity.getAttributeValue(attr) / (double)games));
						}
					}
				}
			}
		}

		public void removeZeros() {
			for (Integer id : allEntities.keySet()) {
				Entity entity = allEntities.get(id);
				for (Attribute attr : entity.getAttributes().keySet()) {
					if (entity.getAttribute(attr) == (Object) 0) {
						entity.removeAttribute(attr);
					}
				}
			}
		}

		public void removeZombies() {
			allEntities
					.entrySet()
					.removeIf(e -> !e.getValue().hasAttribute(Attribute.ALIVE));
		}

		public List<Minion> getMinions() {
			List<Minion> minions = new ArrayList<>();
			for (Integer id : allEntities.keySet()) {
				if (allEntities.get(id) instanceof Minion) minions.add((Minion) allEntities.get(id));
			}
			return minions;
		}

		public Hero getHero() {
			for (Integer id : allEntities.keySet()) {
				if (allEntities.get(id) instanceof Hero) return (Hero) allEntities.get(id);
			}
			return null;
		}
	}
}
