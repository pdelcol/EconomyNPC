package com.resolutiongaming.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TradeEntity{
	public Inventory inv;
	public boolean isKitEntity;
	private Villager villager;
	
	public TradeEntity(Villager villager,boolean isKitEntity)
	{
		this.villager = villager;
		inv = Bukkit.createInventory(null, 27, "" + villager.getCustomName());
		this.isKitEntity = isKitEntity;
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,12000,10));
	}
	public void setVillager(Villager villager)
	{
		this.villager = villager;
	}
	public Villager getVillager()
	{
		return villager;
	}
}
