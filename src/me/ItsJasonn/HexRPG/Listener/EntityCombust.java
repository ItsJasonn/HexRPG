package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class EntityCombust implements Listener {
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		if(!Plugin.getCore().getConfig().getBoolean("world.burn-hostiles")) {
			event.setCancelled(true);
		}
	}
}
