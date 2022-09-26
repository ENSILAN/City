package me.nathanfallet.ensilan.city.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nathanfallet.ensilan.city.City;

public class StartCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp()) {
			if (City.getInstance().isPlaying()) {
				// Stop the game
				City.getInstance().stop();
			} else {
				// Start the game
				City.getInstance().start();
			}
		} else {
			sender.sendMessage("§cVous n'avez pas le droit d'utiliser cette commande !");
		}
		return true;
	}

}
