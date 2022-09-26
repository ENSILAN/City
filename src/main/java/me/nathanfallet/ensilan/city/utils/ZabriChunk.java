package me.nathanfallet.ensilan.city.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.nathanfallet.ensilan.core.Core;

public class ZabriChunk {

	// Stored properties
	private int x;
	private int z;

	// Initializer
	public ZabriChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}

	// Get owner
	public String getOwner() {
		try {
			PreparedStatement state = Core.getInstance().getConnection()
					.prepareStatement("SELECT owner FROM city_chunks WHERE x = ? AND z = ?");
			state.setInt(1, x);
			state.setInt(2, z);
			ResultSet result = state.executeQuery();
			result.next();
			String owner = result.getString("owner");
			result.close();
			state.close();
			return owner;
		} catch (SQLException e) {
		}
		return "";
	}

	// Set owner
	public void setOwner(String owner) {
		try {
			PreparedStatement state = Core.getInstance().getConnection().prepareStatement(
					"INSERT INTO city_chunks (x, z, owner, friends) VALUES(?, ?, ?, '') ON DUPLICATE KEY UPDATE owner = ?");
			state.setInt(1, x);
			state.setInt(2, z);
			state.setString(3, owner);
			state.setString(4, owner);
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Get friends
	public String getFriends() {
		try {
			PreparedStatement state = Core.getInstance().getConnection()
					.prepareStatement("SELECT friends FROM city_chunks WHERE x = ? AND z = ?");
			state.setInt(1, x);
			state.setInt(2, z);
			ResultSet result = state.executeQuery();
			result.next();
			String friends = result.getString("friends");
			result.close();
			state.close();
			return friends;
		} catch (SQLException e) {
		}
		return "";
	}

	// Set owner
	public void setFriends(String friends) {
		try {
			PreparedStatement state = Core.getInstance().getConnection()
					.prepareStatement("UPDATE city_chunks SET friends = ? WHERE x = ? AND z = ?");
			state.setString(1, friends);
			state.setInt(2, x);
			state.setInt(3, z);
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Add friend
	public void addFriend(String uuid) {
		// Get friends
		List<String> friends = new ArrayList<String>(Arrays.asList(getFriends().split(";")));

		// Check if already exists
		if (friends.contains(uuid)) {
			return;
		}

		// Add UUID
		friends.add(uuid);

		// Save
		setFriends(String.join(";", friends));
	}

	// Remove friend
	public void removeFriend(String uuid) {
		// Get friends
		List<String> friends = new ArrayList<String>(Arrays.asList(getFriends().split(";")));

		// Check if already exists
		if (!friends.contains(uuid)) {
			return;
		}

		// Remove UUID
		friends.remove(uuid);

		// Save
		setFriends(String.join(";", friends));
	}

	// Check if a player is allowed to interact
	public boolean isAllowed(String uuid) {
		// Check for owner
		String owner = getOwner();
		if (owner.isEmpty() || owner.equals(uuid)) {
			return true;
		}

		// Check friends
		String[] friends = getFriends().split(";");
		for (String friend : friends) {
			if (friend.equals(uuid)) {
				return true;
			}
		}

		// Player is not allowed
		return false;
	}
}
