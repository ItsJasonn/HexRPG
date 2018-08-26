package me.ItsJasonn.HexRPG.Listener.onInventoryClick;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class ToolsWindow implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(event.getInventory().getTitle().equals(Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.tools"))) {
			event.setCancelled(true);
			
			ItemStack clickedItem = event.getCurrentItem();
			if(clickedItem == null || clickedItem.getType().equals(Material.AIR) || !clickedItem.hasItemMeta()) {
				return;
			}
			
			ItemMeta meta = clickedItem.getItemMeta();
			meta.setLore(null);
			clickedItem.setItemMeta(meta);
			
			player.getInventory().addItem(clickedItem);
			player.closeInventory();
		}
	}
}