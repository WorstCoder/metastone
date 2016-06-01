package net.demilich.metastone.gui.bestofdecks;

import net.demilich.metastone.game.statistics.GameStatistics;
import net.demilich.metastone.game.statistics.Statistic;

import java.util.Map;

public class DeckStats {

     private Long deckId;

     private String heroClass;

     private String deckName;

     private Double winRate;

     private Long won;

     private Long lost;

     private Long damageDealt;

     private Long healingDone;

     private Long manaSpent;

     private Long cardsPlayed;

     private Long turnsTaken;

     private Long armorGained;

     private Long cardsDrawn;

     private Long fatigueDamage;

     private Long minionsPlayed;

     private Long spellsCast;

     private Long heroPowerUsed;

     private Long weaponsEquipped;

     private Long weaponsPlayed;

    public DeckStats (GameStatistics stats){
        heroClass = stats.getHero().toString();
        deckName = stats.getDeckName();
        deckId = (long)stats.getId();
        Map statsMap = stats.getStatsMap();
        for (Statistic stat:Statistic.values()){
            switch(stat){
                case WIN_RATE: winRate = (Double) statsMap.get(stat);
                    break;
                case GAMES_WON: won = (Long) statsMap.get(stat);
                    break;
                case GAMES_LOST: lost = (Long) statsMap.get(stat);
                    break;
                case DAMAGE_DEALT: damageDealt = (Long) statsMap.get(stat);
                    break;
                case HEALING_DONE: healingDone = (Long) statsMap.get(stat);
                    break;
                case MANA_SPENT: manaSpent = (Long) statsMap.get(stat);
                    break;
                case CARDS_PLAYED: cardsPlayed = (Long) statsMap.get(stat);
                    break;
                case TURNS_TAKEN: turnsTaken = (Long) statsMap.get(stat);
                    break;
                case ARMOR_GAINED: armorGained = (Long) statsMap.get(stat);
                    break;
                case CARDS_DRAWN: cardsDrawn = (Long) statsMap.get(stat);
                    break;
                case FATIGUE_DAMAGE: fatigueDamage = (Long) statsMap.get(stat);
                    break;
                case MINIONS_PLAYED: minionsPlayed = (Long) statsMap.get(stat);
                    break;
                case SPELLS_CAST: spellsCast = (Long) statsMap.get(stat);
                    break;
                case HERO_POWER_USED: heroPowerUsed = (Long) statsMap.get(stat);
                    break;
                case WEAPONS_EQUIPPED: weaponsEquipped = (Long) statsMap.get(stat);
                    break;
                case WEAPONS_PLAYED: weaponsPlayed = (Long) statsMap.get(stat);
                    break;
            }
        }

    }


    public String getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public Long getWon() {
        return won;
    }

    public void setWon(Long won) {
        this.won = won;
    }

    public Long getLost() {
        return lost;
    }

    public void setLost(Long lost) {
        this.lost = lost;
    }

    public Long getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(Long damageDealt) {
        this.damageDealt = damageDealt;
    }

    public Long getHealingDone() {
        return healingDone;
    }

    public void setHealingDone(Long healingDone) {
        this.healingDone = healingDone;
    }

    public Long getManaSpent() {
        return manaSpent;
    }

    public void setManaSpent(Long manaSpent) {
        this.manaSpent = manaSpent;
    }

    public Long getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCardsPlayed(Long cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
    }

    public Long getTurnsTaken() {
        return turnsTaken;
    }

    public void setTurnsTaken(Long turnsTaken) {
        this.turnsTaken = turnsTaken;
    }

    public Long getArmorGained() {
        return armorGained;
    }

    public void setArmorGained(Long armorGained) {
        this.armorGained = armorGained;
    }

    public Long getCardsDrawn() {
        return cardsDrawn;
    }

    public void setCardsDrawn(Long cardsDrawn) {
        this.cardsDrawn = cardsDrawn;
    }

    public Long getFatigueDamage() {
        return fatigueDamage;
    }

    public void setFatigueDamage(Long fatigueDamage) {
        this.fatigueDamage = fatigueDamage;
    }

    public Long getMinionsPlayed() {
        return minionsPlayed;
    }

    public void setMinionsPlayed(Long minionsPlayed) {
        this.minionsPlayed = minionsPlayed;
    }

    public Long getSpellsCast() {
        return spellsCast;
    }

    public void setSpellsCast(Long spellsCast) {
        this.spellsCast = spellsCast;
    }

    public Long getHeroPowerUsed() {
        return heroPowerUsed;
    }

    public void setHeroPowerUsed(Long heroPowerUsed) {
        this.heroPowerUsed = heroPowerUsed;
    }

    public Long getWeaponsEquipped() {
        return weaponsEquipped;
    }

    public void setWeaponsEquipped(Long weaponsEquipped) {
        this.weaponsEquipped = weaponsEquipped;
    }

    public Long getWeaponsPlayed() {
        return weaponsPlayed;
    }

    public void setWeaponsPlayed(Long weaponsPlayed) {
        this.weaponsPlayed = weaponsPlayed;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }
}
