package de.diemex.trademod;


import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler
{
    private TradePlayer tradePlayer1 = null;
    private TradePlayer tradePlayer2 = null;
    private ScoreboardManager manager = null;
    private Scoreboard scoreboard = null;
    private Score score1 = null;
    private Score score2 = null;
    private Scoreboard oldBoard = null;
    private Scoreboard oldBoard2 = null;


    public ScoreboardHandler(TradePlayer tradePlayer1, TradePlayer tradePlayer2)
    {
        this.tradePlayer1 = tradePlayer1;
        this.tradePlayer2 = tradePlayer2;

        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        oldBoard = (tradePlayer1.getPlayer().getScoreboard() == null) ? manager.getNewScoreboard() : tradePlayer1.getPlayer().getScoreboard();
        oldBoard2 = (tradePlayer2.getPlayer().getScoreboard() == null) ? manager.getNewScoreboard() : tradePlayer2.getPlayer().getScoreboard();

        Objective objective = scoreboard.registerNewObjective("Offered Currency", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Offered Currency");

        score1 = objective.getScore(Bukkit.getOfflinePlayer(tradePlayer1.getName()));
        score2 = objective.getScore(Bukkit.getOfflinePlayer(tradePlayer2.getName()));
        score1.setScore(0);
        score2.setScore(0);

        tradePlayer1.getPlayer().setScoreboard(scoreboard);
        tradePlayer2.getPlayer().setScoreboard(scoreboard);
    }


    public void updateBoard(TradePlayer p, int d)
    {
        if (p != null)
        {
            if (p.equals(tradePlayer1))
                score1.setScore(d);
            else
                score2.setScore(d);
        }
    }


    public void closeBoard()
    {
        tradePlayer1.getPlayer().setScoreboard(oldBoard);
        tradePlayer2.getPlayer().setScoreboard(oldBoard2);
        scoreboard = null;
    }
}
