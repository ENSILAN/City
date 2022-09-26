package me.nathanfallet.ensilan.city;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.ensilan.city.commands.BankCmd;
import me.nathanfallet.ensilan.city.commands.ChunkCmd;
import me.nathanfallet.ensilan.city.commands.StartCmd;
import me.nathanfallet.ensilan.city.events.BlockBreak;
import me.nathanfallet.ensilan.city.events.BlockIgnite;
import me.nathanfallet.ensilan.city.events.BlockPlace;
import me.nathanfallet.ensilan.city.events.CreatureSpawn;
import me.nathanfallet.ensilan.city.events.EntityDamage;
import me.nathanfallet.ensilan.city.events.EntityExplode;
import me.nathanfallet.ensilan.city.events.PlayerInteract;
import me.nathanfallet.ensilan.city.events.PlayerInteractEntity;
import me.nathanfallet.ensilan.city.events.PlayerJoin;
import me.nathanfallet.ensilan.city.events.PlayerMove;
import me.nathanfallet.ensilan.city.utils.GameProcess;
import me.nathanfallet.ensilan.city.utils.ZabriPlayer;
import me.nathanfallet.ensilan.core.Core;
import me.nathanfallet.ensilan.core.interfaces.LeaderboardGenerator;
import me.nathanfallet.ensilan.core.interfaces.ScoreboardGenerator;
import me.nathanfallet.ensilan.core.models.EnsilanPlayer;

public class City extends JavaPlugin {

	// Store instance to get everywhere
	private static City instance;

	public static City getInstance() {
		return instance;
	}

	// Stored properties
	private GameProcess process;

	// Enabling the plugin
	public void onEnable() {
		// Set current instance
		instance = this;

		// Check connection and init
		if (Core.getInstance().getConnection() != null) {
			// Initialize database structure
			try {
				Statement state = Core.getInstance().getConnection().createStatement();
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `city_players` (`uuid` varchar(255) NOT NULL, `emeralds` int(11) NOT NULL DEFAULT '0', PRIMARY KEY (`uuid`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `city_chunks` (`x` int(11) NOT NULL, `z` int(11) NOT NULL, `owner` varchar(255) NOT NULL, `friends` longtext NOT NULL, PRIMARY KEY (`x`, `z`))");
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// Load game process
			process = new GameProcess();

			// Initialize players
			for (Player player : Bukkit.getOnlinePlayers()) {
				initPlayer(player);
			}

			// Initialize leaderboards
			Core.getInstance().getLeaderboardGenerators().put("city", new LeaderboardGenerator() {
				@Override
				public List<String> getLines(int limit) {
					ArrayList<String> lines = new ArrayList<String>();
			
					// Check if the game is playing or stopped
					if (City.getInstance().isPlaying() || City.getInstance().isStopped()) {
						try {
							// Fetch data to MySQL Database
							Statement state = Core.getInstance().getConnection().createStatement();
							ResultSet result = state.executeQuery(
									"SELECT name, emeralds FROM city_players INNER JOIN players ON city_players.uuid = players.uuid WHERE emeralds > 0 AND admin = 0 ORDER BY emeralds DESC LIMIT " + limit);
			
							// Set lines
							while (result.next()) {
								lines.add(result.getString("name") + " §6- §e" + result.getInt("emeralds") + " émeraudes");
							}
							result.close();
							state.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					
					return lines;
				}

				@Override
				public String getTitle() {
					return "Emeraudes";
				}
			});

			// Initialize scoreboard
			Core.getInstance().getScoreboardGenerators().add(new ScoreboardGenerator() {
				@Override
				public List<String> generateLines(Player player, EnsilanPlayer ep) {
					String time = process.toString();
					ZabriPlayer zp = new ZabriPlayer(player);

					ArrayList<String> lines = new ArrayList<String>();
					lines.add("§c");
					lines.add("§c§lBanque : §c(/bank)");
					lines.add("§f" + zp.getEmeralds() + " émeraudes");
					lines.add("§d");
					lines.add("§d§lTemps :");
					lines.add("§f" + time);

					return lines;
				}
			});

			// Register events
			PluginManager pm = Bukkit.getPluginManager();
			pm.registerEvents(new BlockBreak(), this);
			pm.registerEvents(new BlockIgnite(), this);
			pm.registerEvents(new BlockPlace(), this);
			pm.registerEvents(new CreatureSpawn(), this);
			pm.registerEvents(new EntityDamage(), this);
			pm.registerEvents(new EntityExplode(), this);
			pm.registerEvents(new PlayerInteract(), this);
			pm.registerEvents(new PlayerInteractEntity(), this);
			pm.registerEvents(new PlayerJoin(), this);
			pm.registerEvents(new PlayerMove(), this);

			// Register commands
			getCommand("bank").setExecutor(new BankCmd());
			getCommand("chunk").setExecutor(new ChunkCmd());
			getCommand("start").setExecutor(new StartCmd());

			// Update some shown informations
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {
					// Increment timer
					process.increment();
				}
			}, 0, 20);
		}
	}

	// Disable
	public void onDisable() {
		// Save the game
		if (process != null) {
			process.save();
		}
	}

	// Initialize a player
	public void initPlayer(Player p) {
		try {
			// Insert or update player informations
			PreparedStatement state = Core.getInstance().getConnection().prepareStatement(
					"INSERT INTO city_players (uuid) VALUES(?) ON DUPLICATE KEY UPDATE uuid = uuid");
			state.setString(1, p.getUniqueId().toString());
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Check if the game is playing
	public boolean isPlaying() {
		return process.isPlaying();
	}

	// Check if the game is stopped
	public boolean isStopped() {
		return process.isStopped();
	}

	// Start the game
	public void start() {
		process.start();
	}

	// Stop the game
	public void stop() {
		process.stop();
	}

}