package me.ItsJasonn.HexRPG.Tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class PlayerLevel {
	public static final String
		WOODCUTTING = "WOODCUTTING",
		FARMING = "FARMING",
		MINING = "MINING",
		HITPOINTS = "HITPOINTS",
		FISHING = "FISHING",
		SMITHING = "SMITHING";
	
	private Player player;
	public PlayerLevel(Player player) {
		this.player = player;
	}
	
	public void setExp(float exp) {
		if(exp <= 0) {
			player.setExp(0);
			return;
		}
		
		float newExpPercentage = 100.0f / (float) getRequiredExp() * exp / 100.0f;
		
		player.setExp(Math.min(player.getExp() + newExpPercentage, 1.0f));
		if(player.getExp() >= 1.0f) {
			setLevel(getLevel() + 1);
			
			exp -= getRequiredExp();
			setExp(exp);
		}
	}
	
	public int getExp() {
		return (int) (player.getExp() / player.getExpToLevel() * getRequiredExp());
	}
	
	public void setLevel(int level) {
		player.setLevel(level);
	}
	
	public int getLevel() {
		return player.getLevel();
	}
	
	public int getRequiredExp() {
		if(Plugin.getCore().getConfig().getBoolean("leveling.use-table-algorithm")) {
			List<String> expTableList = Plugin.getCore().getConfig().getStringList("leveling.table-algorithm");
			String expTableItem = expTableList.get(expTableList.size() - 1);
			
			for(String item : Plugin.getCore().getConfig().getStringList("leveling.table-algorithm")) {
				if(item.startsWith(Integer.toString(getLevel()))) {
					expTableItem = item;
					break;
				}
			}
			
			String[] expTableItemArray = expTableItem.split(":");
			
			return Integer.parseInt(expTableItemArray[1]);
		} else {
			int requiredExp = Plugin.getCore().getConfig().getInt("leveling.starting-exp-required");
			for(int i=1;i<getLevel();i++) {
				requiredExp *= Plugin.getCore().getConfig().getDouble("leveling.exp-per-level-multiplier");
			}
			return requiredExp;
		}
	}
	
	public boolean hasLevel() {
		return new File(Plugin.getCore().getDataFolder() + "/dat0/levels", player.getUniqueId().toString() + ".yml").exists();
	}
	
	public static HashMap<EntityType, Integer> getExpList() {
		HashMap<EntityType, Integer> list = new HashMap<EntityType, Integer>();
		
		for(String keys : Plugin.getCore().getConfig().getConfigurationSection("leveling.gained-exp").getKeys(false)) {
			try {
				if(!keys.equalsIgnoreCase("DEFAULT")) {
					EntityType type = EntityType.valueOf(keys.toUpperCase().replace(" ", "_"));
					list.put(type, Plugin.getCore().getConfig().getInt("leveling.gained-exp." + keys));
				}
			} catch(NullPointerException e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error found in HexRPG:");
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The plugin tried to get the required Exp for an entity type, but was not able");
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "to recognize it as a valid type. Please check the list and see if you made a typo.");
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error on value: '" + keys + "'");
				continue;
			}
		}
		
		return list;
	}
	
	public void setSkillLevel(String skill, int level) {
		int maxLevel = Plugin.getCore().getConfig().getInt("leveling.skills.max-skill-levels." + skill.toLowerCase());
		level = level > maxLevel ? maxLevel : level;
		
		File file = new File(Plugin.getCore().getDataFolder() + "/dat0/levels/", player.getUniqueId().toString() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		int oldLevel = config.getInt("SKILLS." + skill.toUpperCase().replace(" ", "_") + ".level", level);
		config.set("SKILLS." + skill.toUpperCase().replace(" ", "_") + ".level", level);
		
		int masterLevel = getSkillLevel(WOODCUTTING) + getSkillLevel(FARMING) + getSkillLevel(MINING) + getSkillLevel(HITPOINTS) + getSkillLevel(FISHING) + getSkillLevel(SMITHING);
		config.set("SKILLS.MASTER", masterLevel + 1);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(player.isOnline()) {
			Player onlinePlayer = (Player) player;
			
			if(oldLevel > 0) {
				onlinePlayer.sendMessage(Plugin.getCore().getLangTools().getMessage("leveling.skill-level-up").replace("%old_level%", "" + oldLevel).replace("%new_level%", "" + level).replace("%skill%", WordUtils.capitalizeFully(skill.replace("_", " "))));
			}
		}
	}
	
	public int getSkillLevel(String skill) {
		File file = new File(Plugin.getCore().getDataFolder() + "/dat0/levels/", player.getUniqueId().toString() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(config.isConfigurationSection("SKILLS") && config.isSet("SKILLS." + skill.toUpperCase().replace(" ", "_") + ".level")) {
			return config.getInt("SKILLS." + skill.toUpperCase().replace(" ", "_") + ".level");
		}
		return 0;
	}
	
	public void setSkillExp(String skill, int exp) {
		File file = new File(Plugin.getCore().getDataFolder() + "/dat0/levels/", player.getUniqueId().toString() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.set("SKILLS." + skill.toUpperCase().replace(" ", "_") + ".exp", exp);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(exp >= getRequiredExp()) {
			setSkillLevel(skill, getLevel() + 1);
			setSkillExp(skill, exp - getRequiredExp());
		}
	}
	
	public int getSkillExp(String skill) {
		File file = new File(Plugin.getCore().getDataFolder() + "/dat0/levels/", player.getUniqueId().toString() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(config.isInt("SKILLS." + skill.toUpperCase().replace(" ", "_") + ".exp")) {
			return config.getInt("SKILLS." + skill.toUpperCase().replace(" ", "_") + ".exp");
		}
		
		return 0;
	}
	
	public int getRequiredSkillExp(String skill) {
		if(Plugin.getCore().getConfig().getBoolean("leveling.use-table-algorithm")) {
			List<String> expTableList = Plugin.getCore().getConfig().getStringList("leveling.table-algorithm");
			String expTableItem = expTableList.get(expTableList.size() - 1);
			
			for(String item : Plugin.getCore().getConfig().getStringList("leveling.table-algorithm")) {
				if(item.startsWith(Integer.toString(getSkillLevel(skill)))) {
					expTableItem = item;
					break;
				}
			}
			
			String[] expTableItemArray = expTableItem.split(":");
			
			return Integer.parseInt(expTableItemArray[1]);
		} else {
			int requiredExp = Plugin.getCore().getConfig().getInt("leveling.starting-exp-required");
			for(int i=1;i<getSkillLevel(skill);i++) {
				requiredExp *= Plugin.getCore().getConfig().getDouble("leveling.exp-per-level-multiplier");
			}
			return requiredExp;
		}
	}
	
	public int getMasterLevel() {
		File file = new File(Plugin.getCore().getDataFolder() + "/dat0/levels/", player.getUniqueId().toString() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(config.isConfigurationSection("SKILLS") && config.isSet("SKILLS.MASTER")) {
			return config.getInt("SKILLS.MASTER");
		}
		return 0;
	}
}