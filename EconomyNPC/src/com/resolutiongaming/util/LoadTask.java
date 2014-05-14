package com.resolutiongaming.util;

import org.bukkit.scheduler.BukkitRunnable;

public class LoadTask extends BukkitRunnable{
	EntityStorage entities;
	public LoadTask(EntityStorage entities)
	{
		this.entities = entities;
	}
	@Override
	public void run() {
		entities.loadEntities();
	}

}
