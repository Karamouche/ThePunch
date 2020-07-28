package fr.karamouche.thepunch.objects;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public enum ColorTeam {
	team5(5, "§a", "a"),
	team4(4, "§e", "b"),
	team3(3, "§6", "c"),
	team2(2, "§c", "d"),
	team1(1, "§4", "e"),
	team0(0, "", "f");
	
	final private int count;
	final private String tag;
	final private Team team;
	final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	
	ColorTeam(int count, String tag, String order) {
		this.count = count;
		this.tag = tag;
		this.team = board.registerNewTeam(order+"Team"+this.getCount());
		this.team.setPrefix(this.getTag());
	}

	public int getCount() {
		return count;
	}

	public String getTag() {
		return tag;
	}

	public Team getTeam() {
		return team;
	}

	static ColorTeam getTeam(int lifes) {
		for(ColorTeam lifeteam : ColorTeam.values()) {
			if(lifeteam.getCount() == lifes)
				return lifeteam;
		}
		return null;
	}
}
