package me.nathanfallet.ensilan.city.events;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.nathanfallet.ensilan.city.utils.CityChunk;
import me.nathanfallet.ensilan.core.Core;

public class EntityDamage implements Listener {

	// When entity damages
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		// Check world
		if (!e.getEntity().getLocation().getWorld().equals(Core.getInstance().getSpawn().getWorld())) {
			return;
		}
		
		// Check for exceptions
		if (!e.getEntityType().equals(EntityType.PLAYER) && !e.getEntityType().equals(EntityType.VILLAGER)) {
			return;
		}

		// Get targeted location
		Location target = e.getEntity().getLocation();
		CityChunk zc = new CityChunk(target.getChunk().getX(), target.getChunk().getZ());
		String owner = zc.getOwner();

		// Check if it can damage
		if (!owner.isEmpty()) {
			// Cancel damages
			e.setCancelled(true);
		}
	}

}
