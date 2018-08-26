package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class ItemCraft implements Listener {
	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		ItemStack copy = event.getCurrentItem().clone();
		copy.setItemMeta(null);
		
		if (Plugin.getCore().getStatsManager().isCustomItem(copy)) {
			Plugin.getCore().getStatsManager().setUnidentified(event.getCurrentItem());
		}
	}
}
