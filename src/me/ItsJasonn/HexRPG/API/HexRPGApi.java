package me.ItsJasonn.HexRPG.API;

import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;
import me.ItsJasonn.HexRPG.Tools.StatsManager;

public class HexRPGApi {
	public static StatsManager getStatsManager() {
		return Plugin.getCore().getStatsManager();
	}
	
	public static PlayerLevel getPlayerLevel(Player player) {
		return new PlayerLevel(player);
	}
	
	public static HexRPGApiCustomMobs getCustomMobsApi() {
		return new HexRPGApiCustomMobs();
	}
}