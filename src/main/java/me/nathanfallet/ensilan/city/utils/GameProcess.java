package me.nathanfallet.ensilan.city.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nathanfallet.ensilan.core.Core;
import me.nathanfallet.ensilan.core.models.AbstractGame;

public class GameProcess extends AbstractGame {

	// Stored properties
	private int minute;
	private int hour;
	private int day;
	private int message;

	// Initializer
	public GameProcess() {
		// Get from file
		File f = new File("plugins/City/game_process.yml");
		if (!f.exists()) {
			return;
		}

		// Load vars
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		boolean playing = config.getBoolean("playing");
		boolean stopped = config.getBoolean("stopped");
		state = playing ? GameState.IN_GAME : stopped ? GameState.FINISHED : GameState.WAITING;
		minute = config.getInt("minute");
		hour = config.getInt("hour");
		day = config.getInt("day");
		message = config.getInt("message");

		// Update time in worlds
		for (World w : Bukkit.getWorlds()) {
			w.setTime(day * 24_000 + hour * 1_200 + minute * 20);
		}
	}

	// Save values to file
	public void save() {
		File f = new File("plugins/City/game_process.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);

		config.set("playing", state == GameState.IN_GAME);
		config.set("stopped", state == GameState.FINISHED);
		config.set("minute", minute);
		config.set("hour", hour);
		config.set("day", day);
		config.set("message", message);

		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Countdown before the start of the game. Zero to disable
	@Override
	public int getCountdown() {
		return 0;
	}

	// Number of players required for the game to start
	@Override
	public int getMinPlayers() {
		return 0;
	}

	// Max number of players in the game
	@Override
	public int getMaxPlayers() {
		return 0;
	}

	// Name of the game
	@Override
	public String getGameName() {
		return "City";
	}

	// Number of the game
	@Override
	public int getGameNumber() {
		return 1;
	}

	// Handle the start process of the game
	@Override
	public void start() {
		// Set playing and time
		state = GameState.IN_GAME;
		minute = 0;
		hour = 0;
		day = 0;
		message = 0;

		// Set world time
		for (World w : Bukkit.getWorlds()) {
			w.setTime(0);
			w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		}

		// Broadcast it
		Bukkit.broadcastMessage("§6§lLa partie commence, que le meilleur gagne !");

		// Change players game mode
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.isOp()) {
				player.setGameMode(GameMode.SURVIVAL);
			}
		}
	}

	// Handle the stop process of the game
	@Override
	public void stop() {
		// Mark game as finished
		state = GameState.FINISHED;

		// Broadcast it
		Bukkit.broadcastMessage("§6§lC'est terminé !");
		Bukkit.broadcastMessage("§6§lAllez voir le classement pour savoir qui a gagné.");

		// Change players game mode
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.isOp()) {
				player.setGameMode(GameMode.ADVENTURE);
			}
		}
	}

	// Called every second
	@Override
	public void mainHandler() {
		// Increment minute
		minute++;

		// If 60 minutes, increment hour
		if (minute > 59) {
			minute = 0;
			hour++;

			// If hour is multiple of 5, show a tip
			if (hour % 5 == 0) {
				tip();
			}
		}

		// If 20 hours, increment day
		if (hour > 19) {
			hour = 0;
			day++;

			// If day is multiple of 3, pop a chest
			if (day % 3 == 0) {
				popRandomChest();
			}
		}
	}

	// Get players participating in the game (excluding those who lost)
	@Override
	public ArrayList<UUID> getPlayers() {
		// In the city, every online player is playing
		ArrayList<UUID> result = new ArrayList<UUID>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			result.add(p.getUniqueId());
		}
		return result;
	}

	// Get all players of the game, even those who lost
	@Override
	public ArrayList<UUID> getAllPlayers() {
		// In the city, every online player is playing
		return getPlayers();
	}

	// Get time as string
	@Override
	public String getGameDescription() {
		if (state == GameState.IN_GAME) {
			return "Jour " + (day + 1) + " - " + (hour >= 10 ? hour : ("0" + hour)) + ":"
					+ (minute >= 10 ? minute : ("0" + minute));
		} else if (state == GameState.FINISHED) {
			return "Terminé !";
		} else {
			return "En attente...";
		}
	}

	// Pop a random chest
	public void popRandomChest() {
		// Initializations
		Location location;
		Random random = new Random();

		// Determine a safe location
		while (true) {
			// Create a random location
			location = Core.getInstance().getSpawn().add(random.nextDouble() * 1000,
					-random.nextDouble() * 30, random.nextDouble() * 1000);
			CityChunk zc = new CityChunk(location.getChunk().getX(), location.getChunk().getZ());

			// Check if chunk is safe
			if (zc.getOwner().isEmpty()) {
				break;
			}
		}

		// Create the chest
		location.getBlock().setType(Material.CHEST);
		Chest chest = (Chest) location.getBlock().getState();

		// Fill with random items
		for (ChestItem item : ItemUtils.getRandomChestItems()) {
			// Check probability
			if (random.nextInt(100) < item.getProbability()) {
				// Set randomly in chest
				chest.getInventory().setItem(random.nextInt(27),
						new ItemStack(item.getMaterial(), 1 + random.nextInt(item.getAmount())));
			}
		}

		// Broadcast chest location
		Bukkit.broadcastMessage("§6§lUn coffre est apparu en " + location.getBlockX() + " / " + location.getBlockY()
				+ " / " + location.getBlockZ() + ". Qui le touvera en premier ?");
	}

	// Chat tip
	public void tip() {
		// List of messages
		String[] messages = {
				"N'oubliez pas de déposer vos émeraudes à la banque !",
				"Faites des échanges avec les villageois pour obtenir des émeraudes !",
				"Soyez le premier à trouver un coffre caché !",
				"Achetez un chunk au spawn pour protéger vos coffres !"
		};

		// Broadcast current
		Bukkit.broadcastMessage("§6§l[Info] §e" + messages[message]);

		// Increment
		message++;

		// Reset if too high
		if (message >= messages.length) {
			message = 0;
		}
	}

}
