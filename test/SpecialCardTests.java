import java.util.List;

import net.pferdimanzug.hearthstone.analyzer.game.GameContext;
import net.pferdimanzug.hearthstone.analyzer.game.Player;
import net.pferdimanzug.hearthstone.analyzer.game.actions.GameAction;
import net.pferdimanzug.hearthstone.analyzer.game.actions.PhysicalAttackAction;
import net.pferdimanzug.hearthstone.analyzer.game.cards.Card;
import net.pferdimanzug.hearthstone.analyzer.game.cards.MinionCard;
import net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.druid.SavageRoar;
import net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.mage.ArcaneExplosion;
import net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.neutral.FaerieDragon;
import net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.neutral.GurubashiBerserker;
import net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.neutral.OasisSnapjaw;
import net.pferdimanzug.hearthstone.analyzer.game.entities.Actor;
import net.pferdimanzug.hearthstone.analyzer.game.entities.Entity;
import net.pferdimanzug.hearthstone.analyzer.game.entities.heroes.Garrosh;
import net.pferdimanzug.hearthstone.analyzer.game.entities.heroes.Hero;
import net.pferdimanzug.hearthstone.analyzer.game.entities.heroes.Jaina;
import net.pferdimanzug.hearthstone.analyzer.game.entities.heroes.Malfurion;

import org.testng.Assert;
import org.testng.annotations.Test;


public class SpecialCardTests extends TestBase {
	
	@Test
	public void testGurubashiBerserker() {
		GameContext context = createContext(new Jaina(), new Garrosh());
		Player mage = context.getPlayer1();
		mage.setMana(10);
		Player warrior = context.getPlayer2();
		warrior.setMana(10);

		MinionCard gurubashiBerserkerCard = new GurubashiBerserker();
		warrior.getHand().add(gurubashiBerserkerCard);
		context.getLogic().performGameAction(warrior.getId(), gurubashiBerserkerCard.play());
		
		MinionCard oasisSnapjawCard = new OasisSnapjaw();
		mage.getHand().add(oasisSnapjawCard);
		context.getLogic().performGameAction(mage.getId(), oasisSnapjawCard.play());
		
		Actor attacker = getSingleMinion(mage.getMinions());
		Actor defender = getSingleMinion(warrior.getMinions());
		
		// Gurubashi Berserker should start with just his base attack
		Assert.assertEquals(defender.getAttack(), GurubashiBerserker.BASE_ATTACK);
		
		// first attack, Gurubashi Berserker should have increased attack
		GameAction attackAction = new PhysicalAttackAction(attacker.getReference());
		attackAction.setTarget(defender);
		context.getLogic().performGameAction(mage.getId(), attackAction);
		
		Assert.assertEquals(attacker.getHp(), attacker.getMaxHp() - GurubashiBerserker.BASE_ATTACK);
		Assert.assertEquals(defender.getHp(), defender.getMaxHp() - attacker.getAttack());
		Assert.assertEquals(defender.getAttack(), GurubashiBerserker.BASE_ATTACK + GurubashiBerserker.ATTACK_BONUS);
		
		// second attack, Gurubashi Berserker should become even stronger
		context.getLogic().performGameAction(mage.getId(), attackAction);
		Assert.assertEquals(attacker.getHp(), attacker.getMaxHp() - 2 * GurubashiBerserker.BASE_ATTACK - GurubashiBerserker.ATTACK_BONUS);
		Assert.assertEquals(defender.getHp(), defender.getMaxHp() - 2 * attacker.getAttack());
		Assert.assertEquals(defender.getAttack(), GurubashiBerserker.BASE_ATTACK + 2 * GurubashiBerserker.ATTACK_BONUS);
	}
	
	@Test
	public void testFaerieDragon() {
		GameContext context = createContext(new Jaina(), new Garrosh());
		Player mage = context.getPlayer1();
		mage.setMana(10);
		Player warrior = context.getPlayer2();
		warrior.setMana(10);

		MinionCard faerieDragonCard = new FaerieDragon();
		context.getLogic().receiveCard(warrior.getId(), faerieDragonCard);
		context.getLogic().performGameAction(warrior.getId(), faerieDragonCard.play());
		
		MinionCard devMonsterCard = new DevMonster(1, 1);
		context.getLogic().receiveCard(mage.getId(), devMonsterCard);
		context.getLogic().performGameAction(mage.getId(), devMonsterCard.play());
		
		Entity attacker = getSingleMinion(mage.getMinions());
		Actor elusiveOne = getSingleMinion(warrior.getMinions());
		
		GameAction attackAction = new PhysicalAttackAction(attacker.getReference());
		List<Actor> validTargets = context.getLogic().getValidTargets(warrior.getId(), attackAction);
		// should be two valid targets: enemy hero and faerie dragon
		Assert.assertEquals(validTargets.size(), 2);
		
		
		GameAction useFireblast = mage.getHero().getHeroPower().play();
		validTargets = context.getLogic().getValidTargets(warrior.getId(), useFireblast);
		// should be three valid targets, both heroes + minion which is not the faerie dragon
		Assert.assertEquals(validTargets.size(), 3);
		Assert.assertFalse(validTargets.contains(elusiveOne));
		
		Card arcaneExplosionCard = new ArcaneExplosion();
		context.getLogic().receiveCard(mage.getId(), arcaneExplosionCard);
		int faerieDragonHp = elusiveOne.getHp();
		context.getLogic().performGameAction(mage.getId(), arcaneExplosionCard.play());
		// hp should been affected after playing area of effect spell
		Assert.assertNotEquals(faerieDragonHp, elusiveOne.getHp());
	}
	
	@Test
	public void testSavageRoar() {
		GameContext context = createContext(new Malfurion(), new Garrosh());
		Player player = context.getPlayer1();
		Hero druid = player.getHero();
		
		player.setMana(10);
		Player warrior = context.getPlayer2();
		warrior.setMana(10);

		MinionCard devMonsterCard = new DevMonster(1, 1);
		context.getLogic().receiveCard(player.getId(), devMonsterCard);
		context.getLogic().performGameAction(player.getId(), devMonsterCard.play());
		
		Actor minion = getSingleMinion(player.getMinions());
		
		context.getLogic().performGameAction(player.getId(), druid.getHeroPower().play());
		Assert.assertEquals(druid.getAttack(), 1);
		Assert.assertEquals(minion.getAttack(), 1);
		
		Card savageRoar = new SavageRoar();
		context.getLogic().receiveCard(player.getId(), savageRoar);
		context.getLogic().performGameAction(player.getId(), savageRoar.play());
		Assert.assertEquals(druid.getAttack(), 3);
		Assert.assertEquals(minion.getAttack(), 3);
		
		context.getLogic().endTurn(player.getId());
		Assert.assertEquals(druid.getAttack(), 0);
		Assert.assertEquals(minion.getAttack(), 1);
		
		context.getLogic().endTurn(player.getId());
		Assert.assertEquals(druid.getAttack(), 0);
		Assert.assertEquals(minion.getAttack(), 1);
	}
	
}
