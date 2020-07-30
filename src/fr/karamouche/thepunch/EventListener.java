
package fr.karamouche.thepunch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.karamouche.thepunch.objects.Game;
import fr.karamouche.thepunch.objects.Puncher;
import fr.karamouche.thepunch.objects.Statut;

public class EventListener implements Listener {
	private final Main myPlugin;
	public EventListener(Main main) {
		myPlugin = main;
	}
	
	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.STARTING)) {
			event.setKickMessage(game.getTag()+"La partie est complète !");
			event.setLoginResult(Result.KICK_OTHER);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		event.setCancelled(true);
		if(myPlugin.getCurrentGame().getStatut().equals(Statut.PREGAME) && event.getPlayer().isOp() && event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			event.setCancelled(false);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		/*final Player player = event.getPlayer();
		myPlugin.getGamesApi().addPlayer(Bukkit.getServerName(), player);

		if (myPlugin.getApi().getModManager().isInMod(player.getUniqueId())) {
			myPlugin.getPlayersManager().setSpecScoreboard(player);
			e.setJoinMessage(null);
			return;
		}*/
		myPlugin.getScoreboardManager().onLogin(event.getPlayer());
		Statut statut = myPlugin.getCurrentGame().getStatut();
		if(statut.equals(Statut.START) || statut.equals(Statut.FINISH)) {
			setSpec(event.getPlayer());
			event.setJoinMessage("");
		}
		else if(statut.equals(Statut.PREGAME) || statut.equals(Statut.STARTING)){
			event.getPlayer().teleport(myPlugin.getCurrentGame().getGameSpawn());
			event.getPlayer().setGameMode(GameMode.ADVENTURE);
			event.getPlayer().setHealth(20);
			event.getPlayer().getInventory().clear();
			event.getPlayer().setLevel(0);
			event.getPlayer().setExp(0);
			event.getPlayer().setDisplayName("");
			/*
			if(myPlugin.getPlayersManager().getPlayers().size() == 1) {
                myPlugin.getGamesApi().createGame(Bukkit.getServerName(), new ArrayList<UUID>());
                myPlugin.getGamesApi().setState(Bukkit.getServerName(), 0);
            }
            */
			event.setJoinMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.GRAY+" a rejoint la partie "+"§8(§a"+myPlugin.getCurrentGame().getNbPlayers()+"§8/§a"+myPlugin.getCurrentGame().getMaxPlayer()+"§8)");
			if(myPlugin.getCurrentGame().getNbPlayers() == myPlugin.getCurrentGame().getMaxPlayer()) {
				myPlugin.getCurrentGame().start();
			}
		}
	}
	
    private void setVanish(Player player, boolean trueOrFalse) {
        if(trueOrFalse) {
            Bukkit.getOnlinePlayers().forEach(players -> players.hidePlayer(player));
        } else {
            Bukkit.getOnlinePlayers().forEach(players -> players.showPlayer(player));
        }
    }
    
    private void setSpec(Player player) {
    	UUID playerUUID = player.getUniqueId();
        Bukkit.getPlayer(playerUUID).setGameMode(GameMode.SPECTATOR);
        setVanish(player, true);
        
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta cM = compass.getItemMeta();
        cM.setDisplayName("§aListe des joueurs");
        compass.setItemMeta(cM);

        ItemStack netherstar = new ItemStack(Material.NETHER_STAR);
        ItemMeta nM = netherstar.getItemMeta();
        nM.setDisplayName("§aRejoindre une nouvelle game");
        netherstar.setItemMeta(nM);
        
        ItemStack bed = new ItemStack(Material.BED);
        ItemMeta bM = bed.getItemMeta();
        bM.setDisplayName("§6Retour au hub");
        bed.setItemMeta(bM);
        
        Bukkit.getPlayer(playerUUID).getInventory().clear();
        Bukkit.getPlayer(playerUUID).getInventory().setHelmet(null);
        Bukkit.getPlayer(playerUUID).getInventory().setChestplate(null);
        Bukkit.getPlayer(playerUUID).getInventory().setLeggings(null);
        Bukkit.getPlayer(playerUUID).getInventory().setBoots(null);
        Bukkit.getPlayer(playerUUID).getInventory().setItem(0, compass);
        Bukkit.getPlayer(playerUUID).getInventory().setItem(7, netherstar);
        Bukkit.getPlayer(playerUUID).getInventory().setItem(8, bed);
    }
	
	@EventHandler
	public void onLeft(PlayerQuitEvent event) {
		//myPlugin.getGamesApi().removePlayer(Bukkit.getServerName(), e.getPlayer());
		myPlugin.getScoreboardManager().onLogout(event.getPlayer());
		Statut statut = myPlugin.getCurrentGame().getStatut();
		if(statut.equals(Statut.START)|| statut.equals(Statut.FINISH)) {
			final Player player = event.getPlayer();
			if(myPlugin.getCurrentGame().getPunchers().containsKey(player))
				myPlugin.getCurrentGame().getPunchers().get(event.getPlayer()).endGame();
			event.setQuitMessage("");
		}
		else {
			event.setQuitMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.GRAY+" a quitté la partie "+ChatColor.YELLOW+"["+(myPlugin.getCurrentGame().getNbPlayers()-1)+"/"+myPlugin.getCurrentGame().getMaxPlayer()+"]");
			/*if(myPlugin.getPlayersManager().getPlayers().size() == 0) {
                myPlugin.getGamesApi().deleteGame(Bukkit.getServerName());
            }*/
		}
		
	}
	
	private Map<Arrow, Player> ArrowMap = new HashMap<Arrow, Player>();
	@EventHandler
	public void onFire(EntityShootBowEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player && event.getProjectile() instanceof Arrow) {
			Arrow arrow = (Arrow)event.getProjectile();
			ArrowMap.put(arrow, (Player) entity);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		final double Y = event.getTo().getY();
		if(Y <= 140 && myPlugin.getCurrentGame().getStatut().equals(Statut.START)) {
			final Player player = event.getPlayer();
			if(myPlugin.getCurrentGame().getPunchers().containsKey(player)) {
				Puncher puncher = myPlugin.getCurrentGame().getPunchers().get(player);
				if(lastDamage.containsKey(player) && myPlugin.getCurrentGame().getPunchers().containsKey(lastDamage.get(player))) {
					puncher.kill(myPlugin.getCurrentGame().getPunchers().get(lastDamage.get(player)));
					lastDamage.remove(player);
				}
				else
					puncher.kill(null);
			}
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	private Map<Player, Player> lastDamage = new HashMap<Player, Player>();
	private ArrayList<Player> onCooldownPlayer = new ArrayList<Player>();
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		//ON RECUPERE LE JOUEUR QUI TAPE
		if(myPlugin.getCurrentGame().getStatut().equals(Statut.START)) {
			Player killer = null;
			Vector direction = null;
			if(event.getDamager() instanceof Player) {
				killer = (Player)event.getDamager();
				direction = killer.getLocation().getDirection();
			}
			else if(event.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) event.getDamager();
				if(ArrowMap.containsKey(arrow)){
					killer = ArrowMap.get(arrow);
					direction = arrow.getVelocity().normalize();
					if(!killer.equals((Player)event.getEntity()))
						killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 200, 1);
					ArrowMap.remove(arrow);
				}
			}
			//ON LE REPERTORIE DANS LAST DAMAGE
			if(event.getEntity() instanceof Player && killer != null && !onCooldownPlayer.contains(killer)) {
				event.setDamage(0);
				onCooldownPlayer.add(killer);
				System.out.println(killer.getName()+" a donné un coup");
				Player theKiller = killer;
				Player victim = (Player)event.getEntity();
				if(!killer.equals(victim)) {
					event.setCancelled(true);
					victim.damage(0);
					//MODIFER LE KNOCKBACK
					if(event.getDamager() instanceof Arrow)
						myPlugin.getCurrentGame().getPunchers().get(victim).addDamage("bow");
					else if(killer.getItemInHand().getType().equals(Material.STICK))
						myPlugin.getCurrentGame().getPunchers().get(victim).addDamage("stick");
					if(lastDamage.containsKey(victim)) {
						lastDamage.replace(victim, killer);
					}
					else
						lastDamage.put(victim, killer);
					//EDIT DU KB
					final double damageOfVictim = myPlugin.getCurrentGame().getPunchers().get(victim).getDamage();
					if(damageOfVictim/30 > 1.3 && (event.getDamager() instanceof Arrow || killer.getItemInHand().getType().equals(Material.STICK))) {
						victim.setVelocity(direction.multiply(damageOfVictim/30).setY(0.3));
						
					}
					else
						victim.setVelocity(direction.multiply(1.3).setY(0.3));
				}
				else
					event.setCancelled(true);
				Bukkit.getScheduler().runTaskLaterAsynchronously(myPlugin, new BukkitRunnable() {
					
					@Override
					public void run() {
						if(theKiller != null) {
							onCooldownPlayer.remove(theKiller);
						}
					}
				}, 10);
			}
		}else
			event.setCancelled(true);
	}
	
	//DESTROY ARROW ON FLOOR
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onArrowHit(ProjectileHitEvent event){
		if(event.getEntity() instanceof Arrow){
			Arrow arrow = (Arrow) event.getEntity();
			arrow.remove();
			Bukkit.getScheduler().runTaskLaterAsynchronously(myPlugin, new BukkitRunnable() {
				
				@Override
				public void run() {
					if(ArrowMap.containsKey(arrow))
						ArrowMap.remove(arrow);
				}
			}, 3);
		  }
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPassiveDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onRain(WeatherChangeEvent event) {
		event.setCancelled(true);
	}
	
	
	/*
    @EventHandler
    public void onDetect(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().equalsIgnoreCase("/mod")) {
            
            if(myPlugin.getApi().getRankManager().getRank(e.getPlayer()).getPower() >= 70) {
                
                if(myPlugin.getApi().getModManager().isInMod(e.getPlayer().getUniqueId()) == false) {
                    if(myPlugin.getCurrentGame().getStatut() == Statut.PREGAME) {
                        myPlugin.getPlayersManager().getPlayers().remove(e.getPlayer());
                    }
                    
                    
                } else if(myPlugin.getApi().getModManager().isInMod(e.getPlayer().getUniqueId()) == true) {
                    
                    
                    if(myPlugin.getStateManager().getState() == State.WAITING) {
                        myPlugin.getPlayersManager().addPlayer(e.getPlayer());
                    }
                    
                }
                
            }
            
        }
    }*/
}
