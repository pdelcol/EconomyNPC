package com.resolutiongaming.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.resolutiongaming.pete.EconomyNPC;

public class PlayerEditList {
	ArrayList<String> players = new ArrayList<String>();
	EconomyNPC plugin;
	public PlayerEditList(EconomyNPC plugin)
	{
		this.plugin = plugin;
	}
	public void addPlayer(String playerName)
	{
		
		for(int x = 0; x < players.size(); x++)
		{
			
			if(players.get(x).equalsIgnoreCase(playerName))
			{
				players.remove(x);
				plugin.getServer().getPlayer(playerName).sendMessage(ChatColor.GOLD + "You are no longer editing NPC's");
				return;
			}
				
		}
		if(checkPlayer(playerName))
		{
			players.add(playerName);
			plugin.getServer().getPlayer(playerName).sendMessage(ChatColor.GOLD + "You are now editing NPC's");

		}
	}
		
	
	public void removePlayer(String playerName)
	{
		for(int x = 0; x < players.size(); x++)
		{
			if(players.get(x).equalsIgnoreCase(playerName))
			{
				players.remove(x);
				plugin.getServer().getPlayer(playerName).sendMessage(ChatColor.GOLD + "You are no longer editing NPC's");
			}
		}
	}
	public boolean checkPlayer(String playerName)
	{
		Player player = plugin.getServer().getPlayer(playerName);
		if(player.hasPermission("npc.edit")) {
			return true;
		}else{
			player.sendMessage(ChatColor.RED + "You cannot do that");
			return false;
		}
	}
	public boolean isInList(String playerName)
	{		
		for(int x = 0; x < players.size(); x++)
		{
			if(players.get(x).equalsIgnoreCase(playerName))
			{
				return true;
			}
		}
		return false;
	}
}
