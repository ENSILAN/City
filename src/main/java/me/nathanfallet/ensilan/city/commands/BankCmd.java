package me.nathanfallet.ensilan.city.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nathanfallet.ensilan.city.City;
import me.nathanfallet.ensilan.city.utils.CityPlayer;

public class BankCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!City.getInstance().isPlaying()) {
			sender.sendMessage("§cLa banque n'est pas encore disponible !");
			return true;
		}
		if (sender instanceof Player) {
			// Get basic informations and balance
			Player player = (Player) sender;
			CityPlayer zp = City.getInstance().getPlayer(player.getUniqueId());
			int balance = zp.getEmeralds();

			// Check sub command
			if (args.length > 0) {
				// Show player balance
				if (args[0].equalsIgnoreCase("balance")) {
					player.sendMessage("§6Vous avez §e" + balance + " émeraudes§6 sur votre compte.");
				}
				// Deposit
				else if (args[0].equalsIgnoreCase("deposit") && args.length == 2) {
					try {
						// Parse amount
						int amount = Integer.parseInt(args[1]);

						// Check if player has enough
						if (player.getInventory().contains(Material.EMERALD, amount)) {
							// Remove from inventory and add on account
							balance += amount;
							player.getInventory().removeItem(new ItemStack(Material.EMERALD, amount));
							zp.setEmeralds(balance);
							player.sendMessage("§aVous venez de déposer §2" + amount
									+ " émeraudes §asur votre compte !\n§eVous avez maintenant §6" + balance
									+ " émeraudes §esur votre compte");
						} else {
							// Warn
							player.sendMessage("§cVous n'avez pas assez d'émeraudes dans votre inventaire !");
						}
					} catch (NumberFormatException e) {
						player.sendMessage("§4" + args[1] + " §cn'est pas un nombre valide !");
					}
				}
				// Retrieve
				else if (args[0].equalsIgnoreCase("retrieve") && args.length == 2) {
					try {
						// Parse amount
						int amount = Integer.parseInt(args[1]);

						// Check if player has enough
						if (balance >= amount) {
							// Remove from account and add in inventory
							balance -= amount;
							zp.setEmeralds(balance);
							player.getInventory().addItem(new ItemStack(Material.EMERALD, amount));
							player.sendMessage("§aVous venez de retirer §2" + amount
									+ " émeraudes §ade votre compte !\n§eVous avez maintenant §6" + balance
									+ " émeraudes §esur votre compte");
						} else {
							// Warn
							player.sendMessage("§cVous n'avez pas assez d'émeraudes sur votre compte !");
						}
					} catch (NumberFormatException e) {
						player.sendMessage("§4" + args[1] + " §cn'est pas un nombre valide !");
					}
				}
				// Show help
				else {
					sendHelp(sender);
				}
			} else {
				// Show help
				sendHelp(sender);
			}
		} else {
			sender.sendMessage("§cSeuls les joueurs peuvent éxecuter cette commande !");
		}

		return true;
	}
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage("§e---- §6Bank §e----\n"
				+ "§6/bank balance§f: Afficher le nombre d'émeraudes sur votre compte\n"
				+ "§6/bank deposit <amount>§f: Déposer <amount> émeraudes sur votre compte\n"
				+ "§6/bank retrieve <amount>§f: Retirer <amount> émeraudes de votre compte\n");
	}

}
