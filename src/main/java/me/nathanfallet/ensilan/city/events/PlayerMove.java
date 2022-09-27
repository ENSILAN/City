package me.nathanfallet.ensilan.city.events;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.nathanfallet.ensilan.city.City;
import me.nathanfallet.ensilan.city.utils.CityChunk;
import me.nathanfallet.ensilan.city.utils.CityPlayer;
import me.nathanfallet.ensilan.core.Core;

public class PlayerMove implements Listener {

	// When player move
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		// Check world
		if (!e.getPlayer().getWorld().equals(Core.getInstance().getSpawn().getWorld())) {
			return;
		}
		
		// Get chunks
		Chunk from = e.getFrom().getChunk();
		Chunk to = e.getTo().getChunk();
		
		// If the player changed of chunk
		if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
			// Get values
			CityChunk zfrom = new CityChunk(from.getX(), from.getZ());
			CityChunk zto = new CityChunk(to.getX(), to.getZ());
			
			// Get owners
			String fromowner = zfrom.getOwner();
			String toowner = zto.getOwner();
			
			// Check if owner is different
			if (!fromowner.equals(toowner)) {
				// Check from
				if (!fromowner.isEmpty()) {
					// Spawn
					if (toowner.isEmpty()) {
						e.getPlayer().sendMessage("§e§lVous quittez le spawn.");
					}
					// Player chunk
					else if (!fromowner.equals("spawn") && !fromowner.equals("null")) {
						CityPlayer zfromowner = City.getInstance().getPlayer(UUID.fromString(fromowner));
						e.getPlayer().sendMessage("§eVous quittez le chunk de §6"+zfromowner.getName()+"§e.");
					}
				}
				
				// Check to
				if (!toowner.isEmpty()) {
					// Spawn
					if (fromowner.isEmpty()) {
						e.getPlayer().sendMessage("§e§lVous enterez dans le spawn.");
					}
					// Already has an owner
					else if (!toowner.equals("spawn") && !toowner.equals("null")) {
						CityPlayer ztoowner = City.getInstance().getPlayer(UUID.fromString(toowner));
						e.getPlayer().sendMessage("§eVous enterez dans le chunk de §6"+ztoowner.getName()+"§e.");
					}
					// Player can buy this chunk
					if (toowner.equals("null")) {
						e.getPlayer().sendMessage("§eVous pouvez acheter ce chunk pour §65 émeraudes §eavec §6/chunk buy§e.");
					}
				}
			}
		}
	}

}
