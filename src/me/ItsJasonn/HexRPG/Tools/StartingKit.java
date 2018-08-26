package me.ItsJasonn.HexRPG.Tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.RandomLib.RandomNumbers;

public class StartingKit {
	private Player player;
	
	public StartingKit(Player player) {
		this.player = player;
	}
	
	public void give() {
		YamlConfiguration config = new SubConfig(SubConfig.TYPES.STARTINGKIT).getConfig();
		
		if(config.getBoolean("starting-kit.clear-inventory")) {
			player.getInventory().clear();
		}
		
		for(String armorKeys : config.getConfigurationSection("starting-kit.items.armor").getKeys(false)) {
			String path = "starting-kit.items.armor." + armorKeys;
			if(!config.getBoolean(path + ".use")) {
				continue;
			}
			
			ItemStack armorPiece = new ItemStack(Material.matchMaterial(config.getString(path + ".type")), config.getInt(path + ".amount"), (short) config.getInt(path + ".data"));
			if(Plugin.getCore().getStatsManager().isArmor(armorPiece)) {
				if(config.getBoolean(path + ".unidentified")) {
					Plugin.getCore().getStatsManager().setUnidentified(armorPiece);
				} else {
					Plugin.getCore().getStatsManager().checkStats(armorPiece, 1, "none");
				}
			}
			
			ItemMeta armorPieceMeta = armorPiece.getItemMeta();
			
			if(!config.getString(path + ".display-name").isEmpty()) {
				armorPieceMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".display-name")));
			}
			
			if(config.getBoolean(path + ".glow")) {
				armorPieceMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			
			armorPiece.setItemMeta(armorPieceMeta);
			
			if(config.getBoolean(path + ".glow")) {
				armorPiece.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			}
			
			switch(armorKeys) {
				case "helmet":
					player.getInventory().setHelmet(armorPiece);
					break;
				case "chestplate":
					player.getInventory().setChestplate(armorPiece);
					break;
				case "leggings":
					player.getInventory().setLeggings(armorPiece);
					break;
				case "boots":
					player.getInventory().setBoots(armorPiece);
					break;
			}
		}
		
		for(String slotKeys : config.getConfigurationSection("starting-kit.items.inventory").getKeys(false)) {
			String path = "starting-kit.items.inventory." + slotKeys;
			
			ItemStack item = new ItemStack(Material.matchMaterial(config.getString(path + ".type")), config.getInt(path + ".amount"), (short) config.getInt(path + ".data"));
			if(Plugin.getCore().getStatsManager().isArmor(item) || Plugin.getCore().getStatsManager().isWeapon(item) || Plugin.getCore().getStatsManager().isThrowable(item)) {
				if(config.getBoolean(path + ".unidentified")) {
					Plugin.getCore().getStatsManager().setUnidentified(item);
				} else {
					Plugin.getCore().getStatsManager().checkStats(item, 1, "none");
				}
			}
			
			ItemMeta armorPieceMeta = item.getItemMeta();
			
			if(!config.getString(path + ".display-name").isEmpty()) {
				armorPieceMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".display-name")));
			}
			
			if(config.getBoolean(path + ".glow")) {
				armorPieceMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			
			item.setItemMeta(armorPieceMeta);
			
			if(config.getBoolean(path + ".glow")) {
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			}
			
			if(!RandomNumbers.isInt(slotKeys)) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Invalid slot key for starting kit! Please check your config file again and make sure to enter a valid number format as slot value.");
				continue;
			}
			int slot = Integer.parseInt(slotKeys);
			
			player.getInventory().setItem(slot, item);
		}
	}
}