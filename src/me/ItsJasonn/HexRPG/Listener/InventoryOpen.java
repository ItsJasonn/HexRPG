package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import me.ItsJasonn.HexRPG.Main.Plugin;

public class InventoryOpen implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryOpen(final InventoryOpenEvent event) {
		final Player player = (Player) event.getPlayer();
		
		if(event.getInventory().getName().endsWith("Shop")) {
			String[] spaceSplitter = ChatColor.stripColor(event.getInventory().getName()).split(" ");
			final OfflinePlayer owner = Bukkit.getServer().getOfflinePlayer(spaceSplitter[0]);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					if(player.getOpenInventory() == event.getInventory()) {
						InventoryClick.OpenShop(owner, player);
					}
				}
			}, 20L);
		}
	}
}
