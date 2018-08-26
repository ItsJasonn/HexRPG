package me.ItsJasonn.HexRPG.Listener;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;
import me.ItsJasonn.HexRPG.Tools.SQLManager;
import me.ItsJasonn.HexRPG.Tools.StartingKit;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class PlayerJoin implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
			Plugin.getCore().getStatsManager().resetPlayerStats(player);
		}
		
		PlayerLevel level = new PlayerLevel(player);
		if(!level.hasLevel()) {
			level.setLevel(Plugin.getCore().getConfig().getInt("leveling.starting-level"));
			level.setExp(Plugin.getCore().getConfig().getInt("leveling.starting-exp"));
		}
		
		if(Plugin.getCore().getConfig().getBoolean("join.disable-join-message")) {
			event.setJoinMessage(null);
		}
		
		if(SQLManager.using()) {
			SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			date.setTimeZone(TimeZone.getDefault());
			
			if(!Plugin.getCore().getSQLManager().hasData("stats", player.getUniqueId().toString())) {
				Plugin.getCore().getSQLManager().createColumn("stats", player, new String[] {"kills", "deaths", "lastLogin", "firstLogin"},  new Object[] {0, 0, "01-01-2000 00:00", "01-01-2000 00:00"});
			}
			
			if(!player.hasPlayedBefore()) {
				try {
					Plugin.getCore().getSQLManager().updateCell("stats", player, "firstLogin", date.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			try {
				Plugin.getCore().getSQLManager().updateCell("stats", player, "lastLogin", date.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Plugin.getCore().getStatsManager().generateStatsData(player);
		
		SubConfig subConfig = new SubConfig(SubConfig.TYPES.STATS);
		YamlConfiguration statsConfig = subConfig.getConfig();
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.required-class")) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					if(!statsConfig.getString(player.getUniqueId().toString() + ".CLASS").equalsIgnoreCase("NONE")) {
						return;
					}
					
					Plugin.getCore().openMenu(player, "CLASS");
				}
			}, 1);
		} else if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.required-race")) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					if(!statsConfig.getString(player.getUniqueId().toString() + ".RACE").equalsIgnoreCase("NONE")) {
						return;
					}
					
					Plugin.getCore().openMenu(player, "RACE");
				}
			}, 1);
		}
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.starting-kit")) {
			if(!player.hasPlayedBefore()) {
				StartingKit kit = new StartingKit(player);
				kit.give();
			}
		}
	}
}
