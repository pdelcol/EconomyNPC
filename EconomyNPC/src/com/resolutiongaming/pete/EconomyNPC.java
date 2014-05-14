package com.resolutiongaming.pete;


import java.util.Iterator;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_7_R1.MerchantRecipeList;
import net.minecraft.server.v1_7_R1.NBTTagCompound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPig;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftVillager;

import com.resolutiongaming.util.EntityInteractListener;
import com.resolutiongaming.util.EntityStorage;
import com.resolutiongaming.util.PlayerEditList;
import com.resolutiongaming.util.PriceList;
import com.resolutiongaming.util.TokenHandler;
import com.resolutiongaming.util.TransactionHandler;

public class EconomyNPC extends JavaPlugin{
	PluginDescriptionFile pdfFile = this.getDescription();
	EntityStorage entities;
	EntityInteractListener listeners;
	PlayerEditList list;
	Economy econ;
	PriceList prices;
	TokenHandler token;
	TransactionHandler handler;
	String[] commandList = {"/npc [kit/essentials] [name] [profession #] : The profession is the shirt color", 
			"/npc token : Checks your token count", "/npc token add [player] [#] : Player is capital sensitive", "/npc newprice [item #] [price]: note price needs to be in decimal format ex. 1.0"
			, "/npc save : Save all NPC's", "/npc kill [npc name] : Kills the specified NPC", "/npc edit : Allows you to edit NPC's"};
	
	public void onEnable()
	{
		list = new PlayerEditList(this);
		prices = new PriceList(this);
		token = new TokenHandler();
		token.load();
		handler = new TransactionHandler(this,token);
		if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		prices.load();
		
		entities = new EntityStorage(this, prices);
		entities.loadEntities();
		
		listeners = new EntityInteractListener(this,entities,list,prices,econ,token);
		pdfFile = this.getDescription();
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is Enabled!");
	}
	
