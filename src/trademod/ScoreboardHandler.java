package trademod;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardHandler {
	private TradePlayer p1 = null;
	private TradePlayer p2 = null;
	private ScoreboardManager manager = null;
	private Scoreboard sb = null;
	private Score a1 = null;
	private Score a2 = null;
	private Objective objective = null;
	private Scoreboard oldBoard = null;
	private Scoreboard oldBoard2 = null;
	
	public ScoreboardHandler(TradePlayer p1, TradePlayer p2) {
		this.p1 = p1;
		this.p2 = p2;
		manager = Bukkit.getScoreboardManager();
		sb = manager.getNewScoreboard();
		oldBoard = (p1.getPlayer().getScoreboard() == null) ? manager.getNewScoreboard() : p1.getPlayer().getScoreboard();
		oldBoard2 = (p2.getPlayer().getScoreboard() == null) ? manager.getNewScoreboard() : p2.getPlayer().getScoreboard();
		objective = sb.registerNewObjective("Offered Currency", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Offered Currency");
		a1 = objective.getScore(Bukkit.getOfflinePlayer(p1.getName()));
		a2 = objective.getScore(Bukkit.getOfflinePlayer(p2.getName()));
		a1.setScore(0);
		a2.setScore(0);
		p1.getPlayer().setScoreboard(sb);
		p2.getPlayer().setScoreboard(sb);
	}
	
	public void updateBoard(TradePlayer p, int d) {
		if(p != null) {
			if(p.equals(p1)) {
				a1.setScore(d);
			} else {
				a2.setScore(d);
			}
		}
	}
	
	public void closeBoard() {
		p1.getPlayer().setScoreboard(oldBoard);
		p2.getPlayer().setScoreboard(oldBoard2);
		sb = null;
	}
}
