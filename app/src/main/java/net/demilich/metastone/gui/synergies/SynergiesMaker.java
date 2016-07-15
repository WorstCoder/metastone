package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PlayCardAction;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.synergy.SynergyGameContext;
import net.demilich.metastone.game.synergy.SynergyGameLogic;
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
        playAll();

        Card card1 = CardCatalogue.getCardByName("Crackle");
        Card card2 = CardCatalogue.getEveryCardByName("Boom Bot");
        Card card3 = CardCatalogue.getCardByName("Tinkmaster Overspark");
        Card card4 = CardCatalogue.getCardByName("Piloted Sky Golem");


        SynergyGameLogic logic = (SynergyGameLogic) context.getLogic();
        Player player1 = context.getPlayer1();
        Player player2 = context.getPlayer2();

        fillWithDummies(context,player2);


        logic.init(player1.getId());
        logic.init(player2.getId());

        setOwner(context,player2);

        player1.setMana(10);
        player1.setMaxMana(10);

        context.setActivePlayer(player1);
        context.startTurn(context.getActivePlayerId());

        context.getLogic().receiveCard(player1.getId(), card1);
        context.getLogic().receiveCard(player1.getId(), card2);
        context.getLogic().receiveCard(player1.getId(), card3);
        context.getLogic().receiveCard(player1.getId(), card4);

        playCard(card1, context);

        playCard(card2, context);
        logic.destroy(context.getPlayer1().getMinions().stream().filter(m -> m.getName().equals(card2.getName())).findFirst().get());
        playCard(card3, context);
        playCard(card4, context);

        context.endTurn();
        context.startTurn(context.getActivePlayerId());
        context.playTurn();
        context.endTurn();

        System.out.print(context.toString());

    }

    private void fillWithDummies(SynergyGameContext context, Player player) {
        player.getHand().addCardsList(dummies.stream().filter(d -> d.getBaseManaCost() < 6).peek(d -> d.setLocation(CardLocation.HAND)).collect(Collectors.toList()));
        new ArrayList<>(dummies).stream().filter(d -> d.getBaseManaCost() < 5).forEach(d -> {
            context.getLogic().summon(player.getId(), ((MinionCard) d).summon());
            d.setLocation(CardLocation.GRAVEYARD);
        });
    }

    private void setOwner(SynergyGameContext context,Player player) {
        player.getHand().toList().forEach(c -> c.setOwner(player.getId()));
        player.getDeck().toList().forEach(c -> c.setOwner(player.getId()));
        player.getSetAsideZone().forEach(c -> c.setOwner(player.getId()));
    }

    private void playCard(Card card, SynergyGameContext context) {
        SynergyGameLogic logic = context.getLogic();
        Player player = context.getPlayer(card.getOwner());
        player.setMana(10);
        if (card.getLocation() == CardLocation.GRAVEYARD) {
            card.setLocation(CardLocation.HAND);
            player.getHand().add(card);
            player.getGraveyard().remove(card);
        }
        if (logic.canPlayCard(card.getOwner(), card.getCardReference())) {

            List<GameAction> validActions = context.getValidActions();
            List<GameAction> validCardActions = new ArrayList<>();

            for(GameAction action : validActions){
                if(action instanceof PlayCardAction && ((PlayCardAction)action).getCardReference().equals(card.getCardReference())){
                    validCardActions.add(action);
                }
            }

            //GameAction nextAction = context.getActivePlayer().getBehaviour().requestAction(context, context.getActivePlayer(), validCardActions);

            /*while (!context.acceptAction(nextAction)) {
                nextAction = context.getActivePlayer().getBehaviour().requestAction(context, context.getActivePlayer(), validCardActions);
            }*/

            logic.performGameAction(card.getOwner(), validCardActions.get(0));
        }
    }

    private void playAll() {
        List<Card> cards = GetRandomSpells.getListCards();
        for (Card card : cards) {

            Card card1 = CardCatalogue.getCardByName("Twisting Nether");

            SynergyGameContext contextClone = context.clone();

            SynergyGameLogic logicClone = (SynergyGameLogic) contextClone.getLogic();

            fillWithDummies(contextClone,contextClone.getPlayer2());
            fillWithDummies(contextClone,contextClone.getPlayer1());

            logicClone.init(contextClone.getPlayer1().getId());
            logicClone.init(contextClone.getPlayer2().getId());

            setOwner(contextClone,contextClone.getPlayer2());
            setOwner(contextClone,contextClone.getPlayer1());

            contextClone.getPlayer1().setMana(10);
            contextClone.getPlayer1().setMaxMana(10);

            contextClone.setActivePlayer(contextClone.getPlayer1());
            contextClone.startTurn(contextClone.getActivePlayerId());

            contextClone.getLogic().receiveCard(contextClone.getPlayer1().getId(), card);
            contextClone.getLogic().receiveCard(contextClone.getPlayer1().getId(), card1);
            playCard(card, contextClone);


            contextClone.endTurn();
            contextClone.startTurn(contextClone.getActivePlayerId());
            contextClone.playTurn();
            contextClone.endTurn();
            contextClone.startTurn(contextClone.getActivePlayerId());
            contextClone.playTurn();
            playCard(card1, contextClone);
            //List<Entity> minions = (List<Entity>)(List<?>) contextClone.getPlayer1().getMinions();
            //Entity[] minion = (Entity[]) minions.toArray();
            //contextClone.getLogic().destroy(minion);
        }
    }


}
