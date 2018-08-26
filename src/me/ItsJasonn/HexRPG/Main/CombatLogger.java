package me.ItsJasonn.HexRPG.Main;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class CombatLogger {
	private static HashMap<UUID, Integer> combatTimer = new HashMap<UUID, Integer>();
	private static HashMap<Entity, Integer> hologramTimer = new HashMap<Entity, Integer>();
	
	public static void startScheduler() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.combat-log")) {
					for(UUID uuid : ((HashMap<UUID, Integer>) combatTimer.clone()).keySet()) {
						OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
						
						combatTimer.put(uuid, combatTimer.get(uuid) - 1);
						if(combatTimer.get(uuid) <= 0) {
							removeOutOfCombat(player);
						}
					}
				}
				
				if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.damage-indicator")) {
					for(Entity hologram : ((HashMap<Entity, Integer>) hologramTimer.clone()).keySet()) {
						hologramTimer.put(hologram, hologramTimer.get(hologram) - 1);
						if(hologramTimer.get(hologram) <= 0) {
							unregisterDamageHologram(hologram);
						}
					}	
				}
			}
		}, 0, 1);
	}
	
	public static void putInCombat(Player player) {
		if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.combat-log")) {
			return;
		}
		
		if(!combatTimer.containsKey(player.getUniqueId())) {
			player.sendMessage(Plugin.getCore().getLangTools().getMessage("combat.in-combat"));
		}
		
		combatTimer.put(player.getUniqueId(), Plugin.getCore().getConfig().getInt("combat.combat-time") * 20);
	}
	
	private static void removeOutOfCombat(OfflinePlayer player) {
		if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.combat-log")) {
			return;
		}
		
		combatTimer.remove(player.getUniqueId());
		
		if(player != null && player.isOnline()) {
			((Player) player).sendMessage(Plugin.getCore().getLangTools().getMessage("combat.out-combat"));
		}
	}
	
	public static void registerDamageHologram(Entity hologram) {
		if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.damage-indicator")) {
			return;
		}
		
		hologramTimer.put(hologram, 7);
	}
	
	private static void unregisterDamageHologram(Entity hologram) {
		if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.damage-indicator")) {
			return;
		}
		
		hologramTimer.remove(hologram);
		hologram.remove();
	}
}