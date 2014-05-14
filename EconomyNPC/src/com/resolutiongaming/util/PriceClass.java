package com.resolutiongaming.util;


import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class PriceClass {
	ItemStack item;
	double price;
	public PriceClass(Material itemMaterial)
	{
		item = new ItemStack(itemMaterial);
	}
	public PriceClass(ItemStack item)
	{
		this.item = item;
	}
	public ItemStack getItem()
	{
		return item;
	}
	public void setItem(ItemStack item)
	{
		this.item = item;
	}
	public double getPrice()
	{
		return price;
	}
	public void setPrice(double price)
	{
		this.price = price;
	}
	public Material getMaterial()
	{
		return item.getType();
	}
}
