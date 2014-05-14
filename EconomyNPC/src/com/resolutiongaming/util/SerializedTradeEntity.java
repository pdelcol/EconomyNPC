package com.resolutiongaming.util;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;

public class SerializedTradeEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String inv;
	public boolean isKitEntity;
	public Location loc;
	public Villager.Profession profession;
	public double x;
	public double y;
	public double z;
	public String worldName;
	public String villagerName;
	public UUID ID;
	public SerializedTradeEntity(double x, double y, double z,String worldName,boolean isKitEntity,String inv, String villagerName, Villager.Profession profession,UUID ID)
	{
		this.inv = inv;
		this.isKitEntity = isKitEntity;
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldName = worldName;
		this.villagerName = villagerName;
		this.profession = profession;
		this.ID = ID;
	}
}
