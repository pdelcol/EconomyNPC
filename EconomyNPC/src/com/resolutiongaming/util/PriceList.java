package com.resolutiongaming.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.yaml.snakeyaml.Yaml;

import com.resolutiongaming.pete.EconomyNPC;

public class PriceList {
	ArrayList<PriceClass> list = new ArrayList<PriceClass>();
	EconomyNPC plugin;
	String path = "plugins/EconomyNPC/prices.yml";

	public PriceList(EconomyNPC plugin) {
		this.plugin = plugin;
	}

	public void load() {
		InputStream input;
		try {
			input = new FileInputStream(new File(path));
			list = new ArrayList<PriceClass>();
			Yaml yaml = new Yaml();
			for (Object data : yaml.loadAll(input)) {
				// System.out.println(data);
				Map<String, Object> priceList = (Map<String, Object>) data;
				if (!priceList.isEmpty()) {
					
					Set<String> listOfName = priceList.keySet();
					Object[] names = listOfName.toArray(new Object[priceList.size()]);
					for(int x = 0; x < names.length; x++){
						if(Material.getMaterial((String) names[x])!=null){
							PriceClass price = new PriceClass(Material.getMaterial((String) names[x]));
							price.setPrice((double) priceList.get(names[x]));
							list.add(price);
						}else{
							plugin.getLogger().warning("Could not load: " + (String) names[x]);
						}
					}
				}
			}
		} catch (Exception e) {
			File file = new File(path);
			// file.mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			BufferedWriter pr = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path)));
			String output = "";
			Map<String, Object> data = new HashMap<String, Object>();
			for (int x = 0; x < list.size(); x++) {
				
				data.put("" + list.get(x).item.getType().name(), list.get(x)
						.getPrice());
				
			}
			Yaml yaml = new Yaml();
			output = yaml.dump(data);
			pr.write(output);
			pr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public double createPrice(Material material, double price)
	{
		for(int x = 0; x < list.size(); x++)
		{
			if((list.get(x).getMaterial() == material))
			{
				double price2 = list.get(x).getPrice();
				list.get(x).setPrice(price);
				return price2;
			}
		}
		PriceClass newPrice = new PriceClass(material);
		newPrice.setPrice(price);
		list.add(newPrice);
		return 0.0;
	}
	public void getPrices()
	{
		for(int x = 0; x < list.size(); x++){
			plugin.getLogger().info(""+list.get(x).getItem().getType().name() + " : " + list.get(x).getPrice());
		}
	}
	public double getPrice(Material material, int numStack)
	{
		for(int x = 0; x < list.size(); x++){
			if(material == list.get(x).getMaterial())
			{
				return list.get(x).price * numStack;
			}
		}
		return -1;
	}
}
