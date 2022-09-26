package me.nathanfallet.ensilan.city.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import me.nathanfallet.ensilan.core.Core;

public class ZabriPlayer {

	// Stored properties
	private String uuid;

	// Initializer
	public ZabriPlayer(Player player) {
		this.uuid = player.getUniqueId().toString();
	}
	
	public ZabriPlayer(String uuid) {
		this.uuid = uuid;
	}
	
	// Get UUID
	public String getUUID(){
		return uuid;
	}
	
	// Get name
	public String getName() {
		try {
			PreparedStatement state = Core.getInstance().getConnection().prepareStatement("SELECT name FROM players WHERE uuid = ?");
			state.setString(1, uuid);
			ResultSet result = state.executeQuery();
			result.next();
			String name = result.getString("pseudo");
			result.close();
			state.close();
			return name;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	// Get amount of emeralds
	public int getEmeralds(){
		try {
			PreparedStatement state = Core.getInstance().getConnection().prepareStatement("SELECT emeralds FROM city_players WHERE uuid = ?");
			state.setString(1, uuid);
			ResultSet result = state.executeQuery();
			result.next();
			int emeralds = result.getInt("emeralds");
			result.close();
			state.close();
			return emeralds;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	// Set amount of emeralds
	public void setEmeralds(int emeralds){
		try {
			PreparedStatement state = Core.getInstance().getConnection().prepareStatement("UPDATE city_players SET emeralds = ? WHERE uuid = ?");
			state.setDouble(1, emeralds);
			state.setString(2, getUUID());
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
