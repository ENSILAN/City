package me.nathanfallet.ensilan.city.events;

import java.util.List;

import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.MerchantRecipe;

import me.nathanfallet.ensilan.city.utils.ItemUtils;

public class PlayerInteractEntity implements Listener {

	// When a player interact with an entity
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		// Check if entity is a Villager
		if (!(e.getRightClicked() instanceof Villager)) {
			return;
		}

		// Get villager informations
		Villager v = (Villager) e.getRightClicked();
		Profession p = v.getProfession();

		// Get recipe list
		List<MerchantRecipe> list = ItemUtils.getRecipes(p);

		// Set custom merchant
		if (!list.isEmpty()) {
			v.setRecipes(list);
		}
	}

}
