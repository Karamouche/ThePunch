package fr.karamouche.thepunch.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.karamouche.thepunch.Main;

public class Game {
	private Statut statut;
	private Map<Player, Puncher> punchers;
	private final String tag;
	private final Location gameSpawn;
	private final int maxPlayer = 3;
	private final Main myPlugin;
	
	//CONSTRUCTEUR
	public Game(Main main) {
		this.setStatut(Statut.PREGAME);
		this.setPunchers(new HashMap<Player, Puncher>());
		this.tag = ChatColor.BLACK+"["+ChatColor.BLUE+"ThePunch"+ChatColor.BLACK+"]"+"§r ";
		this.gameSpawn = new Location(Bukkit.getWorld("ThePunch"), 162, 162, 225);
		this.myPlugin = main;
	}
	
	public Statut getStatut() {
		return statut;
	}
	public void setStatut(Statut statut) {
		this.statut = statut;
	}
	
	public String getTag() {
		return tag;
	}
	public Location getGameSpawn() {
		return gameSpawn;
	}
	
	public int getNbPlayers() {
		if(this.getStatut().equals(Statut.START) || this.getStatut().equals(Statut.FINISH))
			return punchers.size();
		else
			return Bukkit.getOnlinePlayers().size();
	}
	public Map<Player, Puncher> getPunchers() {
		return punchers;
	}
	private void setPunchers(Map<Player, Puncher> punchers) {
		this.punchers = punchers;
	}
	public void addPuncher(Puncher puncher) {
		this.getPunchers().put(puncher.getPlayer(), puncher);
	}
	public void winner(Player fPlayer) {
		if(fPlayer != null) {
			this.setStatut(Statut.FINISH);
			fPlayer.setAllowFlight(true);
			Firework firework = fPlayer.getWorld().spawn(fPlayer.getLocation(), Firework.class);
			FireworkMeta data = firework.getFireworkMeta();
			data.addEffects(FireworkEffect.builder().withColor(Color.AQUA).with(Type.BALL_LARGE).withFlicker().build());
			data.setPower(1);
			firework.setFireworkMeta(data);
			Puncher pPlayer = myPlugin.getCurrentGame().getPunchers().get(fPlayer);
			Bukkit.getServer().broadcastMessage(getTag()+ChatColor.GRAY+"Victoire de "+pPlayer.getColor().getTag()+fPlayer.getName());
		}
	}
	public int getMaxPlayer() {
		return maxPlayer;
	}
	
	public ArrayList<String> getTop3() {
		ArrayList<String> classement = new ArrayList<String>();
		ArrayList<Integer> classementVies = new ArrayList<Integer>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(myPlugin.getCurrentGame().getPunchers().containsKey(player)) {
				classementVies.add(myPlugin.getCurrentGame().getPunchers().get(player).getLifes());
			}
		}
		Collections.sort(classementVies, Collections.reverseOrder());
		for(Integer value : classementVies) {
			for(Player player : myPlugin.getCurrentGame().getPunchers().keySet()) {
				Puncher puncher = myPlugin.getCurrentGame().getPunchers().get(player);
				if(value.equals(puncher.getLifes())) {
					classement.add(puncher.getPlayer().getPlayerListName()+ChatColor.WHITE+" : "+ChatColor.GOLD+value);
				}
			}
		}
		return classement;
	}
	
	public void start() {
		Game thisGame = this;
		BukkitRunnable timer = new BukkitRunnable() {
			int timeB = 30;
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if(thisGame.getNbPlayers() != thisGame.getMaxPlayer()) {
					thisGame.setStatut(Statut.PREGAME);;
					this.cancel();
				}
				else {
					if (timeB > 6) {
						if(timeB == 30)
							Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+" La partie commence dans 30 secondes");
						if(timeB == 15)
							Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+" La partie commence dans 15 secondes");
						if(timeB == 10)
							Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+" La partie commence dans 10 secondes");
					}
					else if(timeB==6) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.sendTitle("§6 Preparez vous... ", "§eLa partie va commencer");
							player.playSound(player.getLocation(), Sound.NOTE_PLING, 256, 1);}}
					else if(timeB<6 && timeB != 0) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.sendTitle("§a"+timeB, "§eLa partie va commencer");
							player.playSound(player.getLocation(), Sound.NOTE_PLING, 256, 1);}}
						
					else if (timeB==0) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.sendTitle("§bC'est parti !", "");
							player.playSound(player.getLocation(), Sound.CAT_MEOW, 256, 1000);
								}
						myPlugin.Start();
						this.cancel();
					}
								
						timeB--;
				}
				
			}
		};
		this.setStatut(Statut.STARTING);;
		timer.runTaskTimer(myPlugin, 0, 20);
	}

}
