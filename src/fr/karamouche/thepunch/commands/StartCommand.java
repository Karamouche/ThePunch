package fr.karamouche.thepunch.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.karamouche.thepunch.Main;
import fr.karamouche.thepunch.objects.Statut;

public class StartCommand implements CommandExecutor {
	private final Main myPlugin;
	public StartCommand(Main main) {
		myPlugin = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdS, String[] args) {
		if(Bukkit.getOnlinePlayers().size() >= 2 && ( myPlugin.getCurrentGame().getStatut().equals(Statut.PREGAME) || myPlugin.getCurrentGame().getStatut().equals(Statut.FINISH)) ) {
			myPlugin.Start();
			return true;
		}
		else if(myPlugin.getCurrentGame().getNbPlayers() < 2)
			sender.sendMessage(myPlugin.getCurrentGame().getTag()+"Il n'y a pas assez de joueur pour commencer");
		else
			sender.sendMessage(myPlugin.getCurrentGame().getTag()+"La game est déjà lancée !");
		return false;
	}

}
