package me.ItsJasonn.HexRPG.Listener.onEntityTame;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import de.tr7zw.itemnbtapi.NBTEntity;

public class PetStats implements Listener {
	@EventHandler
	public void onEntityTame(EntityTameEvent event) {
		Entity e = event.getEntity();
		
		if(e instanceof Wolf || e instanceof Horse) {
			NBTEntity nbtEntity = new NBTEntity(e);
			nbtEntity.setBoolean("hexPet", true);
		}
	}
}