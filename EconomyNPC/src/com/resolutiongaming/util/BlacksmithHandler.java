package com.resolutiongaming.util;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.scheduler.BukkitRunnable;

public class BlacksmithHandler extends BukkitRunnable{
	double cost;
	String playerName;
	Material material;
	Map<Enchantment, Integer> map;
	public BlacksmithHandler(int cost, String playerName, Material material, Map<Enchantment, Integer> map)
	{
		this.cost = cost;
		this.playerName = playerName;
		this.material = material;
		this.map = map;
	}
	public BlacksmithHandler()
	{
		cost = 0.0;
		playerName = "";
		material = Material.AIR;
	}
	public void removeInfo()
	{
		cost = 0.0;
		playerName = "";
		material = Material.AIR;
	}
	public void addInfo(double cost, String playerName,Material material, Map<Enchantment, Integer> map)
	{
		this.cost = cost;
		this.playerName = playerName;
		this.material = material;
		this.map = map;
	}
	public double getCost()
	{
		return cost;
	}
	public String getPlayerName()
	{
		return playerName;
	}
	public Material getMaterial()
	{
		return material;
	}
	@Override
	public void run() {
		new BlacksmithHandler();
	}
}