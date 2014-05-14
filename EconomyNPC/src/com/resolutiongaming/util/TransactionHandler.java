package com.resolutiongaming.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;

import com.resolutiongaming.pete.EconomyNPC;

public class TransactionHandler {
	HashMap<Integer, Transaction> transactionList = new HashMap<Integer, Transaction>();
	ArrayList<Integer> logonList = new ArrayList<Integer>();
	public ArrayList<Transaction> logList = new ArrayList<Transaction>();
	public HashMap<String, Integer> receiverList = new HashMap<String, Integer>();
	Random rand = new Random();
	EconomyNPC plugin;
	TokenHandler tokens;
	public TransactionHandler(EconomyNPC plugin, TokenHandler tokens)
	{
		this.plugin = plugin;
		this.tokens = tokens;
	}
	public void addTransaction(String senderName, String receiverName, int numTokens)
	{
		int key = rand.nextInt();
		Transaction value = new Transaction(senderName, receiverName, numTokens, true, key);
		transactionList.put(key, value);
		receiverList.put(receiverName, key);
		informTransaction(senderName, receiverName, numTokens, key);
	}
	public void informTransaction(String senderName, String receiverName, int numTokens, int key)
	{
		plugin.getServer().getPlayer(senderName).sendMessage(ChatColor.BLUE + "Sent " + numTokens + " tokens to " + receiverName);
		if(plugin.getServer().getPlayer(receiverName).isOnline())
		{
			plugin.getServer().getPlayer(senderName).sendMessage(ChatColor.BLUE + "You are getting " + numTokens + " token(s) from " + senderName);
			plugin.getServer().getPlayer(senderName).sendMessage(ChatColor.BLUE + "Type /npcaccept to accept the tokens");
			plugin.getServer().getPlayer(senderName).sendMessage(ChatColor.BLUE + "Type /npcdeny to accept the tokens");
		}else{
			logonList.add(key);
		}
	}
	public void onLogon(String player)
	{
		
	}
	public void processTransaction(int transactionID)
	{
		
		Transaction transaction = transactionList.get(transactionID);
		if(transaction.inProgress){
			if(tokens.removeTokens(transaction.senderName, transaction.numTokens)){
				tokens.addTokens(transaction.receiverName, transaction.numTokens);
			}else{
				plugin.getServer().getPlayer(transaction.senderName).sendMessage(ChatColor.RED + "Error sending tokens (Do you have enough?)");
				plugin.getServer().getPlayer(transaction.receiverName).sendMessage(ChatColor.RED + "Error receiving tokens");
			}
			receiverList.remove(transaction.transactionID);
			logTransaction(transaction);
		}else{
			
		}
		transactionList.put(transaction.transactionID, transaction);
	}
	public void cancelTransaction(Transaction transaction)
	{
		if(transaction.inProgress)
		{
			transaction.inProgress = false;
			transactionList.remove(transaction.transactionID);
			transaction.cancelled = true;
			logList.add(transaction);
		}
	}
	public void cancelTransaction(int transactionID)
	{
		Transaction transaction = transactionList.get(transactionID);
		if(transaction.inProgress)
		{
			transaction.inProgress = false;
			transactionList.remove(transaction.transactionID);
			transaction.cancelled = true;
			logList.add(transaction);
		}
	}
	public Transaction getTransaction(int transactionID)
	{
		return transactionList.get(transactionID);
	}
	public void logTransaction(Transaction transaction)
	{
		
	}
	
}
