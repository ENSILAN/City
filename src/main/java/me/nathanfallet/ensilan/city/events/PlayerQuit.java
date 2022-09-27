package me.nathanfallet.ensilan.city.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.nathanfallet.ensilan.city.City;
import me.nathanfallet.ensilan.city.utils.CityPlayer;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player data
        CityPlayer player = City.getInstance().getPlayer(event.getPlayer().getUniqueId());
        City.getInstance().getPlayers().remove(player);
    }
    
}
