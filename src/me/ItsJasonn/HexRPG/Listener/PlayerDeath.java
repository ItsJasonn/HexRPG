package me.ItsJasonn.HexRPG.Listener;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;
import me.ItsJasonn.HexRPG.Tools.SQLManager;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class PlayerDeath implements Listener {
	public static HashMap<Player, Location> deathLocation = new HashMap<Player, Location>();
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
			Plugin.getCore().getStatsManager().resetPlayerStats(player);
		}
		
		if(Plugin.getCore().getConfig().getBoolean("death.disable-death-message")) {
			event.setDeathMessage(null);
		}
		
		if(Plugin.getCore().inDual.containsKey(player)) {
			Player challenger = Plugin.getCore().inDual.get(player);
			
			player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.dueling.duel.lost").replace("%enemy%", challenger.getName()));
			challenger.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.dueling.duel.won").replace("%enemy%", player.getName()));
			
			Plugin.getCore().inDual.remove(player);
			Plugin.getCore().inDual.remove(challenger);
			
			Location loc = player.getLocation();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					player.updateInventory();
					player.teleport(loc);
				}
			}, 1);
			
			player.spigot().respawn();
		} else {
			if(Plugin.getCore().getConfig().getBoolean("death.clear-drops")) {
				event.getDrops().clear();
			}
			
			if (player.getKiller() != null) {
				Random random = new Random();
				
				int rSkillExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min");
				PlayerLevel level = new PlayerLevel(player);
				level.setSkillExp("HITPOINTS", level.getSkillExp("HITPOINTS") + rSkillExp);
			}
		}
		
		if(SQLManager.using()) {
			try {
				int deaths = Integer.parseInt((String) Plugin.getCore().getSQLManager().getDataByField("stats", "uuid", player.getUniqueId().toString(), "deaths"));
				deaths += 1;
				
				Plugin.getCore().getSQLManager().updateCell("stats", player, "deaths", Integer.toString(deaths));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			if(player.getKiller() != null && player.getKiller() instanceof Player) {
				Player killer = (Player) player.getKiller();
				
				try {
					int kills = Integer.parseInt((String) Plugin.getCore().getSQLManager().getDataByField("stats", "uuid", killer.getUniqueId().toString(), "kills"));
					kills += 1;
					
					Plugin.getCore().getSQLManager().updateCell("stats", killer, "kills", Integer.toString(kills));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
