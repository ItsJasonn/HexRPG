package me.ItsJasonn.HexRPG.Listener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class InventoryClose implements Listener {
	public static HashMap<Player, String> _invClose = new HashMap<Player, String>();
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();

		SubConfig subConfig = new SubConfig(SubConfig.TYPES.PLAYERSTATS);
		YamlConfiguration statsConfig = subConfig.getConfig();

		if(event.getInventory().getName().startsWith("Trading: ")) {
			String[] playersInTitle = ChatColor.stripColor(event.getInventory().getTitle().replace("Trading: ", "")).split(" - ");
			Player leftPlayer = Bukkit.getServer().getPlayer(playersInTitle[0]);
			Player rightPlayer = Bukkit.getServer().getPlayer(playersInTitle[1]);
			
			int[] leftSlots = {1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39};
			int[] rightSlots = {5, 6, 7, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44};
			
			if(!_invClose.containsKey(player)) {
				if(player.equals(leftPlayer)) {
					for(int slots : leftSlots) {
						if(event.getInventory().getItem(slots) == null || event.getInventory().getItem(slots).getType() == Material.AIR) {
							continue;
						}
						leftPlayer.getInventory().addItem(event.getInventory().getItem(slots));
					}
				}
				
				if(player.equals(rightPlayer)) {
					for(int slots : rightSlots) {
						if(event.getInventory().getItem(slots) == null || event.getInventory().getItem(slots).getType() == Material.AIR) {
							continue;
						}
						rightPlayer.getInventory().addItem(event.getInventory().getItem(slots));
					}
				}
			} else {
				String val = _invClose.get(player);
				
				if(val.equalsIgnoreCase("TRADE_CONFIRM")) {
					// Event is being handled from somewhere else
				}
				
				_invClose.remove(player);
			}
			
			Plugin.getCore().inTrade.remove(leftPlayer);
			Plugin.getCore().inTrade.remove(rightPlayer);
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					if(leftPlayer.getOpenInventory() != null) leftPlayer.closeInventory();
					if(rightPlayer.getOpenInventory() != null) rightPlayer.closeInventory();
				}
			}, 1);
		} else if(event.getInventory().getName().endsWith("Bank")) {
			File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
			YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);

			for (int i = 0; i < 8; i++) {
				bankConfig.set(player.getUniqueId().toString() + "." + Integer.toString(i), event.getInventory().getItem(i));
			}
			try {
				bankConfig.save(bankFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("classes.menu.title"))) && new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.required-class")) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					if(statsConfig.getString(player.getUniqueId().toString() + ".CLASS").equalsIgnoreCase("NONE")) {
						player.openInventory(event.getInventory());
						return;
					}

					if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.required-race")) {
						Plugin.getCore().openMenu(player, "RACE");
					}
				}
			}, 1);
		} else if(event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("races.menu.title")))) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					if(statsConfig.getString(player.getUniqueId().toString() + ".RACE").equalsIgnoreCase("NONE")) {
						player.openInventory(event.getInventory());
					}
				}
			}, 1);
		}
	}
}
