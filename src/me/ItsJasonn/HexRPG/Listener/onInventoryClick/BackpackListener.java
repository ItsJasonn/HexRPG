package me.ItsJasonn.HexRPG.Listener.onInventoryClick;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Instances.HexRPGBackpack;
import me.ItsJasonn.HexRPG.Main.Plugin;

public class BackpackListener implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(!event.getInventory().getTitle().equals(Plugin.getCore().getLangTools().getUncoloredMessage("backpacks.translation.backpack"))) {
			return;
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
			public void run() {
				HexRPGBackpack backpack = new HexRPGBackpack(player);
				
				for(int i=0;i<event.getView().getTopInventory().getSize();i++) {
					ItemStack is = new ItemStack(Material.AIR);
					if(event.getView().getTopInventory().getItem(i) != null && event.getView().getTopInventory().getItem(i).getType() != Material.AIR) {
						is = event.getView().getTopInventory().getItem(i);
					}
					backpack.setItem(is, i);
				}
			}
		}, 1);
	}
}
