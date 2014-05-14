package com.resolutiongaming.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.resolutiongaming.pete.EconomyNPC;

public class EntityInteractListener implements Listener{
	EntityStorage entities;
	EconomyNPC plugin;
	PlayerEditList list;
	PriceList prices;
	Economy econ;
	BlacksmithHandler blacksmith;
	BukkitTask task;
	TokenHandler token;
	boolean firstLogon = true;
	String exchangeName = "";
	String kitName = "";
	String npcKitName = "";
	public EntityInteractListener(EconomyNPC plugin, EntityStorage entities,
			PlayerEditList list, PriceList prices, Economy econ, TokenHandler token) {
		this.entities = entities;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.list = list;
		this.prices = prices;
		this.econ = econ;
		blacksmith = new BlacksmithHandler();
		this.token = token;
	}	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(firstLogon)
		{
			BukkitTask task = new LoadTask(entities).runTaskLater(this.plugin, 50);
			firstLogon = false;
		}
		token.checkName(event.getPlayer().getName());
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Entity e = event.getRightClicked();
		Player player = event.getPlayer();
		PermissionUser user = PermissionsEx.getUser(player);
		for (int x = 0; x < entities.list.size(); x++) {
			if (event.getRightClicked() == entities.list.get(x).getVillager()) {
				event.setCancelled(true);
				if (entities.list.get(x).isKitEntity) {
					if (!list.isInList(player.getName())) {
						String groupName = entities.list.get(x).getVillager()
								.getCustomName().substring(2);
						if(groupName.equalsIgnoreCase("Blacksmith"))
						{
							int damage = player.getItemInHand().getDurability() - new ItemStack(player.getItemInHand().getType()).getDurability();
							int costPerDamage = 1;
							
							int multiplyer = 0;
							if(player.getItemInHand().getEnchantments().size() != 0){
								multiplyer = player.getItemInHand().getEnchantments().size();
								Set<Enchantment> keyset = player.getItemInHand().getEnchantments().keySet();
								Iterator<Enchantment> iter = keyset.iterator();
								for(int q = 0; q < player.getItemInHand().getEnchantments().size(); q++)
								{
									if(iter.hasNext())
										multiplyer += player.getItemInHand().getEnchantments().get(iter.next());
								}
							}
							if(damage > 0)
							{
								if(blacksmith.getPlayerName().equalsIgnoreCase("")){
									double cost = damage * costPerDamage;
									if(multiplyer > 0){
										cost = cost * multiplyer;
									}
									player.sendMessage(ChatColor.GOLD + "The tool will cost: " + cost + " if you would like to reforge it, right click the blacksmith again");
									blacksmith.addInfo(cost,player.getName(),player.getItemInHand().getType(),player.getItemInHand().getEnchantments());
									BukkitTask task = new BlacksmithTask(this.plugin, blacksmith).runTaskLater(this.plugin, 100);
									return;
								}
								else if(blacksmith.getPlayerName().equalsIgnoreCase(player.getName()))
								{
									if(player.getItemInHand().getType() == blacksmith.getMaterial()){
										boolean good = true;
										if(player.getItemInHand().getEnchantments().size() == blacksmith.map.size()){
											Set<Enchantment> keyset = player.getItemInHand().getEnchantments().keySet();
											Iterator<Enchantment> iter = keyset.iterator();
											while(iter.hasNext())
											{
												Enchantment enchant = iter.next();
												if(blacksmith.map.containsKey(enchant))
												{
													if(player.getItemInHand().getEnchantmentLevel(enchant) == blacksmith.map.get(enchant))
													{
														
													}else{
														good = false;
													}
												}else{
													good = false;
												}
											}
										}else{
											good = false;
										}
										if(!good)
										{
											player.sendMessage(ChatColor.RED + "Somethings not right with that!");
											return;
										}
										if (econ.withdrawPlayer(player.getName(),blacksmith.getCost()).transactionSuccess())
										{
											player.getItemInHand().setDurability(new ItemStack(blacksmith.getMaterial()).getDurability());
											blacksmith = new BlacksmithHandler();
											return;
										}else{
											player.sendMessage(ChatColor.RED + "You do not have enough money for that :(");
										}
									}
								}
								
							}
							else
							{
								player.sendMessage(ChatColor.GOLD + "You dont need to reforge that!");
							}
						}
						if(groupName.equalsIgnoreCase("Exchange"))
						{
							if(exchangeName.equalsIgnoreCase(player.getName()))
							{
								if(token.removeTokens(player.getName(), 1))
								{
									if(econ.depositPlayer(player.getName(), 2000.0).transactionSuccess())
									{
										player.sendMessage("You sold 1 token for $2000");
										exchangeName = "";
										return;
									}
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Sorry you dont have enough tokens!");
									return;
								}
							}
							else
							{
								player.sendMessage("Do you really want to sell 1 token for $2000? Right click again to confirm");
								exchangeName = player.getName();
								return;
							}
							
						}
						Inventory inv = entities.list.get(x).inv;
						if (inv.getItem(inv.getSize()-1) != null && player.getItemInHand().getType() == inv.getItem(inv.getSize()-1).getType() || inv.getItem(inv.getSize()-1).getType() == new ItemStack(Material.COAL).getType()) {
							
							if(kitName.equalsIgnoreCase(player.getName()) && npcKitName.equalsIgnoreCase(entities.list.get(x).getVillager().getCustomName()))
							{
								npcKitName = "";
								kitName = "";
								ItemStack numTokens = inv.getItem(inv.getSize()-1);
								if(numTokens.getAmount() == player.getItemInHand().getAmount() && inv.getItem(inv.getSize()-1).getType() != Material.COAL)
								{
									player.setItemInHand(new ItemStack(Material.AIR));
								}else if(inv.getItem(inv.getSize()-1).getType() == Material.COAL){
									if(token.removeTokens(player.getName(), inv.getItem(inv.getSize()-1).getAmount()))
									{
										//player.sendMessage("Success!");
									}
									else
									{
										player.sendMessage(ChatColor.RED + "You dont have enough tokens!");
										return;
									}
								}
								else if(numTokens.getAmount() < player.getItemInHand().getAmount() && inv.getItem(inv.getSize()-1).getType() != Material.COAL){
									player.setItemInHand(new ItemStack(player.getItemInHand().getType(), player.getItemInHand().getAmount()-numTokens.getAmount()));
								}
								boolean dropped = false;
								for (int i = 0; i < inv.getSize()-1; i++) {
									if (inv.getItem(i) != null) {
										//int damage = (short)new ItemStack(inv.getItem(i).getType()).getDurability() - inv.getItem(i).getDurability();		
										if(player.getInventory().firstEmpty() != -1){
											player.getInventory()
													.addItem(
															new ItemStack(inv.getItem(i).getType(), inv.getItem(i).getAmount(),inv.getItem(i).getDurability()));
										}
										else
										{
											player.getLocation().getWorld().dropItemNaturally(player.getLocation(), new ItemStack(inv.getItem(i).getType(), inv.getItem(i).getAmount(),inv.getItem(i).getDurability()));
											dropped = true;
										}
									}
								}
								if(dropped)
								{
									player.sendMessage("Your inventory was full so some items were dropped at your feet");
								}
								player.sendMessage(ChatColor.GOLD + "Success!");
							}
							else
							{
								player.sendMessage(ChatColor.GREEN + "This will cost " + inv.getItem(inv.getSize()-1).getAmount() + " token(s). Right click again to continue");
								npcKitName = entities.list.get(x).getVillager().getCustomName();
								kitName = player.getName();
							}
								
							}else{
								player.sendMessage("Wrong type of token!");
							}
						
						
						
					} else if(list.isInList(player.getName())) {
						player.sendMessage(""
								+ entities.list.get(x).isKitEntity);
						Inventory inv = entities.list.get(x).inv;
						event.getPlayer().openInventory(inv);
					}
				} else if (!entities.list.get(x).isKitEntity) {
					Inventory inv = entities.list.get(x).inv;
					event.getPlayer().openInventory(inv);
				}
			}

		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		Inventory inventory = e.getInventory();
		int slot = e.getRawSlot();
		Inventory topInventory = e.getView().getTopInventory();
		
		for (int x = 0; x < entities.list.size(); x++) {
			if (topInventory.getName().equalsIgnoreCase(
					entities.list.get(x).inv.getName())
					&& !list.isInList(player.getName())
					&& !entities.list.get(x).getVillager().getCustomName().substring(2)
							.equalsIgnoreCase("sell")) {
				e.setCancelled(true);
				e.setResult(Result.DENY);
				if (e.getRawSlot() < 27 && e.getRawSlot() > -1) {
					if (e.getAction() == InventoryAction.PICKUP_ALL) {
						
						Material material = clicked.getType();
						if (econ.withdrawPlayer(
								player.getName(),
								prices.getPrice(material, e.getCurrentItem()
										.getAmount())).transactionSuccess()) {
							e.getCursor().setType(Material.AIR);
							e.getCurrentItem().setType(Material.AIR);
							e.setCancelled(true);
							e.setResult(Result.DENY);
							player.closeInventory();
							player.getInventory().addItem(
									new ItemStack(clicked.getType(), clicked
											.getAmount(),clicked.getDurability()));
							player.sendMessage("You bought " + material.name() + " for " + prices.getPrice(material, e.getCurrentItem().getAmount()));
						} else {
							player.sendMessage(ChatColor.GOLD
									+ "Uh Oh you dont have enough funds");
						}
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityInteract(EntityInteractEvent e)
	{
		if(e.getEntityType() == EntityType.VILLAGER)
		{
			Villager villager = (Villager) e.getEntity();
			for(int x = 0; x < entities.list.size(); x++)
			{
				if(villager.getCustomName().equalsIgnoreCase(entities.list.get(x).getVillager().getCustomName()))
				{
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getPlayer();
		for (int x = 0; x < entities.list.size(); x++) {
			if (inv.getName().equalsIgnoreCase(
					entities.list.get(x).inv.getName())
					&& list.isInList(player.getName()) && !entities.list.get(x).isKitEntity) {
				for (int i = 0; i < inv.getSize(); i++) {
					if(inv.getItem(i)!=null && !(inv.getItem(i).getType() == Material.AIR) && !(inv.getItem(i).getType() == null) )
					{
						ItemMeta meta = inv.getItem(i).getItemMeta();
						ArrayList<String> lore = new ArrayList<String>();
						lore.add("Price: "
								+ prices.getPrice(inv.getItem(i).getType(), inv
										.getItem(i).getAmount()));
						meta.setLore(lore);
						inv.getItem(i).setItemMeta(meta);
					}
				}
			}
		}
			if (inv.getName().substring(2).equalsIgnoreCase("sell")) {
				//if (inv.getViewers().size() <= 1) {
					ItemStack item = null;
					ItemStack[] stacks = inv.getContents();
					double totalSold = 0;
					
					for (int i = 0; i < stacks.length; i++) {
						if(stacks[i] != null){
						item = stacks[i];
						if(item.getType() != Material.AIR && item.getType() != null)
						{
							double price = prices.getPrice(item.getType(), item.getAmount());
							price *= .75;
							if (econ.depositPlayer(player.getName(),price).transactionSuccess()) {
								inv.remove(item);
								 totalSold += price;
							}
							else
							{
								player.sendMessage(ChatColor.RED + "No price set for: " + item.getType().name()+" please remove it from Sell NPC");
							}
						}
					}
				}
				player.sendMessage(ChatColor.GOLD + "You sold $" + totalSold + "(s) worth of items");
			}
	}
		
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryOpen(InventoryOpenEvent e) 
	{
		for (int x = 0; x < entities.list.size(); x++)
		{
			if(e.getViewers().size() > 1)
				e.setCancelled(true);
		}
	}
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		for (int x = 0; x < entities.list.size(); x++) {
			if (event.getEntity() == entities.list.get(x).getVillager()) {
				event.setCancelled(true);
			}
		}
	}
}
