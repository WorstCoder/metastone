package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.spells.RandomChancesSpells.SynergyGameContext;
import net.demilich.metastone.game.spells.RandomChancesSpells.SynergyGameLogic;
import net.demilich.metastone.game.targeting.CardLocation;

import java.util.ArrayList;
import java.util.List;
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
        Card card1 = CardCatalogue.getCardByName("Flame Juggler");
        Card card2 = CardCatalogue.getEveryCardByName("Boom Bot");
        Card card3 = CardCatalogue.getCardByName("Tinkmaster Overspark");
        Card card4 = CardCatalogue.getCardByName("Piloted Sky Golem");


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
        context.getLogic().receiveCard(player1.getId(), card3);
        context.getLogic().receiveCard(player1.getId(), card4);

        playCard(card1);

        playCard(card2);
        logic.destroy(context.getPlayer1().getMinions().stream().filter(m -> m.getName().equals(card2.getName())).findFirst().get());
        playCard(card3);
        playCard(card4);

        context.endTurn();
        context.startTurn(context.getActivePlayerId());
        context.playTurn();
        context.endTurn();

        System.out.print(context.toString());

    }

    private void fillWithDummies(Player player) {
        player.getHand().addCardsList(dummies.stream().filter(d -> d.getBaseManaCost() < 6).peek(d -> d.setLocation(CardLocation.HAND)).collect(Collectors.toList()));
        new ArrayList<>(dummies).stream().filter(d -> d.getBaseManaCost()<4).forEach(d -> {context.getLogic().summon(player.getId(),((MinionCard)d).summon()); d.setLocation(CardLocation.GRAVEYARD);});
    }

    private void setOwner(Player player) {
        player.getHand().toList().forEach(c -> c.setOwner(player.getId()));
        player.getDeck().toList().forEach(c -> c.setOwner(player.getId()));
        player.getSetAsideZone().forEach(c -> c.setOwner(player.getId()));
    }

    private void playCard(Card card){
        SynergyGameLogic logic = context.getLogic();
        Player player = context.getPlayer(card.getOwner());
        player.setMana(10);
        if(card.getLocation()==CardLocation.GRAVEYARD) {
            card.setLocation(CardLocation.HAND);
            player.getHand().add(card);
            player.getGraveyard().remove(card);}
        if(logic.canPlayCard(card.getOwner(),card.getCardReference())) logic.performGameAction(card.getOwner(), card.play());
    }


}
