package me.nathanfallet.ensilan.city.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public class BlockIgnite implements Listener {

	// When a block is on fire
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		if (e.getCause().equals(IgniteCause.LIGHTNING)) {
			e.setCancelled(true);
		}
	}

}
