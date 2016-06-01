package net.demilich.metastone.gui.bestofdecks;

import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.heroes.HeroClass;

/**
 * Created by X on 27.05.2016.
 */
public class BestOfResults {
    public Player player1;
    public Player player2;
    public int winner;

    public BestOfResults(Player p1, Player p2, int w){
        this.player1 = p1;
        this.player2 = p2;
        this.winner = w;
    }
}
