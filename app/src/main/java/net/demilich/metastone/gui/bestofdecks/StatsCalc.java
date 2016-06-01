package net.demilich.metastone.gui.bestofdecks;


import net.demilich.metastone.game.Player;
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
        int n=0;
        for(Object key:calcResults.keySet()){
            GameStatistics tempStat = calcResults.get(key);
            tempStat.setId(n++);
            tempStat.updateWinRate();
            for(Statistic stat:Statistic.values()){
                if(     stat != Statistic.WIN_RATE &&
                        stat != Statistic.GAMES_LOST &&
                        stat != Statistic.GAMES_WON)
                tempStat.set(stat,tempStat.getLong(stat)/tempStat.getGames());
            }
        }
    }

    private void FillPlayer(Player player){
        String deckName = player.getDeckName();
        if ( !calcResults.containsKey(deckName)) {
            calcResults.put(deckName,player.getStatistics());
            calcResults.get(deckName).addGame();
            calcResults.get(deckName).setHero(player.getHero().getHeroClass());
            calcResults.get(deckName).setDeckName(player.getDeckName());
            return;
        }
        GameStatistics oldStats = calcResults.get(deckName);
        GameStatistics newStats = player.getStatistics();
        for(Statistic stat:Statistic.values()){
            if(!oldStats.contains(stat)) oldStats.set(stat,0L);
            if(stat != Statistic.WIN_RATE){
                oldStats.set(stat,oldStats.getLong(stat) + (newStats.contains(stat) ? newStats.getLong(stat) : 0));
            }
        }
        oldStats.addGame();
    }

    public Map getResults(){return calcResults;}
}
