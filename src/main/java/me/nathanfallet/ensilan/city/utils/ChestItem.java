package me.nathanfallet.ensilan.city.utils;

import org.bukkit.Material;

public class ChestItem {
	
	// Stored properties
	private Material material;
	private int probability;
	private int amount;
	
	public ChestItem(Material material, int probability, int amount) {
		this.material = material;
		this.probability = probability;
		this.amount = amount;
	}

	// Getters and setters
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public int getProbability() {
		return probability;
	}
	
	public void setProbability(int probability) {
		this.probability = probability;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
