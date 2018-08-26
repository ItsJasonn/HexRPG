package me.ItsJasonn.HexRPG.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class BlockBreak implements Listener {
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		Player player = event.getPlayer();

		File spawnersFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "spawners.yml");
		YamlConfiguration spawnersConfig = YamlConfiguration.loadConfiguration(spawnersFile);
		if(!spawnersConfig.getKeys(false).isEmpty()) {
			for (String keys : spawnersConfig.getKeys(false)) {
				Location configLocation = new Location(Bukkit.getServer().getWorld(spawnersConfig.getString(keys + ".world")), spawnersConfig.getDouble(keys + ".x"), spawnersConfig.getDouble(keys + ".y"), spawnersConfig.getDouble(keys + ".z"));
				if(event.getBlock().getLocation().getBlockX() == configLocation.getBlockX() && event.getBlock().getLocation().getBlockY() == configLocation.getBlockY() && event.getBlock().getLocation().getBlockZ() == configLocation.getBlockZ()) {
					spawnersConfig.set(keys, null);
					try {
						spawnersConfig.save(spawnersFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Random random = new Random();

		if(event.getBlock().getType() == Material.GRASS) {
			if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money") && new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.rupees")) {
				int r = random.nextInt(100);
				int rRupees = random.nextInt(10) + 1;

				final Block brokenBlock = event.getBlock();
				brokenBlock.setType(Material.AIR);
				if(r <= Plugin.getCore().getConfig().getInt("entities.rupee-from-bush-drop-chance")) {
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(ChatColor.WHITE + "Commonly used among trading");

					ItemStack rupee = new ItemStack(Material.EMERALD, rRupees);
					ItemMeta rupeeMeta = rupee.getItemMeta();
					rupeeMeta.setDisplayName(ChatColor.GREEN + "Rupee");
					rupeeMeta.setLore(lore);
					rupee.setItemMeta(rupeeMeta);

					brokenBlock.getLocation().getWorld().dropItem(event.getBlock().getLocation(), rupee);
				}
			}
		} else if((event.getBlock().getType() == Material.IRON_ORE) || (event.getBlock().getType() == Material.GOLD_ORE) || (event.getBlock().getType() == Material.DIAMOND_ORE) || (event.getBlock().getType() == Material.STONE) || (event.getBlock().getType() == Material.EMERALD_ORE)) {
			if(player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()
					&& player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Novice Pickaxe")) {
				if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools")) {
					int r = random.nextInt(100);
					int rExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.min");

					Block brokenBlock = event.getBlock();
					Material type = event.getBlock().getType();
					brokenBlock.setType(Material.AIR);

					boolean succeed = false;
					
					PlayerLevel level = new PlayerLevel(player);
					double extraChance = Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-mining") * level.getSkillLevel("MINING");
					
					if(r >= 0 && r <= Plugin.getCore().getConfig().getInt("blocks.iron-ore-chance") + extraChance && (type == Material.IRON_ORE)) {
						player.getInventory().addItem(new ItemStack(Material.IRON_ORE));

						player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

						succeed = true;
					}
					if(r >= 0 && r <= Plugin.getCore().getConfig().getInt("blocks.gold-ore-chance") + extraChance && (type == Material.GOLD_ORE)) {
						player.getInventory().addItem(new ItemStack(Material.GOLD_ORE));

						player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

						succeed = true;
					}
					if(r >= 0 && r <= Plugin.getCore().getConfig().getInt("blocks.diamond-ore-chance") + extraChance && (type == Material.DIAMOND_ORE)) {
						player.getInventory().addItem(new ItemStack(Material.DIAMOND_ORE));

						player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

						succeed = true;
					}
					if(r >= 0 && r <= Plugin.getCore().getConfig().getInt("blocks.stone-chance") + extraChance && type == Material.STONE) {
						ItemStack cobbleRock = new ItemStack(Material.CLAY_BALL);
						ItemMeta cobbleRockMeta = cobbleRock.getItemMeta();
						cobbleRockMeta.setDisplayName(ChatColor.GREEN + "Cobble Rock");
						cobbleRock.setItemMeta(cobbleRockMeta);

						player.getInventory().addItem(new ItemStack[] { cobbleRock });

						player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

						succeed = true;
					}
					if((r >= 0) && (r <= 10.0D + 0.0D * extraChance) && (type == Material.EMERALD_ORE)) {
						player.getInventory().addItem(new ItemStack(Material.DIAMOND_ORE));

						succeed = true;
					}
					if(!succeed) {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("resources.on-break.failed"));
					} else {
						int rSkillExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min");
						level.setSkillExp("MINING", level.getSkillExp("MINING") + rSkillExp);
						
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("resources.on-break.success"));
					}

					if(!Plugin.getCore().getConfig().getBoolean("blocks.disable-timer")) {
						File blockFile = new File(Plugin.getCore().getDataFolder(), "/dat0/block.yml");
						YamlConfiguration blockConfig = YamlConfiguration.loadConfiguration(blockFile);

						int rId = random.nextInt(Integer.MAX_VALUE);
						if(blockConfig.isConfigurationSection(Integer.toString(rId))) {
							rId = random.nextInt(Integer.MAX_VALUE);
						}
						String Id = Integer.toString(rId);
						if(type == Material.IRON_ORE) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.iron-ore-timer"));
						} else if(type == Material.GOLD_ORE) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.gold-ore-timer"));
						} else if(type == Material.DIAMOND_ORE) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.diamond-ore-timer"));
						} else if(type == Material.STONE) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.stone-timer"));
						} else if(type == Material.EMERALD_ORE) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.emerald-ore-timer"));
						}
						blockConfig.set(Id + ".type", type.toString());
						blockConfig.set(Id + ".location.world", event.getBlock().getLocation().getWorld().getName());
						blockConfig.set(Id + ".location.x", Integer.valueOf((int) event.getBlock().getLocation().getX()));
						blockConfig.set(Id + ".location.y", Integer.valueOf((int) event.getBlock().getLocation().getY()));
						blockConfig.set(Id + ".location.z", Integer.valueOf((int) event.getBlock().getLocation().getZ()));
						try {
							blockConfig.save(blockFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else if((event.getBlock().getType() == Material.WHEAT_SEEDS) || (event.getBlock().getType() == Material.CARROT) || (event.getBlock().getType() == Material.POTATO) || (event.getBlock().getType() == Material.MELON)) {
			if(player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()
					&& player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Basic Hoe")) {
				if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools")) {
					int r = random.nextInt(100);
					int rExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.min");

					Block brokenBlock = event.getBlock();
					Material type = event.getBlock().getType();
					brokenBlock.setType(Material.AIR);

					boolean succeed = false;

					PlayerLevel level = new PlayerLevel(player);
					double extraChance = Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-farming") * level.getSkillLevel("FARMING");
					
					if(r >= 0 && r <= Plugin.getCore().getConfig().getInt("blocks.crop-chance") + extraChance && (type == Material.WHEAT_SEEDS)) {
						player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.WHEAT) });

						player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

						succeed = true;
					}
					if(r >= 0 && r <= Plugin.getCore().getConfig().getInt("blocks.carrot-chance") + extraChance && (type == Material.CARROT)) {
						player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.CARROT) });

						player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

						succeed = true;
					}
					if(r >= 0 && r <= Plugin.getCore().getConfig().getInt("blocks.potato-chance") + extraChance && (type == Material.POTATO)) {
						player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.POTATO) });

						player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

						succeed = true;
					}
					
					if(!succeed) {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("resources.on-break.failed"));
					} else {
						int rSkillExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min");
						level.setSkillExp("FARMING", level.getSkillExp("FARMING") + rSkillExp);
						
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("resources.on-break.success"));
					}

					if(!Plugin.getCore().getConfig().getBoolean("blocks.disable-timer")) {
						File blockFile = new File(Plugin.getCore().getDataFolder(), "/dat0/block.yml");
						YamlConfiguration blockConfig = YamlConfiguration.loadConfiguration(blockFile);

						int rId = random.nextInt(Integer.MAX_VALUE);
						if(blockConfig.isConfigurationSection(Integer.toString(rId))) {
							rId = random.nextInt(Integer.MAX_VALUE);
						}
						
						String Id = Integer.toString(rId);
						
						if(type == Material.WHEAT) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.crop-timer"));
						} else if(type == Material.CARROT) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.carrot-timer"));
						} else if(type == Material.POTATO) {
							blockConfig.set(Id + ".timer", Plugin.getCore().getConfig().getInt("blocks.potato-timer"));
						}
						
						blockConfig.set(Id + ".type", type.toString());
						blockConfig.set(Id + ".location.world", event.getBlock().getLocation().getWorld().getName());
						blockConfig.set(Id + ".location.x", Integer.valueOf((int) event.getBlock().getLocation().getX()));
						blockConfig.set(Id + ".location.y", Integer.valueOf((int) event.getBlock().getLocation().getY()));
						blockConfig.set(Id + ".location.z", Integer.valueOf((int) event.getBlock().getLocation().getZ()));
						try {
							blockConfig.save(blockFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else if(event.getBlock().getType() == Material.OAK_LOG || event.getBlock().getType() == Material.DARK_OAK_LOG) {
			int rSkillExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min");
			PlayerLevel level = new PlayerLevel(player);
			level.setSkillExp("WOODCUTTING", level.getSkillExp("WOODCUTTING") + rSkillExp);
		}
	}
}
