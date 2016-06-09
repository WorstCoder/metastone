package net.demilich.metastone.gui.bestofdecks;

import com.sun.glass.ui.SystemClipboard;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import javafx.application.Platform;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.HeroCard;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.deckbuilder.DeckFormatProxy;
import net.demilich.metastone.gui.simulationmode.PlayerConfigView;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class StartSimulation {
    private PlayerConfig playerConfig1;
    private PlayerConfig playerConfig2;
    private List<Deck> decks;
    private DeckFormat format;
    private Player player1;
    private Player player2;
    private int nuberOfGames;
    private IBehaviour behaviour;
    private List<BestOfResults> results = Collections.synchronizedList(new ArrayList<>());
    private List<Double> times = Collections.synchronizedList(new ArrayList<>());
    private AtomicInteger gamesCounter = new AtomicInteger(0);
    private int gamesTotal;



    public StartSimulation(List<Deck> decks, int nuberOfGames, DeckFormat format, IBehaviour behaviour){
        this.decks=decks;
        this.nuberOfGames=nuberOfGames;
        this.format=format;
        this.behaviour = behaviour;
        setTotalGames();
        NotificationProxy.sendNotification(GameNotification.BEST_OF_WAIT_UPDATE_TOP,"Simulating");
        NotificationProxy.sendNotification(GameNotification.BEST_OF_WAIT_UPDATE,"...");
    }

    public void Simulation(){
        long time = System.currentTimeMillis();
        ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
        CountDownLatch p1Counter = new CountDownLatch(decks.size());
        for(int p1=0; p1<decks.size(); p1++) {
            int finalP1 = p1;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    PlayerConfig playerConfig1P1 = new PlayerConfig(decks.get(finalP1),behaviour);
                    SetHero(playerConfig1P1);
                    Player player1P1 = new Player(playerConfig1P1);

                    CountDownLatch p2Counter = new CountDownLatch(decks.size()-(finalP1+1));
                    for(int p2 = finalP1+1; p2<decks.size(); p2++) {
                        int finalP2 = p2;
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                PlayerConfig playerConfig2P2 = new PlayerConfig(decks.get(finalP2),behaviour);
                                SetHero(playerConfig2P2);
                                Player player2P2 = new Player(playerConfig2P2);

                                CountDownLatch p3Counter = new CountDownLatch(nuberOfGames);
                                for(int p3=0; p3<nuberOfGames; p3++){
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Long runTime = System.currentTimeMillis();
                                            Player player1P3 = player1P1.clone();
                                            Player player2P3 = player2P2.clone();
                                            GameContext newGame = new GameContext(player1P3, player2P3, new GameLogic(), format);
                                            newGame.play();
                                            results.add(new BestOfResults(newGame.getPlayer1(),newGame.getPlayer2(),newGame.getWinningPlayerId()));
                                            newGame.dispose();
                                            times.add((double)((System.currentTimeMillis() - runTime)/1000));
                                            NotificationProxy.sendNotification(GameNotification.BEST_OF_WAIT_UPDATE,
                                                    gamesCounter.addAndGet(1) + "/" + gamesTotal + " Done" + "\n" + player1P3.getDeckName() +"\n VS. \n" + player2P3.getDeckName());
                                            p3Counter.countDown();
                                        }
                                    });
                                }
                                try {
                                    p3Counter.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                p2Counter.countDown();
                            }
                        });
                    }
                    try {
                        p2Counter.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    p1Counter.countDown();
                }
            });
        }
        try {
            p1Counter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        times.add(times.stream().mapToDouble(val -> val).average().getAsDouble());
        long newtime = (System.currentTimeMillis() - time) / 1000;
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

    public List<BestOfResults> getResults(){
        return results;
    }

    private void setTotalGames(){
        int numOfDecks = this.decks.size();
        gamesTotal = (int)(factorial(numOfDecks).divide(factorial(numOfDecks-2).multiply(BigInteger.valueOf(2))).multiply(BigInteger.valueOf(nuberOfGames))).floatValue();
    }

    private BigInteger factorial(int n){
        BigInteger fact = BigInteger.valueOf(1);
        for (int i=1; i<=n; i++){
            fact=fact.multiply(BigInteger.valueOf(i));
        }
        return fact;
    }
}

