package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class EntityPickupItem implements Listener {
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if(Plugin.getCore().inTrade.containsKey(player)) {
				event.setCancelled(true);
			}
			
			if(event.getItem().getItemStack() != null) {
				ItemStack item = event.getItem().getItemStack();
				if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Rupee")) {
					if(Plugin.getCore().getConfig().getBoolean("inventory.show-message-on-rupee-pickup")) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("inventory.rupee-pickup-message").replace("{AMOUNT}", "" + item.getAmount())));
						
						if(Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
							event.setCancelled(true);
							event.getItem().remove();
							
							Plugin.getCore().getEconomy().depositPlayer(player, item.getAmount());
						}
					}
					
					if(Plugin.getCore().getConfig().getBoolean("inventory.rupee-pickup-sound")) {
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 2);
					}
				}
			}
		}
	}
}
