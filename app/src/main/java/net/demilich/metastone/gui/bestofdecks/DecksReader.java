package net.demilich.metastone.gui.bestofdecks;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DecksReader {

    public List<Deck> decks = new ArrayList<>();

    private Document doc, innerDoc;

    private String hearthPwnUrl = "http://www.hearthpwn.com";

    public DecksReader(String url, int decksNum, HeroClass hero){

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();

            Elements urlDecks = doc.getElementsByAttributeValue("class","tip");
            int counter = 0;
            for (Element element : urlDecks) {
                if (counter==decksNum) break;
                String name = element.children().text();
                String innerDeckUrl = element.getElementsByTag("a").attr("href");

                Deck deck = new Deck(hero);
                deck.setName(name);

                innerDoc = Jsoup.connect(hearthPwnUrl+innerDeckUrl).userAgent("Mozilla").get();

                Elements cards = innerDoc.getElementsByAttribute("data-rarity");
                for(Element card : cards){
                    int cardNum = Integer.parseInt(card.attr("data-Count"));
                    String cardName = card.text();
                    Card cardToAdd = CardCatalogue.getCardByName(cardName);

                    switch (cardNum) {case 2: deck.getCards().add(cardToAdd); case 1: deck.getCards().add(cardToAdd); break;}
                }
                decks.add(deck);
                counter++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public List<Deck> GetDecks(){
        return decks;
    }
}
