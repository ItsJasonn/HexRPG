package me.ItsJasonn.HexRPG.Tools;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class UUIDTools {
	public static Player getPlayerByName(String name) {
		for(Player pLoop : Bukkit.getServer().getOnlinePlayers()) {
			if(pLoop.getName().equalsIgnoreCase(name)) {
				return pLoop;
			}
		}
		return null;
	}
	
	public static OfflinePlayer getOfflinePlayerByName(String name) {
		for(OfflinePlayer pLoop : Bukkit.getServer().getOfflinePlayers()) {
			if(pLoop.getName().equalsIgnoreCase(name)) {
				return pLoop;
			}
		}
		return null;
	}
}
