package me.nathanfallet.ensilan.city.events;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.nathanfallet.ensilan.city.utils.CityChunk;

public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager) {
            // Check if entity is on spawn
            CityChunk zc = new CityChunk(
                event.getEntity().getLocation().getChunk().getX(),
                event.getEntity().getLocation().getChunk().getZ()
            );
            if (zc.getOwner().equals("spawn")) {
                // Cancel
                event.setCancelled(true);
            }
        }
    }
    
}
