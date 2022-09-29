package me.nathanfallet.ensilan.city;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.ensilan.city.commands.BankCmd;
import me.nathanfallet.ensilan.city.commands.ChunkCmd;
import me.nathanfallet.ensilan.city.commands.StartCmd;
import me.nathanfallet.ensilan.city.events.CreatureSpawn;
import me.nathanfallet.ensilan.city.events.PlayerInteractEntity;
import me.nathanfallet.ensilan.city.events.PlayerJoin;
import me.nathanfallet.ensilan.city.events.PlayerMove;
import me.nathanfallet.ensilan.city.events.PlayerQuit;
import me.nathanfallet.ensilan.city.utils.GameProcess;
import me.nathanfallet.ensilan.city.utils.CityChunk;
import me.nathanfallet.ensilan.city.utils.CityPlayer;
import me.nathanfallet.ensilan.core.Core;
import me.nathanfallet.ensilan.core.interfaces.LeaderboardGenerator;
import me.nathanfallet.ensilan.core.interfaces.ScoreboardGenerator;
import me.nathanfallet.ensilan.core.interfaces.WorldProtectionRule;
import me.nathanfallet.ensilan.core.models.EnsilanPlayer;
import me.nathanfallet.ensilan.core.models.AbstractGame.GameState;

public class City extends JavaPlugin {

	// Store instance to get everywhere
	private static City instance;

	public static City getInstance() {
		return instance;
	}

	// Stored properties
	private GameProcess process;
	private List<CityPlayer> players;

	// Enabling the plugin
	public void onEnable() {
		// Set current instance
		instance = this;

		// Check connection
		if (!initDatabase()) {
			return;
		}

		// Load game process
		process = new GameProcess();
		Core.getInstance().getGames().add(process);

		// Init players
		for (Player player : Bukkit.getOnlinePlayers()) {
			getPlayers().add(new CityPlayer(player));
		}

		// World protection rules
		Core.getInstance().getWorldProtectionRules().add(new WorldProtectionRule() {
			@Override
			public boolean isAllowedInProtectedLocation(Player player, EnsilanPlayer ep, Location location, Event event) {
				return player.isOp();
			}
			@Override
			public boolean isProtected(Location location) {
				return !isPlaying();
			}
		});
		Core.getInstance().getWorldProtectionRules().add(new WorldProtectionRule() {
			@Override
			public boolean isAllowedInProtectedLocation(Player player, EnsilanPlayer ep, Location location, Event event) {
				if (
					event instanceof PlayerInteractEntityEvent &&
					((PlayerInteractEntityEvent) event).getRightClicked() instanceof Villager
				) {
					// Allow villagers (for trades)
					return true;
				}

				CityChunk zc = new CityChunk(location.getChunk().getX(), location.getChunk().getZ());
				return player.isOp() || zc.isAllowed(player.getUniqueId().toString());
			}
			@Override
			public boolean isProtected(Location location) {
				return location.getWorld().getName().equals(Core.getInstance().getSpawn().getWorld().getName());
			}
		});

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
							"SELECT name, emeralds FROM city_players " +
							"INNER JOIN players ON city_players.uuid = players.uuid " +
							"WHERE emeralds > 0 " +
							"ORDER BY emeralds DESC " +
							"LIMIT " + limit
						);

						// Set lines
						while (result.next()) {
							lines.add(
                                result.getString("name") +
                                ChatColor.GOLD + " - " + ChatColor.YELLOW +
                                result.getInt("emeralds") + " émeraudes"
                            );
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
				CityPlayer zp = getPlayer(player.getUniqueId());

				ArrayList<String> lines = new ArrayList<String>();
				lines.add("§c");
				lines.add("§c§lBanque : §c(/bank)");
				lines.add("§f" + zp.getCachedEmeralds() + " émeraudes");
				lines.add("§d");
				lines.add("§d§lTemps :");
				lines.add("§f" + process.getGameDescription());

				return lines;
			}
		});

		// Register events
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new CreatureSpawn(), this);
		pm.registerEvents(new PlayerInteractEntity(), this);
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		pm.registerEvents(new PlayerMove(), this);

		// Register commands
		getCommand("bank").setExecutor(new BankCmd());
		getCommand("chunk").setExecutor(new ChunkCmd());
		getCommand("start").setExecutor(new StartCmd());
	}

	// Disable
	public void onDisable() {
		// Save the game
		if (process != null) {
			process.save();
		}
		players = null;
	}

	// Initialize database
	private boolean initDatabase() {
		try {
			Statement create = Core.getInstance().getConnection().createStatement();
			create.executeUpdate("CREATE TABLE IF NOT EXISTS `city_players` (" +
				"`uuid` varchar(255) NOT NULL," +
				"`emeralds` int(11) NOT NULL DEFAULT '0'," +
				"PRIMARY KEY (`uuid`)" +
			")");
			create.executeUpdate("CREATE TABLE IF NOT EXISTS `city_chunks` (" +
				"`x` int(11) NOT NULL," +
				"`z` int(11) NOT NULL," +
				"`owner` varchar(255) NOT NULL," +
				"`friends` longtext NOT NULL," +
				"PRIMARY KEY (`x`, `z`)" +
			")");
			create.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Retrieve players
	public List<CityPlayer> getPlayers() {
		// Init players if needed
		if (players == null) {
			players = new ArrayList<>();
		}

		// Return players
		return players;
	}

    // Retrieve a player from its UUID
    public CityPlayer getPlayer(UUID uuid) {
        // Iterate players
        for (CityPlayer player : getPlayers()) {
            if (player.getUUID().equals(uuid)) {
                return player;
            }
        }

        // No player found
        return null;
    }

	// Check if the game is playing
	public boolean isPlaying() {
		return process.getState() == GameState.IN_GAME;
	}

	// Check if the game is stopped
	public boolean isStopped() {
		return process.getState() == GameState.FINISHED;
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
