package net.demilich.metastone.game.spells.RandomChancesSpells;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.Environment;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.BattlecryAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.cards.SpellCard;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.EntityType;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.TargetAcquisitionEvent;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.game.logic.TargetLogic;
import net.demilich.metastone.game.spells.*;
import net.demilich.metastone.game.spells.custom.AlarmOBotSpell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.valueprovider.RandomValueProvider;
import net.demilich.metastone.game.targeting.CardLocation;
import net.demilich.metastone.game.targeting.EntityReference;
import net.demilich.metastone.game.targeting.IdFactory;
import net.demilich.metastone.game.targeting.TargetSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SynergyGameLogic extends GameLogic {

    SynergyTargetLogic targetLogic = new SynergyTargetLogic();


    public void init(int playerId) {
        Player player = context.getPlayer(playerId);
        player.getHero().setId(idFactory.generateId());
        player.getHero().setOwner(player.getId());
        player.getHero().setMaxHp(player.getHero().getAttributeValue(Attribute.BASE_HP));
        player.getHero().setHp(player.getHero().getAttributeValue(Attribute.BASE_HP));

        player.getHero().getHeroPower().setId(idFactory.generateId());
        assignCardIds(player.getDeck());
        assignCardIds(player.getHand());

        log("Setting hero hp to {} for {}", player.getHero().getHp(), player.getName());

        player.getDeck().shuffle();
    }

    @Override
    protected void assignCardIds(CardCollection cardCollection) {
        for (Card card : cardCollection) {
            card.setId(idFactory.generateId());
            if (card.getLocation() == null) card.setLocation(CardLocation.DECK);
        }
    }

    //protected void

    @Override
    protected void resolveBattlecry(int playerId, Actor actor) {
        BattlecryAction battlecry = actor.getBattlecry();
        Player player = context.getPlayer(playerId);
        if (!battlecry.canBeExecuted(context, player)) {
            return;
        }

        GameAction battlecryAction = null;
        battlecry.setSource(actor.getReference());
        if (battlecry.getTargetRequirement() != TargetSelection.NONE) {
            List<Entity> validTargets = targetLogic.getValidTargets(context, player, battlecry);
            if (validTargets.isEmpty()) {
                return;
            }

            List<GameAction> battlecryActions = new ArrayList<>();
            for (Entity validTarget : validTargets) {
                GameAction targetedBattlecry = battlecry.clone();
                targetedBattlecry.setTarget(validTarget);
                battlecryActions.add(targetedBattlecry);
            }

            battlecryAction = player.getBehaviour().requestAction(context, player, battlecryActions);
        } else {
            battlecryAction = battlecry;
        }
        if (hasAttribute(player, Attribute.DOUBLE_BATTLECRIES) && actor.getSourceCard().hasAttribute(Attribute.BATTLECRY)) {
            // You need DOUBLE_BATTLECRIES before your battlecry action, not after.
            performGameAction(playerId, battlecryAction);
            performGameAction(playerId, battlecryAction);
        } else {
            SpellDesc battlecrySpellDesc = ((BattlecryAction) battlecryAction).getSpell();

            List<SpellDesc> battlecrySpells = getPossibilities(battlecrySpellDesc, context, player, battlecrySpellDesc, actor);
            GameAction battlecryClone = battlecryAction.clone();
            ((BattlecryAction) battlecryClone).setSpell(battlecrySpells.get(0));

            performGameAction(playerId, battlecryClone);
        }
    }

    @Override
    public void resolveDeathrattles(Player player, Actor actor) {
        resolveDeathrattles(player, actor, -1);
    }

    @Override
    public void resolveDeathrattles(Player player, Actor actor, int boardPosition) {
        if (!actor.hasAttribute(Attribute.DEATHRATTLES)) {
            return;
        }
        if (boardPosition == -1) {
            player.getMinions().indexOf(actor);
        }
        boolean doubleDeathrattles = hasAttribute(player, Attribute.DOUBLE_DEATHRATTLES);
        EntityReference sourceReference = actor.getReference();
        for (SpellDesc deathrattleTemplate : actor.getDeathrattles()) {
            SpellDesc deathrattle = deathrattleTemplate.addArg(SpellArg.BOARD_POSITION_ABSOLUTE, boardPosition);

            List<SpellDesc> possibleDeathrattles = getPossibilities(deathrattle, context, player, deathrattle, context.resolveSingleTarget(sourceReference));
            deathrattle = possibleDeathrattles.get(0);

            castSpell(player.getId(), deathrattle, sourceReference, EntityReference.NONE, false);
            if (doubleDeathrattles) {
                castSpell(player.getId(), deathrattle, sourceReference, EntityReference.NONE, false);
            }
        }
    }

    @Override
    public void castSpell(int playerId, SpellDesc spellDesc, EntityReference sourceReference, EntityReference targetReference,
                          boolean childSpell) {
        Player player = context.getPlayer(playerId);
        Entity source = null;
        if (sourceReference != null) {
            try {
                source = context.resolveSingleTarget(sourceReference);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error resolving source entity while casting spell: " + spellDesc);
            }

        }
        SpellCard spellCard = null;
        EntityReference spellTarget = spellDesc.hasPredefinedTarget() ? spellDesc.getTarget() : targetReference;
        List<Entity> targets = targetLogic.resolveTargetKey(context, player, source, spellTarget);
        // target can only be changed when there is one target
        // note: this code block is basically exclusively for the SpellBender
        // Secret, but it can easily be expanded if targets of area of effect
        // spell should be changeable as well
        Card sourceCard = null;
        if (source != null) {
            sourceCard = source.getEntityType() == EntityType.CARD ? (Card) source : null;
        }
        if (sourceCard != null && sourceCard.getCardType().isCardType(CardType.SPELL) && !spellDesc.hasPredefinedTarget() && targets != null
                && targets.size() == 1) {
            if (sourceCard instanceof SpellCard) {
                spellCard = (SpellCard) sourceCard;
            }

            if (spellCard != null && spellCard.getTargetRequirement() != TargetSelection.NONE && !childSpell) {
                GameEvent spellTargetEvent = new TargetAcquisitionEvent(context, playerId, ActionType.SPELL, spellCard, targets.get(0));
                context.fireGameEvent(spellTargetEvent);
                Entity targetOverride = context
                        .resolveSingleTarget((EntityReference) context.getEnvironment().get(Environment.TARGET_OVERRIDE));
                if (targetOverride != null && targetOverride.getId() != IdFactory.UNASSIGNED) {
                    targets.remove(0);
                    targets.add(targetOverride);
                    spellDesc = spellDesc.addArg(SpellArg.FILTER, null);
                    log("Target for spell {} has been changed! New target {}", spellCard, targets.get(0));
                }
            }

        }
        try {
            Spell spell = spellFactory.getSpell(spellDesc);

            if (isRandom(spellDesc) && !spellDesc.contains(SpellArg.POSSIBILITY)) {
                if (childSpell || source instanceof SpellCard) {
                    List<SpellDesc> possibleSpells = getPossibilities(spellDesc, context, player, spellDesc, source);
                    spellDesc = possibleSpells.get(0);

                    spell = spellFactory.getSpell(possibleSpells.get(0));
                }
            }
            if(targets!=null && spellDesc.contains(SpellArg.POSSIBILITY)){
                targets.clear();
                targets.add((Entity) spellDesc.get(SpellArg.POSSIBILITY));}

            spell.cast(context, player, spellDesc, source, targets);
        } catch (Exception e) {
            if (source != null) {
                logger.error("Error while playing card: " + source.getName());
            }
            logger.error("Error while casting spell: " + spellDesc);
            panicDump();
            e.printStackTrace();
        }

        if (spellCard != null) {
            context.getEnvironment().remove(Environment.TARGET_OVERRIDE);
        }
    }


    protected List<SpellDesc> getPossibilities(SpellDesc spell, GameContext context, Player player, SpellDesc desc, Entity source) {
        //List<SpellDesc> spells = getAllSpells(root,null,null);
        //List<List<SpellDesc>> possibleSpells = new ArrayList<>();
        List<SpellDesc> possibleSpells = new ArrayList<>();
        if (spell.getSpellClass() == MetaSpell.class) return metaPossibilities(spell, context, player, desc, source);
        //for(SpellDesc spell : spells) {
        if (isRandom(spell)) {
            //List<SpellDesc> tempSpellList = new ArrayList<>();
            Spell unrandomizedSpell = getUnrandomizedSpell(spell);
            List<Object> possibilities = unrandomizedSpell.getPossibilities((SynergyGameContext) context, player, spell, source);
            for (Object possibility : possibilities) {
                SpellDesc newSpell = spell.addArg(SpellArg.POSSIBILITY, possibility);
                newSpell.changeArg(SpellArg.CLASS, unrandomizedSpell.getClass());
                //((SpellC)newSpell.get(SpellArg.CLASS)).set(possibility);
                possibleSpells.add(newSpell);
            }
        }
        return possibleSpells;
        //}
        /*
        List<SpellDesc> newSpells = new ArrayList<>();
        List<SpellDesc> tempSpells = new ArrayList<>();
        newSpells.add(root);
        for (List<SpellDesc> possSpells : possibleSpells) {
            for(SpellDesc spell : newSpells){
                tempSpells.addAll(makeSpells(spell,possSpells));
            }
            newSpells.clear();
            newSpells.addAll(tempSpells);
            tempSpells.clear();
        }
        return newSpells;*/
    }

    private List<SpellDesc> metaPossibilities(SpellDesc root, GameContext context, Player player, SpellDesc desc, Entity source) {
        List<SpellDesc> possSpells = new ArrayList<>();
        possSpells.add(root);
        List<SpellDesc> rootSpells = Arrays.asList((SpellDesc[]) root.get(SpellArg.SPELLS));
        List<SpellDesc> tempSpells = new ArrayList<>();

        boolean doubles = true;
        boolean orderMatter = true;
        int order = 0;
        if (source.getName().equals("Dark Bargain")) {
            doubles = false;
            orderMatter = false;
        }
        //Loop through all meta spells
        for (SpellDesc spell : rootSpells) {
            //If spell is random get possibilities and unrandomized spell
            if (isRandom(spell)) {
                Spell unrandomizedSpell = getUnrandomizedSpell(spell);
                List<SpellDesc> innerPossSpells = getPossibilities(spell, context, player, desc, source);
                //if(!orderMatter){innerPossSpells=innerPossSpells.subList(order++,innerPossSpells.size());}
                //Loop through possibilities
                for (SpellDesc innerPossSpell : innerPossSpells) {
                    //Make clone of each last spells
                    for (SpellDesc outerSpell : possSpells) {

                        if (!orderMatter && innerPossSpell.contains(SpellArg.USED) && (boolean) innerPossSpell.get(SpellArg.USED))
                            continue;
                        if (!doubles) {
                            if (getAllSpellPoss(outerSpell).contains(innerPossSpell.get(SpellArg.POSSIBILITY))) {
                                innerPossSpell.putArg(SpellArg.USED, true);
                                continue;
                            }
                        }

                        SpellDesc rootClone = outerSpell.clone();
                        List<SpellDesc> cloneSpells = Arrays.asList((SpellDesc[]) rootClone.get(SpellArg.SPELLS));
                        //Find and modify random clone spells
                        for (SpellDesc innerSpell : cloneSpells) {
                            if (cloneSpells.indexOf(innerSpell) == rootSpells.indexOf(spell)) {
                                innerSpell.putArg(SpellArg.POSSIBILITY, innerPossSpell.get(SpellArg.POSSIBILITY));
                                innerSpell.changeArg(SpellArg.CLASS, unrandomizedSpell.getClass());
                                tempSpells.add(rootClone);
                            }
                        }
                    }
                }
                possSpells.clear();
                possSpells.addAll(tempSpells);
                tempSpells.clear();
            }
        }
        return possSpells;
    }

    private List<Object> getAllSpellPoss(SpellDesc spell) {
        List<Object> poss = new ArrayList<>();
        List<SpellDesc> spells = getAllSpells(spell, null, null);
        for (SpellDesc innerSpell : spells) {
            if (innerSpell.contains(SpellArg.POSSIBILITY)) {
                poss.add(innerSpell.get(SpellArg.POSSIBILITY));
            }
        }
        return poss;
    }

    private List<SpellDesc> makeSpells(SpellDesc root, List<SpellDesc> possibileSpells) {
        List<SpellDesc> newSpells = new ArrayList<>();
        for (SpellDesc finalSpell : possibileSpells) {
            SpellDesc firstRoot = root.clone();
            List<SpellDesc> finalSpellSpells = getAllSpells(firstRoot, null, null);
            for (SpellDesc finalFinalSpell : finalSpellSpells) {
                if (spellsEqual(finalFinalSpell, finalSpell)) {
                    finalFinalSpell = finalSpell;
                    newSpells.add(finalFinalSpell);
                }
            }
        }
        return newSpells;
    }


    private List<SpellDesc> getAllSpells(SpellDesc spell, List<SpellDesc> spells, SpellDesc spell_2) {
        if (spells == null) {
            spells = new ArrayList<>();
        }
        if (spell.contains(SpellArg.SPELLS)) {
            List<SpellDesc> innerSpells = new ArrayList<>(Arrays.asList((SpellDesc[]) spell.get(SpellArg.SPELLS)));
            for (SpellDesc innerSpell : innerSpells) {
                getAllSpells(innerSpell, spells, spell_2);
            }
        }
        if (spell.contains(SpellArg.SPELL_1)) {
            getAllSpells((SpellDesc) spell.get(SpellArg.SPELL_1), spells, spell_2);
        }
        if (spell.contains(SpellArg.SPELL_2)) {
            getAllSpells((SpellDesc) spell.get(SpellArg.SPELL_2), spells, spell_2);
        }
        if (spell.contains(SpellArg.SPELL_3)) {
            getAllSpells((SpellDesc) spell.get(SpellArg.SPELL_3), spells, spell_2);
        }
        if (spell.contains(SpellArg.SPELL)) {
            getAllSpells((SpellDesc) spell.get(SpellArg.SPELL), spells, spell_2);
        }

        if (spell_2 == null) {
            spells.add(spell);
        } else if (spell.getSpellClass() == spell_2.getSpellClass()) {
            spells.add(spell);
        }
        return spells;
    }

    private boolean spellsEqual(SpellDesc spell_1, SpellDesc spell_2) {
        if (spell_2.getSpellClass().getSimpleName().equals(spell_1.getSpellClass().getSimpleName() + "C")) return true;
        Map<SpellArg, Object> args_1 = spell_1.getArgs();
        Map<SpellArg, Object> args_2 = spell_2.getArgs();
        for (SpellArg arg : args_1.keySet()) {
            if (arg == SpellArg.CLASS) continue;
            if (args_1.get(arg) != args_2.get(arg)) return false;
        }
        return true;
    }

    private Spell getUnrandomizedSpell(SpellDesc spell) {
        //List<SpellDesc> cardSpells = getRandomSpells.GetCardSpells(card);
        //for (SpellDesc spell : cardSpells){
        Class spellClass = spell.getSpellClass();

        if (spellClass == AlarmOBotSpell.class) {
            return new AlarmOBotSpellC();
        }
        if (spellClass == EquipRandomWeaponSpell.class) {
            return new EquipRandomWeaponSpellC();
        }
        if (spellClass == PutRandomMinionOnBoardSpell.class && spell.get(SpellArg.CARD_LOCATION) == CardLocation.HAND) {
            return new PutRandomMinionOnBoardSpellC();
        }
        if (spellClass == PutRandomSecretIntoPlaySpell.class) {
            return new PutRandomSecretIntoPlaySpellC();
        }
        if (spellClass == ResurrectSpell.class) {
            return new ResurrectSpellC();
        }
        if (spellClass == SummonRandomSpell.class) {
            return new SummonRandomSpellC();
        }
        if (spellClass == SummonRandomMinionFilteredSpell.class) {
            return new SummonRandomMinionFilteredSpellC();
        }
        if (spellClass == SummonRandomMinionFromSpell.class) {
            return new SummonRandomMinionFromSpellC();
        }
        if (spellClass == SummonRandomNotOnBoardSpell.class) {
            return new SummonRandomNotOnBoardSpellC();
        }
        if (spellClass == TransformToRandomMinionSpell.class) {
            return new TransformToRandomMinionSpellC();
        }
        if (spellClass == MultiTargetDamageSpell.class) {
            return new MultiTargetDamageSpellC();
        }
        if (spellClass == DestroyAllExceptOneSpell.class) {
            return new DestroyAllExceptOneSpellC();
        }
        //if (spellClass == DestroySpell.class) {
        //    return new DestroySpellC();
        //}
        return spellFactory.getSpell(spell);
    }

    private boolean isRandom(SpellDesc spell) {
        //List<SpellDesc> cardSpells = getRandomSpells.GetCardSpells(card);
        //for (SpellDesc spell : cardSpells){
        Class spellClass = spell.getSpellClass();

        if (spell.contains(SpellArg.RANDOM_TARGET) && spell.contains(SpellArg.TARGET)) return true;

        if (spell.contains(SpellArg.VALUE) && spell.get(SpellArg.VALUE).getClass() == RandomValueProvider.class
                || spell.contains(SpellArg.ATTACK_BONUS) && spell.get(SpellArg.ATTACK_BONUS).getClass() == RandomValueProvider.class) return true;


        if (spellClass == AlarmOBotSpell.class) {
            return true;
        }
        if (spellClass == EquipRandomWeaponSpell.class) {
            return true;
        }
        if (spellClass == PutRandomMinionOnBoardSpell.class && spell.get(SpellArg.CARD_LOCATION) == CardLocation.HAND) {
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
        //if (spellClass == DestroySpell.class) {
        //    return true;
        //}
        if (spellClass == MindControlSpell.class) {
            return true;
        }
        if (spellClass == MetaSpell.class) {
            return true;
        }
            return false;
        }


    public SynergyTargetLogic getTargetLogic() {
        return targetLogic;
    }

}
