package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class CustomMobManager {
	private ArrayList<CustomMob> customMobs = new ArrayList<CustomMob>();
	private HashMap<Location, Integer> spawnerCounter = new HashMap<Location, Integer>();
	
	public void startScheduler() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
			public void run() {
				File file = new File(Plugin.getCore().getDataFolder() + "/dat0/", "spawners.yml");
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				for(String keys : config.getKeys(false)) {
					String mobType = config.getString(keys + ".type");
					Location location = new Location(Bukkit.getServer().getWorld(config.getString(keys + ".world")), config.getInt(keys + ".x"), config.getInt(keys + ".y"), config.getInt(keys + ".z"));
					
					CustomMobManager customMobManager = Plugin.getCore().getCustomMobManager();
					
					if(!customMobManager.getSpawnerCounter().containsKey(location)) {
						customMobManager.getSpawnerCounter().put(location, Integer.MAX_VALUE - 1);
					} else {
						customMobManager.getSpawnerCounter().put(location, customMobManager.getSpawnerCounter().get(location) + 1);
						if(customMobManager.getSpawnerCounter().get(location) >= Plugin.getCore().getConfig().getInt("spawners." + mobType + ".delay")) {
							customMobManager.getSpawnerCounter().put(location, 0);
							
							for(int i=0;i<Plugin.getCore().getConfig().getInt("spawners." + mobType + ".amount");i++) {
								Random random = new Random();
								Entity entity = location.getWorld().spawnEntity(location.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1), EntityType.valueOf(config.getString(keys + ".entity-type")));
								
								entity.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + WordUtils.capitalizeFully(mobType.replace("_", " ")));
								if(config.isString(keys + ".name")) {
									entity.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + WordUtils.capitalizeFully(config.getString(keys + ".name").replace("_", " ")));
								}
								entity.setCustomNameVisible(true);
								
								CustomMob customMob = null;
								
								CustomMobType mobTypeEnum = CustomMobType.valueOf(mobType.toUpperCase().replace(" ", "_"));
								if(mobTypeEnum == CustomMobType.NECROMANCER) {
									customMob = new NecroMancer(entity);
								} else if(mobTypeEnum == CustomMobType.WITCH) {
									customMob = new Witch(entity);
									Bukkit.getServer().getPluginManager().registerEvents((Witch) customMob, Plugin.getCore());
								} else if(mobTypeEnum == CustomMobType.CASTER) {
									customMob = new Caster(entity);
									Bukkit.getServer().getPluginManager().registerEvents((Caster) customMob, Plugin.getCore());
								} else if(mobTypeEnum == CustomMobType.GHOST_MOB) {
									customMob = new GhostMob(entity);
									Bukkit.getServer().getPluginManager().registerEvents((GhostMob) customMob, Plugin.getCore());
								} else if(mobTypeEnum == CustomMobType.BAT) {
									customMob = new Bat(entity);
									Bukkit.getServer().getPluginManager().registerEvents((Bat) customMob, Plugin.getCore());
								} else if(mobTypeEnum == CustomMobType.WISP) {
									customMob = new Wisp(entity);
								} else if(mobTypeEnum == CustomMobType.WRAITH) {
									customMob = new Wraith(entity);
								}
								
								int tier = config.getInt(keys + ".tier");
								boolean elite = config.getBoolean(keys + ".elite");
								if(tier == 1) {
									if(!elite) {
										customMob.setDamage(1, 50);
										customMob.setMaxHealth(random.nextInt(6000) + 2000);
										customMob.setHealth(customMob.getMaxHealth());
									} else {
										customMob.setDamage(50, 75);
										customMob.setMaxHealth(random.nextInt(4000) + 8000);
										customMob.setHealth(customMob.getMaxHealth());
									}
								} else if(tier == 2) {
									if(!elite) {
										customMob.setDamage(75, 150);
										customMob.setMaxHealth(random.nextInt(30000) + 10000);
										customMob.setHealth(customMob.getMaxHealth());
									} else {
										customMob.setDamage(150, 200);
										customMob.setMaxHealth(random.nextInt(40000) + 40000);
										customMob.setHealth(customMob.getMaxHealth());
									}
								}
								
								customMob.apply();
								customMob.setType(mobTypeEnum);
								
								customMob.executeDefault();
							}
						}
					}
				}
			}
		}, 0, 20);
	}
	
	public ArrayList<CustomMob> getCustomMobs() {
		return this.customMobs;
	}
	
	public HashMap<Location, Integer> getSpawnerCounter() {
		return this.spawnerCounter;
	}
}