package me.nathanfallet.ensilan.city.events;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.nathanfallet.ensilan.city.City;

public class PlayerJoin implements Listener {
	
	// When a player join the server
	@EventHandler
	public void onPlayerJoinLast(PlayerJoinEvent e) {
		// Initialize the player
		City.getInstance().initPlayer(e.getPlayer());
		
		// Define the game mode and name correctly
		if (e.getPlayer().isOp()) {
			// Player is operator
			e.getPlayer().setGameMode(GameMode.CREATIVE);
			e.getPlayer().setDisplayName("§c"+e.getPlayer().getName());
		} else if (City.getInstance().isPlaying()) {
			// Game is started
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
			e.getPlayer().setDisplayName(e.getPlayer().getName());
		} else {
			// Game is not yet started
			e.getPlayer().setGameMode(GameMode.ADVENTURE);
			e.getPlayer().setDisplayName(e.getPlayer().getName());
		}
	}

}
