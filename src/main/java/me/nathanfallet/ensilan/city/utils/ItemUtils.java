package me.nathanfallet.ensilan.city.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ItemUtils {

	// Get recipe list for a villager
	public static List<MerchantRecipe> getRecipes(Profession c) {
		// Create a recipe list
		List<MerchantRecipe> list = new ArrayList<>();

		// Depending of career, fill it
		switch (c) {

		// FARMER villagers
		case FARMER:
			// 16 WHEAT for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.WHEAT, 16), new ItemStack(Material.EMERALD, 1)));

			// 32 SEEDS for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.WHEAT_SEEDS, 32), new ItemStack(Material.EMERALD, 1)));

			// 16 POTATO_ITEM for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.POTATO, 16), new ItemStack(Material.EMERALD, 1)));

			// 16 CARROT_ITEM for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.CARROT, 16), new ItemStack(Material.EMERALD, 1)));

			// 9 PUMPKIN for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.PUMPKIN, 9), new ItemStack(Material.EMERALD, 1)));

			// 9 MELON_BLOCK for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.MELON, 9), new ItemStack(Material.EMERALD, 1)));

			// Exit
			break;

		// CLERIC villagers
		case CLERIC:
			// 32 ROTTEN_FLESH for 1 EMERALD
			list.add(
					createMerchantRecipe(new ItemStack(Material.ROTTEN_FLESH, 32), new ItemStack(Material.EMERALD, 1)));

			// 9 GOLD_INGOT for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.GOLD_INGOT, 9), new ItemStack(Material.EMERALD, 1)));

			// 32 NETHER_STALK for 1 EMERALD
			list.add(
					createMerchantRecipe(new ItemStack(Material.NETHER_WART, 32), new ItemStack(Material.EMERALD, 1)));

			// Exit
			break;

		// LIBRARIAN villagers
		case LIBRARIAN:
			// 32 PAPER for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.PAPER, 32), new ItemStack(Material.EMERALD, 1)));

			// 8 BOOK for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.BOOK, 8), new ItemStack(Material.EMERALD, 1)));

			// 10 EMERALD for 1 ENCHANTED BOOK (Mending)
			list.add(createMerchantRecipe(new ItemStack(Material.EMERALD, 10),
					createEnchantedBook(Enchantment.MENDING, 1)));

			// 10 EMERALD for 1 ENCHANTED BOOK (Fortune)
			list.add(createMerchantRecipe(new ItemStack(Material.EMERALD, 10),
					createEnchantedBook(Enchantment.LOOT_BONUS_BLOCKS, 1)));

			// 10 EMERALD for 1 ENCHANTED BOOK (Silk Touch)
			list.add(createMerchantRecipe(new ItemStack(Material.EMERALD, 10),
					createEnchantedBook(Enchantment.SILK_TOUCH, 1)));

			// Exit
			break;

		// LEATHERWORKER villagers
		case LEATHERWORKER:
			// 10 LEATHER for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.LEATHER, 10), new ItemStack(Material.EMERALD, 1)));

			// 10 RABBIT_HIDE for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.RABBIT_HIDE, 10), new ItemStack(Material.EMERALD, 1)));

			// Exit
			break;

		// FLETCHER villagers
		case FLETCHER:
			// 16 STRING for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.STRING, 16), new ItemStack(Material.EMERALD, 1)));

			// 16 ARROW for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.ARROW, 16), new ItemStack(Material.EMERALD, 1)));

			// Exit
			break;

		// BUTCHER villagers
		case BUTCHER:
			// 16 PORK for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.PORKCHOP, 16), new ItemStack(Material.EMERALD, 1)));

			// 16 RAW_CHICKEN for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.CHICKEN, 16), new ItemStack(Material.EMERALD, 1)));

			// 16 RABBIT for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.RABBIT, 16), new ItemStack(Material.EMERALD, 1)));

			// 32 COAL for 1 EMERALD
			list.add(createMerchantRecipe(new ItemStack(Material.COAL, 32), new ItemStack(Material.EMERALD, 1)));

			// Exit
			break;

		// If nothing from previous cases
		default:
			// Stop here because we have no custom recipe
			break;

		}

		return list;
	}

	// Create a custom recipe
	public static MerchantRecipe createMerchantRecipe(ItemStack from, ItemStack to) {
		MerchantRecipe r = new MerchantRecipe(to, Integer.MAX_VALUE);
		r.addIngredient(from);
		return r;
	}

	// Create an enchanted book
	public static ItemStack createEnchantedBook(Enchantment enchantment, int level) {
		// Create ItemStack
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);

		// Add enchantment
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
		meta.addStoredEnchant(enchantment, level, true);
		book.setItemMeta(meta);

		// Return ItemStack
		return book;
	}

	// Get random chest items
	public static ChestItem[] getRandomChestItems() {
		ChestItem[] available = {
			new ChestItem(Material.ROTTEN_FLESH, 50, 16),
			new ChestItem(Material.STRING, 50, 16),
			new ChestItem(Material.GUNPOWDER, 50, 16),
			new ChestItem(Material.BONE, 50, 16),
			new ChestItem(Material.ARROW, 50, 16),
			new ChestItem(Material.PORKCHOP, 50, 16),
			new ChestItem(Material.CHICKEN, 50, 16),
			new ChestItem(Material.BEEF, 50, 16),
			new ChestItem(Material.WHEAT, 50, 16),
			new ChestItem(Material.POTATO, 50, 16),
			new ChestItem(Material.CARROT, 50, 16),
			new ChestItem(Material.LEATHER, 30, 8),
			new ChestItem(Material.IRON_INGOT, 30, 8),
			new ChestItem(Material.GOLD_INGOT, 20, 8),
			new ChestItem(Material.DIAMOND, 10, 4),
			new ChestItem(Material.EMERALD, 5, 2)
		};

		return available;
	}

}
