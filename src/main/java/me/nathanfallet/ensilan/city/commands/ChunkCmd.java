package me.nathanfallet.ensilan.city.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.ensilan.city.City;
import me.nathanfallet.ensilan.city.utils.ZabriChunk;
import me.nathanfallet.ensilan.city.utils.ZabriPlayer;

public class ChunkCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!City.getInstance().isPlaying() && !sender.isOp()) {
			sender.sendMessage("§cLa gestion des chunks n'est pas encore disponible !");
			return true;
		}
		if (sender instanceof Player) {
			// Get basic informations and balance
			Player player = (Player) sender;
			ZabriPlayer zp = new ZabriPlayer(player);
			ZabriChunk zc = new ZabriChunk(player.getLocation().getChunk().getX(),
					player.getLocation().getChunk().getZ());
			String owner = zc.getOwner();

			// Check sub command
			if (args.length > 0) {
				// Info
				if (args[0].equalsIgnoreCase("info")) {
					if (owner.isEmpty()) {
						player.sendMessage("§eIl n'y a rien à savoir sur ce chunk.");
					} else if (owner.equals("null")) {
						player.sendMessage("§eVous pouvez acheter ce chunk pour §65 émeraudes §eavec §6/chunk buy§e.");
					} else if (owner.equals("spawn")) {
						player.sendMessage("§eCe chunk fait partie du spawn.");
					} else {
						ZabriPlayer zowner = new ZabriPlayer(owner);
						player.sendMessage("§eCe chunk appartient à §6" + zowner.getName() + "§e.");
					}
				}
				// Buy
				else if (args[0].equalsIgnoreCase("buy")) {
					// Check is the chunk is available
					if (owner.equals("null")) {
						// Get player balance
						int balance = zp.getEmeralds();

						// Check if he has enough to buy it
						if (balance >= 5) {
							// Update balance and chunk owner
							balance -= 5;
							zp.setEmeralds(balance);
							zc.setOwner(player.getUniqueId().toString());
							player.sendMessage("§aCe chunk vous appartient désormais !");
						} else {
							player.sendMessage("§cVous n'avez pas assez d'émeraudes pour acheter ce chunk !");
						}
					} else {
						player.sendMessage("§cCe chunk n'est pas disponible !");
					}
				}
				// Sell
				else if (args[0].equalsIgnoreCase("sell")) {
					// Check if the player owns the chunk
					if (owner.equals(player.getUniqueId().toString())) {
						// Get player balance and update it
						int balance = zp.getEmeralds() + 5;
						zp.setEmeralds(balance);
						zc.setOwner("null");
						player.sendMessage("§aVous avez vendu ce chunk pour §25 émeraudes§a!");
					} else if ((owner.isEmpty() || owner.equals("spawn")) && player.isOp()) {
						// Create the chunk
						zc.setOwner("null");

						// Mark chunk borders
						Location zero = player.getLocation().getChunk().getBlock(0, 0, 0).getLocation();
						player.getWorld().getHighestBlockAt(zero).setType(Material.OAK_FENCE);
						player.getWorld().getHighestBlockAt(zero.add(15, 0, 0)).setType(Material.OAK_FENCE);
						player.getWorld().getHighestBlockAt(zero.add(0, 0, 15)).setType(Material.OAK_FENCE);
						player.getWorld().getHighestBlockAt(zero.add(-15, 0, 0)).setType(Material.OAK_FENCE);

						player.sendMessage("§aCe chunk est maintenant disponible !");
					} else {
						player.sendMessage("§cCe chunk ne vous appartient pas !");
					}
				}
				// Claim to spawn
				else if (args[0].equalsIgnoreCase("spawn") && player.isOp()) {
					// Create the chunk
					zc.setOwner("spawn");

					player.sendMessage("§aCe chunk fait maintenant partie du spawn !");
				}
				// Add friend to current chunk
				else if (args[0].equalsIgnoreCase("addfriend") && args.length == 2) {
					// Check if the player owns the chunk
					if (owner.equals(player.getUniqueId().toString()) || player.isOp()) {
						// Get player
						Player target = Bukkit.getPlayer(args[1]);

						if (target != null && target.isOnline()) {
							// Add it to chunk
							zc.addFriend(target.getUniqueId().toString());

							player.sendMessage("§aCe joueur peut désormais accéder à ce chunk !");
						} else {
							// Player not found
							player.sendMessage("§cImpossible de trouver ce joueur !");
						}
					} else {
						player.sendMessage("§cCe chunk ne vous appartient pas !");
					}
				}
				// Remove friend to current chunk
				else if (args[0].equalsIgnoreCase("removefriend") && args.length == 2) {
					// Check if the player owns the chunk
					if (owner.equals(player.getUniqueId().toString()) || player.isOp()) {
						// Get player
						Player target = Bukkit.getPlayer(args[1]);

						if (target != null && target.isOnline()) {
							// Add it to chunk
							zc.removeFriend(target.getUniqueId().toString());

							player.sendMessage("§aCe joueur ne peut désormais plus accéder à ce chunk !");
						} else {
							// Player not found
							player.sendMessage("§cImpossible de trouver ce joueur !");
						}
					} else {
						player.sendMessage("§cCe chunk ne vous appartient pas !");
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
		sender.sendMessage("§e---- §6Chunks §e----\n" + "§6/chunk info§f: Informations sur ce chunk\n"
				+ "§6/chunk buy§f: Acheter ce chunk pour 5 émeraudes\n"
				+ "§6/chunk sell§f: Vendre ce chunk pour 5 émeraudes (if you own it)\n"
				+ "§6/chunk addfriend <player>§f: Ajouter <player> à ce chunk\n"
				+ "§6/chunk removefriend <player>§f: Supprimer <player> de ce chunk\n");
	}

}
