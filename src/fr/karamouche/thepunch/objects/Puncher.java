package fr.karamouche.thepunch.objects;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import fr.karamouche.thepunch.Main;
import io.netty.util.internal.ThreadLocalRandom;
import net.md_5.bungee.api.ChatColor;

public class Puncher {
	private final Player player;
	private int lifes;
	private int kills;
	private int damage;
	private ColorTeam color;
	private final Main myPlugin;
	
	@SuppressWarnings("deprecation")
	public Puncher(Player player, Main main) {
		final int maxHealth = 5;
		this.player = player;;
		player.setHealthScale(maxHealth*2);
		player.setHealthScaled(true);
		this.lifes = maxHealth;
		this.color = ColorTeam.team5;
		ColorTeam.team5.getTeam().addPlayer(player);
		this.kills = 0;
		this.setDamage(0);
		player.setLevel(0);
		myPlugin = main;
		myPlugin.getCurrentGame().addPuncher(this);
	}
	public Player getPlayer() {
		return player;
	}
	
	public int getLifes() {
		return lifes;
	}
	public void removeLife() {
		if(this.getLifes()>1) {
			this.lifes = this.getLifes() - 1;
			this.getPlayer().setHealth(this.lifes*4);
			this.setColor(ColorTeam.getTeam(lifes));
		}
	}
	
	public int getDamage() {
		return damage;
	}
	private void setDamage(int damage) {
		this.damage = damage;
		this.getPlayer().setLevel(damage);
	}
	public void addDamage(String type) {
		final int randomNum;
		switch (type) {
		case "bow":
			randomNum = ThreadLocalRandom.current().nextInt(20, 36);
			break;

		case "stick":
			randomNum = ThreadLocalRandom.current().nextInt(30, 46);
			break;
		default:
			randomNum = ThreadLocalRandom.current().nextInt(20, 36);
		}
		this.setDamage(this.getDamage()+randomNum);
	}
	
	public int getKills() {
		return kills;
	}
	public void addKill() {
		this.kills = this.getKills() +1;
	}
	
	public void kill(Puncher killer) {
		if(killer != null) {
			killer.addKill();
		}
		//TELEPORTER LE JOUEUR AU SPAWN
		if(this.getLifes() == 1) {
			this.endGame();
		}
		else {
			//LANCER DES FEUX D'ARTIFICEs
			String tag = this.getColor().getTag();
			Firework firework = this.getPlayer().getWorld().spawn(player.getLocation(), Firework.class);
			FireworkMeta data = firework.getFireworkMeta();
			switch (tag) {
			case "§a":
				data.addEffects(FireworkEffect.builder().withColor(Color.LIME).with(Type.BALL_LARGE).withFlicker().build());
				break;
			case "§e":
				data.addEffects(FireworkEffect.builder().withColor(Color.YELLOW).with(Type.BALL_LARGE).withFlicker().build());
				break;
			case "§6":
				data.addEffects(FireworkEffect.builder().withColor(Color.ORANGE).with(Type.BALL_LARGE).withFlicker().build());
				break;
			default:
				data.addEffects(FireworkEffect.builder().withColor(Color.RED).with(Type.BALL_LARGE).withFlicker().build());
				break;
			}
			data.setPower(2);
			firework.setFireworkMeta(data);
			if(killer != null) {
				Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+this.getColor().getTag()+this.getPlayer().getName()+ChatColor.GRAY+" a �t� tu� par "+killer.getColor().getTag()+killer.getPlayer().getName());
			}
			else
				Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+this.getColor().getTag()+this.getPlayer().getName()+ChatColor.GRAY+" est mort");
			this.removeLife();
			this.setDamage(0);
			int randomNum = ThreadLocalRandom.current().nextInt(0, Spawns.values().length);
			this.getPlayer().teleport(Spawns.values()[randomNum].toLocation());
		}
		
	}
	public void endGame() {
		setSpec(this.getPlayer());
		myPlugin.getCurrentGame().getPunchers().remove(this.getPlayer());
		Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+this.getColor().getTag()+this.getPlayer().getName()+ChatColor.GRAY+" est �limin� !");
		if(myPlugin.getCurrentGame().getNbPlayers() == 1) {
			Player fPlayer = null;
			for(Player player : Bukkit.getOnlinePlayers())
				if(player.getGameMode().equals(GameMode.ADVENTURE))
					fPlayer = player;
			myPlugin.getCurrentGame().winner(fPlayer);
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
        cM.setDisplayName("�aListe des joueurs");
        compass.setItemMeta(cM);

        ItemStack netherstar = new ItemStack(Material.NETHER_STAR);
        ItemMeta nM = netherstar.getItemMeta();
        nM.setDisplayName("�aRejoindre une nouvelle game");
        netherstar.setItemMeta(nM);
        
        ItemStack bed = new ItemStack(Material.BED);
        ItemMeta bM = bed.getItemMeta();
        bM.setDisplayName("�6Retour au hub");
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
	
	
	public void giveItems() {
		ItemStack pelle = new ItemStack(Material.STICK);
		ItemMeta pelleMeta = pelle.getItemMeta();
		pelleMeta.setDisplayName(ChatColor.AQUA+"ThePunch");
		pelleMeta.setLore(Arrays.asList(ChatColor.GRAY+"Un baton qui repousse fort"));
		pelleMeta.addEnchant(Enchantment.KNOCKBACK, 5, true);
		pelleMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		pelleMeta.spigot().setUnbreakable(true);
		pelle.setItemMeta(pelleMeta);
		this.getPlayer().getInventory().setItem(0, pelle);
		
		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowMeta = bow.getItemMeta();
		bowMeta.setDisplayName(ChatColor.AQUA+"Puncher 2000");
		bowMeta.setLore(Arrays.asList(ChatColor.GRAY+"Un arc qui picote"));
		bowMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 4, true);
		bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		bowMeta.spigot().setUnbreakable(true);
		bow.setItemMeta(bowMeta);
		this.getPlayer().getInventory().setItem(1, bow);
		this.getPlayer().getInventory().setItem(28, new ItemStack(Material.ARROW));
	}
	public ColorTeam getColor() {
		return color;
	}
	@SuppressWarnings("deprecation")
	public void setColor(ColorTeam color) {
		this.color.getTeam().removePlayer(this.getPlayer());
		this.color = color;
		this.color.getTeam().addPlayer(this.getPlayer());
	}
}
