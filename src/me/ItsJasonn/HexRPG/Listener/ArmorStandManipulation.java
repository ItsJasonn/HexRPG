package me.ItsJasonn.HexRPG.Listener;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import me.ItsJasonn.HexRPG.Main.Plugin;

public class ArmorStandManipulation implements Listener {
	@EventHandler
	public void onArmorStandManipulation(PlayerArmorStandManipulateEvent event) {
		ArrayList<String> hologramNames = new ArrayList<String>();
		for(String keys : Plugin.getCore().getConfig().getConfigurationSection("npc.holograms").getKeys(false)) {
			hologramNames.add(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("npc.holograms." + keys)));
		}
		
		for(String hologramName : hologramNames) {
			if(event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().equals(hologramName)) {
				event.setCancelled(true);
			}
		}
	}
}