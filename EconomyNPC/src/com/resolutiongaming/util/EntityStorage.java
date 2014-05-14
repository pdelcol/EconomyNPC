package com.resolutiongaming.util;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftAgeable;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPig;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.LivingEntity;

import com.resolutiongaming.pete.EconomyNPC;

public class EntityStorage{
	public ArrayList<TradeEntity> list = new ArrayList<TradeEntity>();
	
	EconomyNPC plugin;
	PriceList prices;
	public EntityStorage(EconomyNPC plugin, PriceList prices)
	{
		this.plugin = plugin;
		this.prices = prices;
		 BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {

				@Override
				public void run() {
					for(TradeEntity e:list)
					{
						e.getVillager().addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,30));
						e.getVillager().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
					}
				}
	        	
	            
	        }, 12000,120);
	}
	
	public void addEntity(Villager villager,boolean isKitEntity)
	{
		list.add(new TradeEntity(villager, isKitEntity));
	}
	public void removeEntity(String villagerName,String world)
	{
		List<Entity> list2 = plugin.getServer().getWorld(world).getEntities();
		for(int x = 0; x < list.size(); x++)
		{
			for (Iterator<Entity> listIter = list2.iterator(); listIter.hasNext();){
				if(list.get(x).getVillager() == listIter.next())
				{
					list.get(x).getVillager().setHealth(0.0);
					list.get(x).getVillager().damage(1.0);
					list.get(x).getVillager().remove();
					list.remove(x);
					return;
				}	
			}
		}
	}
	public Villager getEntity(String name)
	{
		for(int x = 0; x < list.size(); x++)
		{
			if(name.equalsIgnoreCase(list.get(x).getVillager().getCustomName()))
			{
				return list.get(x).getVillager();
			}
		}
		return null;
	}
	public Villager getEntity(Villager entity)
	{
		for(int x = 0; x < list.size(); x++)
		{
			if(entity == list.get(x))
			{
				return list.get(x).getVillager();
			}
		}
		return null;
	}
	public void saveEntities(boolean deleteList)
	{
		try{
			 
			FileWriter fstream = new FileWriter("plugins/EconomyNPC/NPCS/nums.txt");
			BufferedWriter save = new BufferedWriter(fstream);
			for(int x = 0; x < list.size(); x++){
			// Create an ObjectOutputStream to put objects into save file.
				save.write(list.get(x).getVillager().getCustomName());
				save.newLine();
			}
		    save.close();
		    fstream.close();
			for(int x = 0; x < list.size(); x++){
				FileOutputStream fileOut = new FileOutputStream("plugins/EconomyNPC/NPCS/" + list.get(x).getVillager().getCustomName() + ".NPC");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				String inventory = InventoryStringDeSerializer.InventoryToString(list.get(x).inv);
				SerializedTradeEntity entity = new SerializedTradeEntity(list.get(x).getVillager().getLocation().getX(),list.get(x).getVillager().getLocation().getY(),list.get(x).getVillager().getLocation().getZ()
						,list.get(x).getVillager().getLocation().getWorld().getName(),list.get(x).isKitEntity,inventory,list.get(x).getVillager().getCustomName(),list.get(x).getVillager().getProfession(), list.get(x).getVillager().getUniqueId());
				
				out.writeObject(entity);
				out.close();
				fileOut.close();
			}
			if(deleteList)
			{
				list.clear();
			}
//			List<World> worlds = plugin.getServer().getWorlds();
//			for (Iterator<World> worldIter = worlds.iterator(); worldIter.hasNext();){
//				for(int x = 0; x < list.size(); x++)
//				{
//					removeEntity(list.get(x).getVillager().getCustomName().substring(2), worldIter.next().getName());
//				}
//			}
		}catch(Exception e){
			plugin.getLogger().severe("ERROR SAVING FILE! (Is this the first time running this?");
			
			e.printStackTrace();
		}
	}
	public void loadEntities()
	{
		try{
			ArrayList<String> names = new ArrayList<String>();
			
			FileReader fileReader = new FileReader("plugins/EconomyNPC/NPCS/nums.txt");
	            // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = new BufferedReader(fileReader);  
	        String name = "";
	        while((name = bufferedReader.readLine()) != null)
	        {
	        	names.add(name);
	        }
	        
	        bufferedReader.close();	
	        fileReader.close();
	        for(int x = 0; x < names.size(); x++){    
	        	FileInputStream fileIn = new FileInputStream("plugins/EconomyNPC/NPCS/" + names.get(x) +".NPC");
		   		ObjectInputStream in = new ObjectInputStream(fileIn);
		   		SerializedTradeEntity entity = (SerializedTradeEntity)in.readObject();
				Arrow arrow = (Arrow) plugin.getServer().getWorld(entity.worldName).spawnEntity(new Location(plugin.getServer().getWorld(entity.worldName),entity.x,entity.y,entity.z), EntityType.ARROW);
		   		List<Entity> list2 = arrow.getNearbyEntities(entity.x, entity.y, entity.z);
				Villager villager = null;
				TradeEntity tradeEntity = null;
		   		for (int i = 0; i < list2.size(); i++){	
		   			EntityType type = EntityType.VILLAGER;
					if(list2.get(i).getType() == type && list2.get(i) instanceof Villager)
					{
						
						villager = (Villager) list2.get(i);
						if(entity.villagerName.equalsIgnoreCase(villager.getCustomName())){
							boolean exists = false;
							for(int v = 0; v < list.size(); v++){
								if(list.get(v).getVillager().getCustomName().equalsIgnoreCase(villager.getCustomName())){
									exists = true;
								}
								
							}
							if(exists)
							{
								Villager villager1 = (Villager)villager;
								
						   		villager1.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,12000,9001));
								tradeEntity = new TradeEntity((Villager)villager1,entity.isKitEntity);
						   		tradeEntity.inv = InventoryStringDeSerializer.StringToInventory(entity.inv,villager1.getCustomName());
							}
//							if(!exists)
//							{
//								Villager villager1 = (Villager) arrow.getLocation().getWorld().spawnEntity(arrow.getLocation(), EntityType.VILLAGER);
//								
//								villager1.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,12000,9001));
//								tradeEntity = new TradeEntity((Villager)villager1,entity.isKitEntity);
//						   		tradeEntity.inv = InventoryStringDeSerializer.StringToInventory(entity.inv,villager1.getCustomName());
//							}
							//exists = false;
						}
						
					}
					
		   		}
		   		
		   		
		   		if(tradeEntity != null)
		   		{
			   		for(int v = 0; v < tradeEntity.inv.getSize()-1; v++){
			   			if(tradeEntity.inv.getItem(v) != null){
					   		if(!(tradeEntity.inv.getItem(v).getType() == Material.AIR) && !(tradeEntity.inv.getItem(v).getType() == null))
							{
								ItemMeta meta = tradeEntity.inv.getItem(v).getItemMeta();
								ArrayList<String> lore = new ArrayList<String>();
								lore.add("Price: "
										+ prices.getPrice(tradeEntity.inv.getItem(v).getType(), tradeEntity.inv
												.getItem(v).getAmount()));
								meta.setLore(lore);
								tradeEntity.inv.getItem(v).setItemMeta(meta);
							}
			   			}
			   		}
			   		list.add(tradeEntity);
		   		}
		   		arrow.remove();
		    	in.close();
		    	fileIn.close();
	        }
		}catch(Exception e){
			plugin.getLogger().severe("ERROR LOADING FILE! (Is this the first time running this?");
			File dir = new File("plugins/EconomyNPC/NPCS");
			if(!dir.exists())
			{
				dir.mkdir();
			}
			e.printStackTrace();
		}
	}
}
