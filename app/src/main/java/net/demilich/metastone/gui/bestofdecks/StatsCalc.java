package net.demilich.metastone.gui.bestofdecks;


import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.statistics.GameStatistics;
import net.demilich.metastone.game.statistics.Statistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsCalc {

    private List<BestOfResults> simResults;
    private Map<String,GameStatistics> calcResults = new HashMap<>();

    public StatsCalc(List<BestOfResults> simResults){
        this.simResults = simResults;
        FillResults();
    }

    private void FillResults(){
        for(BestOfResults result:simResults) {
            FillPlayer(result.player1);
            FillPlayer(result.player2);
        }
    }

    private void FillPlayer(Player player){
        String deckName = player.getDeckName();
        if ( !calcResults.containsKey(deckName)) {
            calcResults.put(player.getDeckName(),player.getStatistics());
            return;
        }
        //Map playerStats = player.getStatistics().getStatsMap();
        GameStatistics oldStats = calcResults.get(deckName);
        GameStatistics newStats = player.getStatistics();
        for(Statistic stat:Statistic.values()){
            if(!oldStats.contains(stat)) oldStats.set(stat,0L);
            if(stat != Statistic.WIN_RATE){
                oldStats.set(stat,oldStats.getLong(stat) + (newStats.contains(stat) ? newStats.getLong(stat) : 0));
            }
        }
    }

    public Map getResults(){return calcResults;}
}
