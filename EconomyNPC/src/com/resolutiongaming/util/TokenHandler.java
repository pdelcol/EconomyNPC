package com.resolutiongaming.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class TokenHandler {
	public Map<String, Integer> tokens = new HashMap<String, Integer>();
	//Constructor
	public TokenHandler()
	{
		//We dont need anything here for right now
	}
	//Load the tokens
	public void load()
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("plugins/EconomyNPC/Token_List.bin"));
			Object result = ois.readObject();
			tokens = (Map<String, Integer>)result;
			ois.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//Save the tokens
	public void save()
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("plugins/EconomyNPC/Token_List.bin"));
			oos.writeObject(tokens);
			oos.flush();
			oos.close();
			//Handle I/O exceptions
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//Check to see if the player is in the token list
	public void checkName(String playerName)
	{
		if(!tokens.containsKey(playerName))
		{
			tokens.put(playerName, 0);
		}
	}
	//Get the number of tokens for a given player
	public int getNumTokens(String playerName)
	{
		if(tokens.get(playerName) == null)
		{
			tokens.put(playerName, 0);
		}
		return tokens.get(playerName);
	}
	//Add tokens to a given players account
	public void addTokens(String playerName, int numTokens)
	{
		if(tokens.containsKey(playerName))
		{
			tokens.put(playerName, tokens.get(playerName) + numTokens);
		}
	}
	//Remove tokens from a given players account
	public boolean removeTokens(String playerName, int numTokens)
	{
		if(tokens.containsKey(playerName))
		{
			if(tokens.get(playerName) >= numTokens){
				tokens.put(playerName, tokens.get(playerName) - numTokens);
				return true;
			}
			return false;
		}
		return false;
	}
}
