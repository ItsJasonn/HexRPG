package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class ItemDamage implements Listener {
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		ItemStack item = event.getItem();
		
		if(Plugin.getCore().getStatsManager().isCustomItem(item)) {
			event.setCancelled(true);
		}
	}
}