package me.ItsJasonn.HexRPG.Instances.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tr7zw.itemnbtapi.NBTItem;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public abstract class Ability {
	public double damage, stunDuration, poisonDamage, poisonDuration, movementSpeedDuration;
	public float movementSpeed;
	public boolean removeStuns;
	
	public abstract void execute();
	
	public Ability(double damage, double stunDuration, double poisonDamage, double poisonDuration, float movementSpeed, double movementSpeedDuration, boolean removeStuns) {
		this.damage = damage;
		this.stunDuration = stunDuration;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.movementSpeed = movementSpeed;
		this.movementSpeedDuration = movementSpeedDuration;
		this.removeStuns = removeStuns;
	}
	
	private static HashMap<UUID, Integer> cooldownTimer = new HashMap<UUID, Integer>();
	private static HashMap<UUID, Integer> stunnedTimer = new HashMap<UUID, Integer>();
	private static HashMap<UUID, Integer> silencedTimer = new HashMap<UUID, Integer>();
	
	public static ArrayList<UUID> getStunnedPlayers() {
		return new ArrayList<>(stunnedTimer.keySet());
	}
	
	public static ArrayList<UUID> getSilencedPlayers() {
		return new ArrayList<>(silencedTimer.keySet());
	}
	
	public static ArrayList<UUID> getCooldownTimer() {
		return new ArrayList<>(cooldownTimer.keySet());
	}
	
	public static void setCooldown(UUID uuid, boolean cooldown, int cooldownTime) {
		if(cooldown) {
			cooldownTimer.put(uuid, cooldownTime * 20);
		} else {
			cooldownTimer.remove(uuid);
		}
	}
	
	public static void setStunned(UUID uuid, boolean stunned, double time) {
		if(stunned) {
			if(Bukkit.getServer().getOfflinePlayer(uuid) != null && Bukkit.getServer().getOfflinePlayer(uuid).isOnline()) {
				Player player = Bukkit.getServer().getPlayer(uuid);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (time * 20), 0));
			}
			
			stunnedTimer.put(uuid, (int) (time * 20));
		} else {
			stunnedTimer.remove(uuid);
		}
	}
	
	public static void setSilenced(UUID uuid, boolean silenced, double time) {
		if(silenced) {
			if(Bukkit.getServer().getOfflinePlayer(uuid) != null && Bukkit.getServer().getOfflinePlayer(uuid).isOnline()) {
				Player player = Bukkit.getServer().getPlayer(uuid);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (time < 1 ? time * 20 : time * 20 / 4), 0));
			}
			
			silencedTimer.put(uuid, (int) (time * 20));
		} else {
			silencedTimer.remove(uuid);
		}
	}
	
	public static void setCooldown(UUID uuid, boolean cooldown) {
		setCooldown(uuid, cooldown, 0);
	}
	
	public static void setStunned(UUID uuid, boolean stunned) {
		setStunned(uuid, stunned, 0);
	}
	
	public static void setSilenced(UUID uuid, boolean silenced) {
		setSilenced(uuid, silenced, 0);
	}
	
	public static boolean hasCooldown(UUID uuid) {
		return cooldownTimer.containsKey(uuid);
	}
	
	public static String getAbility(ItemStack weapon) {
		NBTItem nbtItem = new NBTItem(weapon);
		if(!nbtItem.hasKey("hexAbility_a")) {
			return "";
		}
		
		return nbtItem.getString("hexAbility_a");
	}
	
	public static void startScheduler() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
			public void run() {
				if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.abilities")) {
					for(UUID uuid : new ArrayList<>(cooldownTimer.keySet())) {
						cooldownTimer.put(uuid, cooldownTimer.get(uuid) - 1);
						if(cooldownTimer.get(uuid) <= 0) {
							setCooldown(uuid, false);
							
							OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);
							if(offlinePlayer != null && offlinePlayer.isOnline()) {
								Player player = offlinePlayer.getPlayer();
								
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("abilities.cooldown-over"));
							}
						}
					}
					
					for(UUID uuid : new ArrayList<>(stunnedTimer.keySet())) {
						stunnedTimer.put(uuid, stunnedTimer.get(uuid) - 1);
						if(stunnedTimer.get(uuid) <= 0) {
							setStunned(uuid, false);
						}
					}
					
					for(UUID uuid : new ArrayList<>(silencedTimer.keySet())) {
						silencedTimer.put(uuid, silencedTimer.get(uuid) - 1);
						if(silencedTimer.get(uuid) <= 0) {
							setSilenced(uuid, false);
						}
					}
				}
			}
		}, 0, 1);
	}
}