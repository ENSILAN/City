package me.nathanfallet.ensilan.city.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.nathanfallet.ensilan.city.City;
import me.nathanfallet.ensilan.city.utils.ZabriChunk;
import me.nathanfallet.ensilan.core.Core;

public class BlockPlace implements Listener {

	// When a block is placed
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		// Check if the game is playing
		if (!City.getInstance().isPlaying() && !e.getPlayer().isOp()) {
			e.getPlayer().sendMessage("§cPatientez, la partie n'a pas encore commencé !");
			e.setCancelled(true);
			return;
		}

		// Check world
		if (!e.getBlock().getWorld().equals(Core.getInstance().getSpawn().getWorld())) {
			return;
		}
		
		// Get targeted location
		Location target = e.getBlock().getLocation();
		ZabriChunk zc = new ZabriChunk(target.getChunk().getX(), target.getChunk().getZ());
					
		// Check if can't interact with this block
		if (!zc.isAllowed(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().isOp()) {
			// Cancel interaction
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cVous ne pouvez pas poser de block ici !");
		}
	}

}
