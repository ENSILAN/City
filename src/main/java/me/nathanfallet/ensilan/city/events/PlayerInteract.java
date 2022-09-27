package me.nathanfallet.ensilan.city.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.nathanfallet.ensilan.city.City;
import me.nathanfallet.ensilan.city.utils.CityChunk;
import me.nathanfallet.ensilan.core.Core;

public class PlayerInteract implements Listener {

	// When player interacts
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		// Check if the game is playing
		if (!City.getInstance().isPlaying() && !e.getPlayer().isOp()) {
			e.getPlayer().sendMessage("§cPatientez, la partie n'a pas encore commencé !");
			e.setCancelled(true);
			return;
		}

		// Check world
		if (!e.getPlayer().getWorld().equals(Core.getInstance().getSpawn().getWorld())) {
			return;
		}

		// Check if interaction is linked with a block
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)
				|| e.getAction().equals(Action.PHYSICAL)) {
			// Get targeted location
			Location target = e.getClickedBlock().getLocation();

			// Check for exceptions
			if (target.getBlock().getType().equals(Material.WATER) // What about STATIONARY_WATER?
					|| target.getBlock().getType().equals(Material.RAIL)
					|| target.getBlock().getType().equals(Material.POWERED_RAIL)) {
				return;
			}

			// Get chunk owner
			CityChunk zc = new CityChunk(target.getChunk().getX(), target.getChunk().getZ());

			// Check if can't interact with this block
			if (!zc.isAllowed(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().isOp()) {
				// Cancel interaction
				e.setCancelled(true);
				e.getPlayer().sendMessage("§cVous ne pouvez pas intéragir avec ce block !");
			}
		}
	}

}
