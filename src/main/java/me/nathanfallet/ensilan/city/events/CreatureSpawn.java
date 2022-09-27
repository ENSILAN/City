package me.nathanfallet.ensilan.city.events;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import me.nathanfallet.ensilan.city.utils.CityChunk;
import me.nathanfallet.ensilan.core.Core;

public class CreatureSpawn implements Listener {

	// When a creature spawn
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		// Check world
		if (!e.getLocation().getWorld().equals(Core.getInstance().getSpawn().getWorld())) {
			return;
		}

		// Check for exceptions
		if (e.getEntityType().equals(EntityType.VILLAGER) || e.getEntityType().equals(EntityType.ARMOR_STAND)) {
			return;
		}

		// Get targeted location
		Location target = e.getLocation();
		CityChunk zc = new CityChunk(target.getChunk().getX(), target.getChunk().getZ());
		String owner = zc.getOwner();

		// Check if it can't spawn
		if (owner.equals("spawn") || owner.equals("null")) {
			// Cancel spawn
			e.setCancelled(true);
		}
	}

}
