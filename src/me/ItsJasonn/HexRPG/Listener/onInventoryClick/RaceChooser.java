package me.ItsJasonn.HexRPG.Listener.onInventoryClick;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class RaceChooser implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(event.getInventory().getTitle().equals(Plugin.getCore().getConfig().getString("races.menu.title"))) {
			event.setCancelled(true);
			
			File statsFile = new SubConfig(SubConfig.TYPES.PLAYERSTATS).getFile();
			YamlConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);
			
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				if(!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
					return;
				}
				
				String className = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).toUpperCase().replace(" ", "_");
				
				statsConfig.set(player.getUniqueId().toString() + ".RACE", className);
				try {
					statsConfig.save(statsFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				player.closeInventory();
			}
		}
	}
}