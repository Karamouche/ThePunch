package fr.karamouche.thepunch.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.karamouche.thepunch.Main;
import fr.karamouche.thepunch.objects.Statut;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PersonalScoreboard {
	private final Player player;
    private final UUID uuid;
    private final ObjectiveSign objectiveSign;
    private final Main myPlugin;
    final Date date = new Date();
    private String currentDate = (new SimpleDateFormat("dd-MM-yyyy")).format(date).replace("-", "/");

    PersonalScoreboard(Player player, Main main){
        this.player = player;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar","ThePunch");
        myPlugin = main;
        
        reloadData();
        objectiveSign.addReceiver(player);
    }

    public void reloadData(){}

	public void setLines(String ip){
        objectiveSign.setDisplayName(ChatColor.YELLOW+ "§lThe Punch");
        objectiveSign.setLine(0, ChatColor.GRAY+currentDate);
        objectiveSign.setLine(1, "§1");
        Statut statut = myPlugin.getCurrentGame().getStatut();
        if(statut.equals(Statut.PREGAME) || statut.equals(Statut.STARTING)) {
        	objectiveSign.setLine(2, "§e§lCoins");
        	objectiveSign.setLine(3, "§7" + "0" /*main.getApi().getEcoManager().getBalanceCoins(player)*/);    
        	objectiveSign.setLine(4, "§2");
        	objectiveSign.setLine(5, "§e§lVos Stats");
        	objectiveSign.setLine(6, "§3Parties jouées: §b" +" 0" /*main.getPlayersManager().getProfile(player).getGlobalGamesPlayed()*/);
        	objectiveSign.setLine(7, "§3Victoires: §b" +" 0" /*main.getPlayersManager().getProfile(player).getGlobalWins()*/);
        	objectiveSign.setLine(8, "§3Kills: §b" +" 0" /*main.getPlayersManager().getProfile(player).getGlobalKills()*/);
        	objectiveSign.setLine(9, "§3");
        	objectiveSign.setLine(10, "§c§lDémarrage:");
        	
        	final int missingPlayers = myPlugin.getCurrentGame().getMaxPlayer()-myPlugin.getCurrentGame().getNbPlayers();
        	if(missingPlayers == 1)
        		objectiveSign.setLine(11, ChatColor.GRAY+"En attente de "+ChatColor.YELLOW+missingPlayers+ChatColor.GRAY+" joueur");
        	else if(statut.equals(Statut.STARTING)) {
        		//METTRE LE TIMER
            	objectiveSign.setLine(11, ChatColor.GRAY+"En cours");
        	}
        	else
        		objectiveSign.setLine(11, ChatColor.GRAY+"En attente de "+ChatColor.YELLOW+missingPlayers+ChatColor.GRAY+" joueurs");
        	objectiveSign.setLine(12, "§4");
        	objectiveSign.setLine(13, "§8» " + ip);
        }else if(statut.equals(Statut.START)) {
        	if(myPlugin.getCurrentGame().getPunchers().containsKey(player)) {
	        	objectiveSign.setLine(2, ChatColor.AQUA+"Joueurs en vie : "+ChatColor.DARK_AQUA+myPlugin.getCurrentGame().getNbPlayers());
	        	objectiveSign.setLine(3, "§2");
	        	objectiveSign.setLine(4, ChatColor.YELLOW+"Kills : "+ChatColor.RESET+myPlugin.getCurrentGame().getPunchers().get(player).getKills());
	        	objectiveSign.setLine(5, ChatColor.YELLOW+"Vies : "+ChatColor.RESET+myPlugin.getCurrentGame().getPunchers().get(player).getLifes());
	        	for(int i = 6 ; i<13; i++)
	        		objectiveSign.setLine(i, "§3");
	        	objectiveSign.setLine(13, "§8» " + ip);
	        	
	        }
	        else {
	        	objectiveSign.setLine(2, "§2");
	        	objectiveSign.setLine(3, ChatColor.AQUA+"Joueurs en vie: "+ChatColor.DARK_AQUA+myPlugin.getCurrentGame().getNbPlayers());
	        	objectiveSign.setLine(4, "§3");
	        	ArrayList<String> classement = myPlugin.getCurrentGame().getTop3();
	        	//RAJOUTER UN CLASSEMENT /3
	        	if(classement.size()>=3) {
	        		objectiveSign.setLine(5, classement.get(0));
	        		objectiveSign.setLine(6, classement.get(1));
	        		objectiveSign.setLine(7, classement.get(2));
	        		objectiveSign.setLine(8, "§4");
	        		objectiveSign.setLine(9, "§8» " + ip);
	        	} else if(classement.size() == 2) {
	        		objectiveSign.setLine(5, classement.get(0));
	        		objectiveSign.setLine(6, classement.get(1));
	        		objectiveSign.setLine(7, "§4");
	        		objectiveSign.setLine(8, "§8» " + ip);
	        	} else if(classement.size() == 1) {
	        		objectiveSign.setLine(5, classement.get(0));
	        		objectiveSign.setLine(6, "§4");
	        		objectiveSign.setLine(7, "§8» " + ip);
	        	} else
	        		objectiveSign.setLine(5, "§8» " + ip);
	        }
        }else if(statut.equals(Statut.FINISH)) {
			objectiveSign.setLine(2, "§2");
			objectiveSign.setLine(3, "§cFin de la partie §e§lThe Punch §c!");
			objectiveSign.setLine(4, "§cVous allez être envoyé");
			objectiveSign.setLine(5, "§cdans une nouvelle partie");
			objectiveSign.setLine(6, "§cdans quelques instants !");
			objectiveSign.setLine(7, "§3");
			objectiveSign.setLine(8, "§8» " + ip);
        }
        objectiveSign.updateLines();
    }

    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
    }
}