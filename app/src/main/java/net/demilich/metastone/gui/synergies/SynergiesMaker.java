package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.TurnState;
import net.demilich.metastone.game.actions.PlayCardAction;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.game.targeting.CardLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SynergiesMaker {

    private DeckFormat format;

    private HeroClass heroClass;

    private CardCollection cards;

    private SynergyGameContext context;

    private GameMaker gameMaker;

    private List<Card> dummies;

    private List<Synergy> synergies = new ArrayList<>();


    public SynergiesMaker(DeckFormat format, HeroClass hero) {
        this.format = format;
        this.heroClass = hero;

        this.gameMaker = new GameMaker(format, heroClass);
        context = (SynergyGameContext) gameMaker.getGame();
        dummies = gameMaker.getDummies();

        SetupCards();
        //GameLogic useHeroPower
        //cards.add(CardCatalogue.getHeroPowers().toList().stream().filter(hp -> hp.getClassRestriction()==heroClass).findFirst().get());
    }

    private void SetupCards() {
        cards = CardCatalogue.query(format, heroClass);
        cards.addAll(CardCatalogue.query(format, HeroClass.ANY));
    }

    public void start() {
        Card card1 = CardCatalogue.getCardByName("Knife Juggler");
        Card card2 = CardCatalogue.getCardByName("Murloc Tidehunter");


        SynergyGameLogic logic = (SynergyGameLogic) context.getLogic();
        Player player1 = context.getPlayer1();
        Player player2 = context.getPlayer2();

        fillWithDummies(player2);


        logic.init(player1.getId());
        logic.init(player2.getId());

        setOwner(player2);

        player1.setMana(10);
        player1.setMaxMana(10);

        context.setActivePlayer(player1);
        context.startTurn(context.getActivePlayerId());

        context.getLogic().receiveCard(player1.getId(), card1);
        context.getLogic().receiveCard(player1.getId(), card2);

        logic.performGameAction(card1.getOwner(), card1.play());
        logic.performGameAction(card2.getOwner(), card2.play());

        context.endTurn();
        context.startTurn(context.getActivePlayerId());
        context.playTurn();
        context.endTurn();

        System.out.print(context.toString());

    }

    private void fillWithDummies(Player player) {
        player.getHand().addCardsList(dummies.stream().filter(d -> d.getBaseManaCost() < 6).peek(d -> d.setLocation(CardLocation.HAND)).collect(Collectors.toList()));
        new ArrayList<>(dummies).stream().filter(d -> d.getBaseManaCost()<5).forEach(d -> {context.getLogic().summon(player.getId(),((MinionCard)d).summon()); d.setLocation(CardLocation.GRAVEYARD);});
    }

    private void setOwner(Player player) {
        player.getHand().toList().forEach(c -> c.setOwner(player.getId()));
        player.getDeck().toList().forEach(c -> c.setOwner(player.getId()));
        player.getSetAsideZone().forEach(c -> c.setOwner(player.getId()));
    }
}