	public void onDisable()
	{
		token.save();
		entities.saveEntities(true);
		prices.save();
		getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is Disabled!");
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player player = null;
		if(sender instanceof Player)
		{
			 player = (Player) sender;
		}
		if(cmd.getName().equalsIgnoreCase("tokens"))
		{
			player.sendMessage(ChatColor.GOLD + "You have: " + token.getNumTokens(player.getName()) + " token(s)");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("tokens") && args.length == 2 && player.hasPermission("npc.trade"))
		{
			handler.addTransaction(player.getName(), (String)args[0], Integer.parseInt(args[1]));
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("npcaccept"))
		{
			if(handler.receiverList.containsKey(player.getName())){
				handler.processTransaction(handler.receiverList.get(player.getName()));
			}else{
				player.sendMessage("You dont have any pending transactions!");
			}
		}
		if(cmd.getName().equalsIgnoreCase("npcdeny"))
		{
			if(handler.receiverList.containsKey(player.getName())){
				handler.cancelTransaction((handler.receiverList.get(player.getName())));
			}else{
				player.sendMessage("You dont have any pending transactions!");
			}
		}
		if (cmd.getName().equalsIgnoreCase("NPC") && player != null &&player.hasPermission("npc.edit")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.GREEN + "Specify an NPC type or an NPC option!");
				return false;
			}
//			if(args.length == 3 && args[0].equalsIgnoreCase("token"))
//			{
//				if(getServer().getPlayer(args[1]) != null && Integer.parseInt(args[2]) > 0)
//				{
//					getServer().getPlayer(args[1]).getInventory().addItem(new ItemStack(Material.,Integer.parseInt(args[2])));
//				}else{
//					getServer().getPlayer(args[1]).getInventory().addItem(new ItemStack(Material.REDSTONE_TORCH_OFF,1));
//				}
//			}
			if(args.length == 1 && args[0].equalsIgnoreCase("save"))
			{
				entities.saveEntities(false);
				prices.save();
				token.save();
				player.sendMessage(ChatColor.GOLD + "Saving NPC's");
				return true;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("load"))
			{
				entities.loadEntities();
				return true;
			}
			if(args.length == 3 && args[0].equalsIgnoreCase("newprice"))
			{
				double price = prices.createPrice(Material.getMaterial(Integer.parseInt(args[1])), Double.parseDouble(args[2]));
				player.sendMessage(ChatColor.GOLD + "Changed price of " + Material.getMaterial(Integer.parseInt(args[1])) + " from " + price +" to " +Double.parseDouble(args[2]));
				return true;
			}
			if(args.length == 3 && args[0].equalsIgnoreCase("addtoken"))
			{
				token.addTokens(args[1], Integer.parseInt(args[2]));
				return true;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("print"))
			{
				prices.getPrices();
				return true;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("help"))
			{
				player.sendMessage(commandList);
				return true;
			}
			if(args.length == 2 && args[0].equalsIgnoreCase("kill"))
			{
				List<World> worlds = getServer().getWorlds();
				for (Iterator<World> worldIter = worlds.iterator(); worldIter.hasNext();){
					entities.removeEntity("" + args[1],worldIter.next().getName());
				}
				return true;
			}
			if(args.length == 4 && args[0].equalsIgnoreCase("token") && args[1].equalsIgnoreCase("add"))
			{
				token.addTokens(args[2], Integer.parseInt(args[3]));
				player.sendMessage(ChatColor.GOLD + "You added " +  Integer.parseInt(args[3]) + " token(s) to " + args[2]+ " account");
				return true;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("list"))
			{
				player.sendMessage(ChatColor.RED + "" + entities.list.size());
				for(int x = 0; x < entities.list.size(); x++)
				{
					player.sendMessage(ChatColor.RED + "" + entities.list.get(x).getVillager().getCustomName());
				}
				return true;
			}
			if(args.length == 3 && args[0].equalsIgnoreCase("essentials")){
				Villager entity = (Villager) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
				try{
					entity.setCustomName(ChatColor.GREEN + args[1]);
				}catch(Exception e){
					entity.setCustomName( "BOB");
				}
				if(Integer.parseInt(args[2]) == 0){
					entity.setProfession(Profession.FARMER);
				}
				if(Integer.parseInt(args[2]) == 1){
					entity.setProfession(Profession.LIBRARIAN);
				}
				if(Integer.parseInt(args[2]) == 2){
					entity.setProfession(Profession.PRIEST);
				}
				if(Integer.parseInt(args[2]) == 3){
					entity.setProfession(Profession.BLACKSMITH);
				}
				if(Integer.parseInt(args[2]) == 4){
					entity.setProfession(Profession.BUTCHER);
				}
				entity.setCustomNameVisible(true);
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,30));
				entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
				entity.setAge(Integer.MAX_VALUE);
				entity.setAgeLock(true);
				entities.addEntity(entity, false);
				return true;
				
			}
			else if(args.length == 3 && args[0].equalsIgnoreCase("kit")){
				Villager entity = (Villager) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
				try{
					entity.setCustomName(ChatColor.GREEN + args[1]);
				}catch(Exception e){
					entity.setCustomName("BOB");
				}
				if(Integer.parseInt(args[2]) == 0){
					entity.setProfession(Profession.FARMER);
				}
				if(Integer.parseInt(args[2]) == 1){
					entity.setProfession(Profession.LIBRARIAN);
				}
				if(Integer.parseInt(args[2]) == 2){
					entity.setProfession(Profession.PRIEST);
				}
				if(Integer.parseInt(args[2]) == 3){
					entity.setProfession(Profession.BLACKSMITH);
				}
				if(Integer.parseInt(args[2]) == 4){
					entity.setProfession(Profession.BUTCHER);
				}
				entity.setCustomNameVisible(true);
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,30));
				entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
				entities.addEntity(entity, true);
				return true;
			}
			else if(args[0].equalsIgnoreCase("edit"))
			{
				list.addPlayer(sender.getName());
				return true;
			}
			if(args.length == 2 && args[0].equalsIgnoreCase("token"))
			{
				player.sendMessage(ChatColor.GOLD + "" + args[1] + " has: " + token.getNumTokens(args[1]) + " token(s)");
				return true;
			}
		}
			if(cmd.getName().equalsIgnoreCase("npc"))
			{
				if(args.length == 4 && args[0].equalsIgnoreCase("token") && args[1].equalsIgnoreCase("add") && !(sender instanceof Player))
				{
					token.addTokens(args[2], Integer.parseInt(args[3]));
					sender.sendMessage("You gave " + args[2] + " " + Integer.parseInt(args[3]) + " token(s)!");
					return true;
				}
				
			}
		
		return false;
		}
	
}
