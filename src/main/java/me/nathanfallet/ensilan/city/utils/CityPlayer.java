package me.nathanfallet.ensilan.city.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.nathanfallet.ensilan.core.Core;

public class CityPlayer {

	// Stored properties
	private UUID uuid;

    // Cached current data
    private int emeralds;
    private String name;

	// Initializer
	public CityPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.name = player.getName();

		// Update player data
        try {
            // Database fetch/update/insert
            PreparedStatement fetch = Core.getInstance().getConnection()
                .prepareStatement("SELECT emeralds FROM city_players WHERE uuid = ?");
            fetch.setString(1, uuid.toString());
			ResultSet result = fetch.executeQuery();
            if (result.next()) {
                // Save data to cache
                emeralds = result.getInt("emeralds");
            } else {
                // Save data to cache
                emeralds = 0;

                // Insert
                PreparedStatement insert = Core.getInstance().getConnection()
                    .prepareStatement("INSERT INTO city_players (uuid) VALUES(?)");
                insert.setString(1, uuid.toString());
                insert.executeUpdate();
                insert.close();
            }
            result.close();
            fetch.close();
        } catch (Exception e) {
            // Error, disconnect player
            e.printStackTrace();
            player.kickPlayer("Erreur lors de la vérification de votre identité dans la base de données !");
        }
	}

	public CityPlayer(UUID uuid) {
		this.uuid = uuid;

		// Update player data
        try {
            // Database fetch/update/insert
            PreparedStatement fetch = Core.getInstance().getConnection()
                .prepareStatement("SELECT name, emeralds FROM city_players INNER JOIN players ON city_players.uuid = players.uuid WHERE uuid = ?");
            fetch.setString(1, uuid.toString());
			ResultSet result = fetch.executeQuery();
            if (result.next()) {
                // Save data to cache
                emeralds = result.getInt("emeralds");
				name = result.getString("name");
            } else {
				throw new IllegalArgumentException("Unknown UUID!");
			}
            result.close();
            fetch.close();
        } catch (Exception e) {
            // Error, disconnect player
            e.printStackTrace();
        }
	}
	
	// Get UUID
	public UUID getUUID(){
		return uuid;
	}

	// Cached emeralds
	public int getCachedEmeralds() {
		return emeralds;
	}
	
	// Get name
	public String getName() {
		return name;
	}
	
	// Get amount of emeralds
	public int getEmeralds(){
		try {
			PreparedStatement state = Core.getInstance().getConnection().prepareStatement("SELECT emeralds FROM city_players WHERE uuid = ?");
			state.setString(1, uuid.toString());
			ResultSet result = state.executeQuery();
			result.next();
			emeralds = result.getInt("emeralds");
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emeralds;
	}
	
	// Set amount of emeralds
	public void setEmeralds(int newEmeralds){
		try {
			PreparedStatement state = Core.getInstance().getConnection().prepareStatement("UPDATE city_players SET emeralds = ? WHERE uuid = ?");
			state.setDouble(1, newEmeralds);
			state.setString(2, uuid.toString());
			state.executeUpdate();
			state.close();
			emeralds = newEmeralds;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
