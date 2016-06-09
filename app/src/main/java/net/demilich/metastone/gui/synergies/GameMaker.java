package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.*;
import net.demilich.metastone.game.cards.desc.MinionCardDesc;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.Hero;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.entities.minions.Race;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.game.logic.GameLogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GameMaker {

    private DeckFormat deckFormat;

    private HeroClass heroClass;

    private HeroClass enemyClass;

    private GameContext game;

    private List<Card> dummies = new ArrayList<>();

    private CardCollection dummiesCollection = new CardCollection();

    private CardCollection cards;

    private PlayerConfig playerConfig1 = new PlayerConfig();

    private PlayerConfig playerConfig2 = new PlayerConfig();

    private Player player1;

    private Player player2;

    private ContextTypes type;

    public GameMaker(DeckFormat format, HeroClass hero, HeroClass enemyHero, ContextTypes type){
        this.deckFormat = format;
        this.heroClass = hero;
        this.enemyClass = enemyHero;
        this.type=type;
        MakeDummies();
        SetupPlayers();
        SetupCards();
        SetupGames();
    }

    public GameMaker(DeckFormat format, HeroClass hero, ContextTypes type){
        this(format,hero,HeroClass.MAGE,type);

    }

    private void MakeDummies(){
        //int j=1;
        for(int i=1;i<10;i++){
            MinionCardDesc dummyDesc = new MinionCardDesc();
            dummyDesc.baseAttack = i;
            dummyDesc.baseHp = i;
            dummyDesc.baseManaCost = i;
            dummyDesc.name="Dummy " + i;
            //if(j<8 && j!=2) {dummyDesc.race=Race.values()[j++];
            //dummyDesc.name="Dummy " + i +" - " + dummyDesc.race.toString();} else j++;
            dummies.add(new MinionCard(dummyDesc));
        }
        dummiesCollection.addCardsList(dummies);
    }

    private Deck DummyDeck(HeroClass heroClass){
        Deck deck = new Deck(heroClass);
        CardCollection collection = new CardCollection();
        for(int i=0;i<3;i++) {
            collection.addAll(dummiesCollection);}
        collection.addAll(CardCatalogue.query(deckFormat, CardType.SPELL,Rarity.ANY,heroClass, Attribute.SECRET));
        deck.getCards().addAll(collection);
        return deck;
    }

    private void SetupPlayers(){
        playerConfig1.setBehaviour(new GameStateValueBehaviour());
        playerConfig2.setBehaviour(new GameStateValueBehaviour());
        playerConfig1.setDeck(DummyDeck(heroClass));
        playerConfig2.setDeck(DummyDeck(enemyClass));
        playerConfig1.setName("P1");
        playerConfig2.setName("P2");
        SetHero(playerConfig1);
        SetHero(playerConfig2);
        player1 = new Player(playerConfig1);
        player2 = new Player(playerConfig2);
    }

    private void SetupCards(){
        cards = CardCatalogue.query(deckFormat,heroClass);
        cards.addAll(CardCatalogue.query(deckFormat,HeroClass.ANY));
    }

    private void SetupGames(){
        game = new GameContext(player1,player2,new GameLogic(), deckFormat);
        if(type==ContextTypes.EMPTY){return;}
        if(type==ContextTypes.ENEMY_DUMMIES){
            for(int i=0;i<5;i++){
                if (i==1) i++;
                game.getLogic().summon(1,new Minion((MinionCard) dummies.get(i)));
            }
        }
    }

    private void SetHero(PlayerConfig config){
        if(config.getHeroCard() == null || config.getDeck().getHeroClass().toString() != config.getHeroCard().getClassRestriction().name()) {
            for (Card card : CardCatalogue.getHeroes()) {
                if (config.getDeck().getHeroClass().toString() == card.getClassRestriction().name()) {
                    config.setHeroCard((HeroCard) card);
                }
            }
        }
    }

    public GameContext getGame(){return game;}
}
