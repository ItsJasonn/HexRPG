package me.ItsJasonn.HexRPG.Listener.onInventoryClick;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StatsWindow implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(event.getInventory().getTitle().equals(player.getName() + "'s stats")) {
			event.setCancelled(true);
		}
	}
}
