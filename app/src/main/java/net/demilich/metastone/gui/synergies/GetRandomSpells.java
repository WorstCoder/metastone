package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.cards.desc.ActorCardDesc;
import net.demilich.metastone.game.cards.desc.HeroCardDesc;
import net.demilich.metastone.game.cards.desc.HeroPowerCardDesc;
import net.demilich.metastone.game.cards.desc.SpellCardDesc;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.spells.MetaSpell;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.metastone.utils.UserHomeMetastone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class GetRandomSpells{

    private Document doc;

    private List<String> list_cards = new ArrayList<>();
    private List<String> list_spells = new ArrayList<>();
    private String path = UserHomeMetastone.getPath() + "/synergies";

    private String url="http://hearthstone.gamepedia.com/Random_effect/Wild_format";

    public GetRandomSpells() {
        try {
            GetCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GetSpells();
    }

    private void GetCards () throws Exception{
        if(!new File(path + "/random_cards.ser").exists()){
        doc = Jsoup.connect(url).userAgent("Mozilla").get();
        Elements cards = doc.getElementsByAttributeValue("class", "to_hasTooltip");
        for (Element e : cards) {
            list_cards.add(e.text());
        }
            try
            {
                new File(path).mkdir();
                FileOutputStream fileOut =  new FileOutputStream(path + "/random_cards.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(list_cards);
                out.close();
                fileOut.close();
            }catch(IOException i)
            {
                i.printStackTrace();
            }
        }
        else {
            try
            {
                FileInputStream fileIn = new FileInputStream(path + "/random_cards.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                list_cards = (List<String>)in.readObject();
                in.close();
                fileIn.close();
            }catch(IOException i)
            {
                i.printStackTrace();
            }
        }
    }
    private void GetSpells(){
        for(String cardName: list_cards) {
            Card c = CardCatalogue.getEveryCardByName(cardName);
            if (c.getCardType() == CardType.MINION) {
                ActorCardDesc desc = (ActorCardDesc) c.desc;
                if (desc.battlecry != null) {
                    GetSpells(desc.battlecry.spell);
                }
                if (desc.deathrattle != null) {
                    GetSpells(desc.deathrattle);
                }
                if (desc.trigger != null) {
                    GetSpells(desc.trigger.spell);
                }
            }
            if (c.getCardType() == CardType.SPELL) {
                SpellCardDesc desc = (SpellCardDesc)c.desc;
                GetSpells(desc.spell);
            }
            if (c.getCardType() == CardType.HERO_POWER) {
                HeroPowerCardDesc desc = (HeroPowerCardDesc)c.desc;
                GetSpells(desc.spell);
            }
        }
    }

    private void GetSpells(SpellDesc spell){
        if(spell.contains(SpellArg.SPELLS)){
            List<SpellDesc> spells = new ArrayList<>(Arrays.asList((SpellDesc[])spell.get(SpellArg.SPELLS)));
            for(SpellDesc desc : spells){
                if(!list_spells.contains(desc.getSpellClass().toString()))
                list_spells.add(desc.getSpellClass().toString());
            }
        }
        else {
            if(!list_spells.contains(spell.getSpellClass().toString()))
                list_spells.add(spell.getSpellClass().toString());
        }
    }
}

























