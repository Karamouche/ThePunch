package fr.karamouche.thepunch;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.karamouche.thepunch.commands.StartCommand;
import fr.karamouche.thepunch.objects.Game;
import fr.karamouche.thepunch.objects.Puncher;
import fr.karamouche.thepunch.objects.Spawns;
import fr.karamouche.thepunch.objects.Statut;
import fr.karamouche.thepunch.scoreboard.ScoreboardManager;

public class Main extends JavaPlugin {
	
	
	//VARIABLES
	
    private ScoreboardManager sc;
    private Game game;
    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;
    
	
	@Override
	public void onEnable() {
		System.out.println("ThePunch ON");
		//loadChannel()
		//SCOREBOARD
		 scheduledExecutorService = Executors.newScheduledThreadPool(16);
	     executorMonoThread = Executors.newScheduledThreadPool(1);
	     sc = new ScoreboardManager(this);
	     game = new Game(this);
	     getServer().getPluginManager().registerEvents(new EventListener(this), this);
	     getCommand("forcestart").setExecutor(new StartCommand(this));
	}
	
	@Override
	public void onDisable() {
		System.out.println("ThePunch OFF");
		sc.onDisable();
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Team team : board.getTeams())
			team.unregister();
	}

    public ScoreboardManager getScoreboardManager() {
        return sc;
    }

    public ScheduledExecutorService getExecutorMonoThread() {
        return executorMonoThread;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
    public Game getCurrentGame() {
    	return this.game;
    }
/*
    public GamesAPI getGamesApi() {
        return (GamesAPI) Bukkit.getServer().getPluginManager().getPlugin("GamesAPI");
    }
    
    public CubixAPI getApi() {
        return (CubixAPI) Bukkit.getServer().getPluginManager().getPlugin("CubixAPI");
    }
*/
    /*
    private void loadChannel() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void teleport(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
    */
	public void Start() {
		int i = 0;
		for(Player player : Bukkit.getOnlinePlayers()) {
			Puncher puncher = new Puncher(player, this);
			puncher.getPlayer().teleport(Spawns.values()[i].toLocation());
			i++;
			puncher.giveItems();
			player.setGameMode(GameMode.ADVENTURE);
			player.setAllowFlight(false);
		}
		getCurrentGame().setStatut(Statut.START);
	}
	
}
