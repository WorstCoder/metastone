package net.demilich.metastone.gui.synergies;

import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.cards.desc.ActorCardDesc;
import net.demilich.metastone.game.cards.desc.HeroPowerCardDesc;
import net.demilich.metastone.game.cards.desc.MinionCardDesc;
import net.demilich.metastone.game.cards.desc.SpellCardDesc;
import net.demilich.metastone.game.spells.*;
import net.demilich.metastone.game.synergy.spells.MissilesSpellC;
import net.demilich.metastone.game.spells.custom.AlarmOBotSpell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.condition.RandomCondition;
import net.demilich.metastone.game.spells.desc.valueprovider.RandomValueProvider;
import net.demilich.metastone.utils.UserHomeMetastone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;


public class GetRandomSpells {

    private Document doc;

    private List<String> list_cards = new ArrayList<>();
    private Map<String, List<Card>> list_spells = new HashMap<>();
    private static List<Card> listCards = new ArrayList<>();
    private String path = UserHomeMetastone.getPath() + "/synergies";
    private List<Card> allCards = CardCatalogue.getAll().toList();

    private String url = "http://hearthstone.gamepedia.com/Random_effect/Wild_format";

    public GetRandomSpells() {
    }

    public void GetRandoms() {
        try {
            GetCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GetAllSpells();

        List<Card> rands = new ArrayList<>();
        for (Card card : allCards) {
            List<SpellDesc> cardSpells = GetCardSpells(card);
            for (SpellDesc spell : cardSpells) {
                if (isRandom(spell) && !getListCards().contains(card)) rands.add(card);
            }
        }
    }

    private void GetCards() throws Exception {
        if (!new File(path + "/random_cards.ser").exists()) {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();
            Elements cards = doc.getElementsByAttributeValue("class", "to_hasTooltip");
            for (Element e : cards) {
                list_cards.add(e.text());
            }
            try {
                new File(path).mkdir();
                FileOutputStream fileOut = new FileOutputStream(path + "/random_cards.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(list_cards);
                out.close();
                fileOut.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(path + "/random_cards.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                list_cards = (List<String>) in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    private void GetAllSpells() {
        list_spells.put("RANDOM_TARGET", new ArrayList<>());
        list_spells.put("SPELL_2", new ArrayList<>());
        list_spells.put("VALUE", new ArrayList<>());
        list_spells.put("SPELL", new ArrayList<>());
        list_spells.put("FILTER", new ArrayList<>());
        list_spells.put("TRIGGERS", new ArrayList<>());
        for (String cardName : list_cards) {
            Card c = CardCatalogue.getEveryCardByName(cardName);
            getListCards().add(c);
            if(c.getCardType() == CardType.MINION && ((MinionCardDesc)c.desc).trigger!=null) list_spells.get("TRIGGERS").add(c);
            if (c.getCardType() == CardType.MINION) {
                ActorCardDesc desc = (ActorCardDesc) c.desc;
                if (desc.battlecry != null) {
                    AddSpells(GetSpells(desc.battlecry.spell, null), c);
                }
                if (desc.deathrattle != null) {
                    AddSpells(GetSpells(desc.deathrattle, null), c);
                }
                if (desc.trigger != null) {
                    AddSpells(GetSpells(desc.trigger.spell, null), c);
                }
            }
            if (c.getCardType() == CardType.SPELL) {
                SpellCardDesc desc = (SpellCardDesc) c.desc;
                AddSpells(GetSpells(desc.spell, null), c);
            }
            if (c.getCardType() == CardType.HERO_POWER) {
                HeroPowerCardDesc desc = (HeroPowerCardDesc) c.desc;
                AddSpells(GetSpells(desc.spell, null), c);
            }
        }
    }

    public List<SpellDesc> GetSpells(SpellDesc spell, List<SpellDesc> spellsList) {
        if(spell==null) return new ArrayList<SpellDesc>();
        if (spellsList == null) spellsList = new ArrayList<>();
        if (spell.contains(SpellArg.SPELLS)) {
            List<SpellDesc> innerSpells = new ArrayList<>(Arrays.asList((SpellDesc[]) spell.get(SpellArg.SPELLS)));
            for (SpellDesc innerSpell : innerSpells) {
                GetSpells(innerSpell, spellsList);
            }
        }
        if (spell.contains(SpellArg.SPELL_1)) {
            GetSpells((SpellDesc) spell.get(SpellArg.SPELL_1), spellsList);
        }
        if (spell.contains(SpellArg.SPELL_2)) {
            GetSpells((SpellDesc) spell.get(SpellArg.SPELL_2), spellsList);
        }
        if (spell.contains(SpellArg.SPELL_3)) {
            GetSpells((SpellDesc) spell.get(SpellArg.SPELL_3), spellsList);
        }
        if (spell.contains(SpellArg.SPELL)) {
            GetSpells((SpellDesc) spell.get(SpellArg.SPELL), spellsList);
        }
        spellsList.add(spell);
        return spellsList;
    }

    private void AddSpells(List<SpellDesc> spells, Card card) {
        for (SpellDesc spell : spells) {
            if (!list_spells.containsKey(spell.getSpellClass().toString())) {

                list_spells.put(spell.getSpellClass().toString(), new ArrayList<>());
                list_spells.get(spell.getSpellClass().toString()).add(card);
            } else if (!list_spells.get(spell.getSpellClass().toString()).contains(card)) {
                list_spells.get(spell.getSpellClass().toString()).add(card);
            }

            if (spell.contains(SpellArg.RANDOM_TARGET) && !list_spells.get("RANDOM_TARGET").contains(card)) {
                list_spells.get("RANDOM_TARGET").add(card);
            }

            if (spell.contains(SpellArg.VALUE) && spell.get(SpellArg.VALUE).getClass() == RandomValueProvider.class && !list_spells.get("VALUE").contains(card)) {
                list_spells.get("VALUE").add(card);
            }
            if (spell.contains(SpellArg.SPELL_2) && !list_spells.get("SPELL_2").contains(card)) {
                list_spells.get("SPELL_2").add(card);
            }
            if (spell.contains(SpellArg.SPELL) && !list_spells.get("SPELL").contains(card)) {
                list_spells.get("SPELL").add(card);
            }
            if (spell.contains(SpellArg.FILTER) && !list_spells.get("FILTER").contains(card)) {
                list_spells.get("FILTER").add(card);
            }
        }
    }

    public List<SpellDesc> GetCardSpells(Card card) {
        List<SpellDesc> spells = new ArrayList<>();
        if (card.getCardType() == CardType.MINION) {
            ActorCardDesc desc = (ActorCardDesc) card.desc;
            if (desc.battlecry != null) {
                spells.addAll(GetSpells(desc.battlecry.spell, null));
            }
            if (desc.deathrattle != null) {
                spells.addAll(GetSpells(desc.deathrattle, null));
            }
            if (desc.trigger != null) {
                spells.addAll(GetSpells(desc.trigger.spell, null));
            }
        }
        if (card.getCardType() == CardType.SPELL) {
            SpellCardDesc desc = (SpellCardDesc) card.desc;
            spells.addAll(GetSpells(desc.spell, null));
        }
        if (card.getCardType() == CardType.HERO_POWER) {
            HeroPowerCardDesc desc = (HeroPowerCardDesc) card.desc;
            spells.addAll(GetSpells(desc.spell, null));
        }
        return spells;
    }

    public boolean isRandom(SpellDesc spell) {
        //List<SpellDesc> cardSpells = getRandomSpells.GetCardSpells(card);
        //for (SpellDesc spell : cardSpells){
        Class spellClass = spell.getSpellClass();

        if (spell.contains(SpellArg.RANDOM_TARGET) && spell.contains(SpellArg.TARGET) && spell.getSpellClass() != MissilesSpellC.class)
            return true;

        if (spell.contains(SpellArg.VALUE) && spell.get(SpellArg.VALUE).getClass() == RandomValueProvider.class
                || spell.contains(SpellArg.ATTACK_BONUS) && spell.get(SpellArg.ATTACK_BONUS).getClass() == RandomValueProvider.class)
            return true;


        if (spellClass == AlarmOBotSpell.class) {
            return true;
        }
        if (spellClass == EquipRandomWeaponSpell.class) {
            return true;
        }
        if (spellClass == PutRandomMinionOnBoardSpell.class) {
            return true;
        }
        if (spellClass == PutRandomSecretIntoPlaySpell.class) {
            return true;
        }
        if (spellClass == ResurrectSpell.class) {
            return true;
        }
        if (spellClass == SummonRandomSpell.class) {
            return true;
        }
        if (spellClass == SummonRandomMinionFilteredSpell.class) {
            return true;
        }
        if (spellClass == SummonRandomMinionFromSpell.class) {
            return true;
        }
        if (spellClass == SummonRandomNotOnBoardSpell.class) {
            return true;
        }
        if (spellClass == TransformMinionSpell.class) {
            return true;
        }
        if (spellClass == MultiTargetDamageSpell.class) {
            return true;
        }
        if (spellClass == DestroyAllExceptOneSpell.class) {
            return true;
        }
        if (spellClass == RandomlyCastSpell.class) {
            return true;
        }
        if (spellClass == MindControlSpell.class) {
            return true;
        }
        if (spellClass == EitherOrSpell.class && spell.get(SpellArg.CONDITION).getClass() == RandomCondition.class) {
            return true;
        }

        if (spellClass == MissilesSpell.class) {
            return true;
        }
        return false;
    }

    public static List<Card> getListCards() {
        return listCards;
    }
}

























