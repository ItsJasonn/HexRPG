package me.ItsJasonn.HexRPG.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class HexRPGScroll {
	private String scrollName;
	private ItemStack scroll;
	
	private String description;
	private String color;
	private double successRate, destroyRate;
	
	private SubConfig subConfig = new SubConfig(SubConfig.TYPES.SCROLLS);
	private YamlConfiguration config = YamlConfiguration.loadConfiguration(subConfig.getFile());
	
	public HexRPGScroll(String scrollName) {
		this.scrollName = scrollName;
		
		if(!_a()) {
			this.scroll = new ItemStack(Material.AIR);
			return;
		}
		
		description = ChatColor.translateAlternateColorCodes('&', config.getString("description." + scrollName));
		
		String additionals = "";
		HashMap<String, HashMap<String, Integer>> map = _b();
		for(String mapKeys : map.get(scrollName).keySet()) {
			if(mapKeys.equalsIgnoreCase("IDENTIFICATION")) {
				continue;
			}
			
			if(additionals.length() > 0) {
				additionals += ", ";
			} else {
				YamlConfiguration statsConfig = new SubConfig(SubConfig.TYPES.STATS).getConfig();
				additionals = ChatColor.translateAlternateColorCodes('&', statsConfig.getString("stats.stats-colors.primary"));
			}
			
			int amount = map.get(scrollName).get(mapKeys);
			additionals += "+" + amount + " " + mapKeys;
		}
		
		color = ChatColor.translateAlternateColorCodes('&', config.getString("color." + scrollName)) + "" + ChatColor.BOLD;
		successRate = config.getDouble("success." + scrollName);
		destroyRate = config.getDouble("destroy." + scrollName);
		
		String successRateLine = ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getLangTools().getMessage("scrolls.translation.success-rate")) + " " + successRate + "%";
		String destroyRateLine = ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getLangTools().getMessage("scrolls.translation.on-failure")).replace("%destroy_rate%", "" + destroyRate);
		
		this.scroll = new ItemStack(Material.PAPER);
		ItemMeta meta = this.scroll.getItemMeta();
		
		meta.setDisplayName(color + WordUtils.capitalizeFully(scrollName) + " " + Plugin.getCore().getLangTools().getUncoloredMessage("scrolls.translation.scroll"));
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.addAll(Plugin.getCore().ensmallerString(description, 35, null));
		lore.addAll(Plugin.getCore().ensmallerString(additionals, 35, null));
		lore.add("");
		lore.addAll(Plugin.getCore().ensmallerString(successRateLine, 35, null));
		lore.addAll(Plugin.getCore().ensmallerString(destroyRateLine, 35, null));
		meta.setLore(lore);
		
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		scroll.setItemMeta(meta);
		scroll.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
	}
	
	public void give(Player player) {
		player.getInventory().addItem(scroll);
	}
	
	public void give(Player player, int slot, boolean override) {
		if(!override && (player.getInventory().getItem(slot) != null || player.getInventory().getItem(slot).getType() != Material.AIR)) {
			return;
		}
		player.getInventory().setItem(slot, scroll);
	}
	
	private boolean _a() {
		if(!config.isSet("description." + scrollName) || !config.isSet("color." + scrollName) || !config.isSet("additionals." + scrollName) || !config.isSet("success." + scrollName) || !config.isSet("destroy." + scrollName)) {
			return false;
		}
		return true;
	}
	
	private HashMap<String, HashMap<String, Integer>> _b() {
		if(!_a()) {
			return null;
		}
		
		Set<String> keys = config.getConfigurationSection("additionals").getKeys(false);
		HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
		
		for(String k : keys) {
			HashMap<String, Integer> additionals = new HashMap<String, Integer>();
			for(String splitted : config.getString("additionals." + scrollName).split(":")) {
				if(splitted.equalsIgnoreCase("IDENTIFICATION")) {
					additionals.put("IDENTIFICATION", -1);
					continue;
				}
				
				String stat = splitted.substring(0, splitted.indexOf("("));
				
				String value = splitted.substring(splitted.indexOf("(") + 1, splitted.indexOf(")"));
				int amount = 0;
				if(value.contains("-")) {
					String[] splittedValues = value.split("-");
					int randA = Integer.parseInt(splittedValues[0]);
					int randB = Integer.parseInt(splittedValues[1]);
					
					amount = ThreadLocalRandom.current().nextInt(randA, randB + 1);
				} else {
					amount = Integer.parseInt(splitted.substring(splitted.indexOf("(") + 1, splitted.indexOf(")")));
				}
				
				additionals.put(stat, amount);
			}
			
			map.put(k, additionals);
		}
		
		return map;
	}
	
	public static ArrayList<HexRPGScroll> getScrolls() {
		ArrayList<HexRPGScroll> list = new ArrayList<HexRPGScroll>();
		
		SubConfig subConfig = new SubConfig(SubConfig.TYPES.SCROLLS);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(subConfig.getFile());
		
		Set<String> scrollTypes = config.getConfigurationSection("description").getKeys(false);
		if(scrollTypes.size() == 0) {
			return list;
		}
		
		for(String scrollName : scrollTypes) {
			HexRPGScroll hexRpgScroll = new HexRPGScroll(scrollName);
			list.add(hexRpgScroll);
		}
		
		return list;
	}

	public ItemStack getScroll() {
		return scroll;
	}

	public String getDescription() {
		return description;
	}

	public String getColor() {
		return color;
	}

	public double getSuccessRate() {
		return successRate;
	}

	public double getDestroyRate() {
		return destroyRate;
	}
}