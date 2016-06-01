package net.demilich.metastone.gui.bestofdecks;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DecksReader implements Callable<List<Deck>> {

    private Document doc, innerDoc;

    private String hearthPwnUrl = "http://www.hearthpwn.com", url;

    private int decksNum;
    private AtomicInteger decksReaded = new AtomicInteger(0);

    private HeroClass hero;

    public DecksReader(String url, HeroClass hero, int decksNum){
        this.url=url;
        this.decksNum=decksNum;
        this.hero=hero;
    }

    public List<Deck> call() throws Exception {
        List<Deck> decks = Collections.synchronizedList(new ArrayList<>());
            doc = Jsoup.connect(url).userAgent("Mozilla").get();
            Elements urlDecks = doc.getElementsByAttributeValue("class", "tip");
            ExecutorService executor = Executors.newFixedThreadPool(decksNum + 2, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setDaemon(true);
                    return t;
                }
            });
            CountDownLatch latch = new CountDownLatch(decksNum);
            for (int i=0;i<decksNum;i++) {
                int finalI = i;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Element element = urlDecks.get(finalI);
                        String name = element.children().text();
                        String innerDeckUrl = element.getElementsByTag("a").attr("href");

                        Deck deck = new Deck(hero);
                        deck.setName(name);
                        NotificationProxy.sendNotification(GameNotification.BEST_OF_WAIT_UPDATE,name);
                        List<Card> cardsCollection = Collections.synchronizedList(new ArrayList<>());

                        try {
                            innerDoc = Jsoup.connect(hearthPwnUrl + innerDeckUrl).userAgent("Mozilla").get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Elements cards = innerDoc.getElementsByAttribute("data-rarity");
                        CountDownLatch cardsLatch = new CountDownLatch(cards.size());
                        //for (Element card : cards)
                            for(int j=0;j<cards.size();j++){
                                int finalJ = j;
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        int cardNum = Integer.parseInt(cards.get(finalJ).attr("data-Count"));
                                        String cardName = cards.get(finalJ).text();
                                        Card cardToAdd = CardCatalogue.getCardByName(cardName);

                                        switch (cardNum) {
                                            case 2:
                                                //deck.getCards().add(cardToAdd);
                                                cardsCollection.add(cardToAdd);
                                            case 1:
                                                cardsCollection.add(cardToAdd);
                                                break;
                                        }
                                        cardsLatch.countDown();
                                    }
                                });
                            }
                        try {
                            cardsLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        deck.getCards().addCardsList(cardsCollection);
                        decks.add(deck);
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executor.shutdown();
        return decks;
    }

}
