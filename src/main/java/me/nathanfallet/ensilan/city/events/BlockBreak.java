package me.nathanfallet.ensilan.city.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.nathanfallet.ensilan.city.City;
import me.nathanfallet.ensilan.city.utils.CityChunk;
import me.nathanfallet.ensilan.core.Core;

public class BlockBreak implements Listener {

	// When a block is broken
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
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
		CityChunk zc = new CityChunk(target.getChunk().getX(), target.getChunk().getZ());

		// Check if can't interact with this block
		if (!zc.isAllowed(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().isOp()) {
			// Cancel interaction
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cVous ne pouvez pas casser ce block !");
		}
	}

}
