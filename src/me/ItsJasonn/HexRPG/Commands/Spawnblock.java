package me.ItsJasonn.HexRPG.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class Spawnblock implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Spawnblock")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp() || player.hasPermission("hexrpg.spawnblock")) {
					ArrayList<String> list = new ArrayList<String>();
					for (EntityType types : EntityType.values()) {
						try {
							Entity entity = player.getWorld().spawnEntity(new Location(Bukkit.getServer().getWorlds().get(0), 0, 0, 0), types);

							if (entity instanceof LivingEntity) {
								list.add(types.toString());
							}

							entity.remove();
						} catch (IllegalArgumentException e) {
							continue;
						} catch (NullPointerException e) {
							continue;
						}
					}

					list.add("NECROMANCER");
					list.add("CASTER");
					list.add("GHOST_MOB");
					list.add("BAT");
					list.add("ZOMBIE_WITH_HORSE");
					list.add("SKELETON_WITH_HORSE");
					list.add("WISP");
					list.add("WRAITH");

					if (args.length == 0) {
						player.sendMessage(ChatColor.YELLOW + "/Spawnblock " + ChatColor.GOLD + "- Popup this help page.");
						player.sendMessage(ChatColor.YELLOW + "/Spawnblock [Mob] <EntityType> <Tier> <Armor/Gear> <Name> <Skull Id> " + ChatColor.GOLD + "- Place a mobspawner block.");
						player.sendMessage(ChatColor.YELLOW + "/Spawnblock List " + ChatColor.GOLD + "- Get all available mobs to spawn.");
					} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("List")) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("custom-mobs.command-execution.available-mobs-header"));

							player.sendMessage(ChatColor.GOLD + list.toString().replace("[", "").replace("]", ""));
						} else if (args[0].equalsIgnoreCase("WitherSkeleton")) {
							player.getWorld().getBlockAt(player.getLocation()).setType(Material.SPAWNER);

							CreatureSpawner spawner = (CreatureSpawner) player.getWorld().getBlockAt(player.getLocation()).getState();
							spawner.setSpawnedType(EntityType.CREEPER);
							spawner.setDelay(spawner.getDelay() * 5);

							player.sendMessage(Plugin.getCore().getLangTools().getMessage("custom-mobs.command-execution.placed").replace("%type%", WordUtils.capitalizeFully(args[0])));
						} else if (list.contains(args[0].toUpperCase().replace(" ", "_"))) {
							Block block = player.getWorld().getBlockAt(player.getLocation());
							block.setType(Material.SPAWNER);

							if (block.getState() instanceof CreatureSpawner) {
								CreatureSpawner spawner = (CreatureSpawner) block.getState();

								if (args.length >= 2 && EntityType.valueOf(args[1]) != null) {
									spawner.setSpawnedType(EntityType.valueOf(args[1]));
								} else {
									if(args[0].equalsIgnoreCase("Bat") || args[0].equalsIgnoreCase("Wisp")) {
										spawner.setSpawnedType(EntityType.BAT);
									} else {
										spawner.setSpawnedType(EntityType.SKELETON);
									}
								}
								
								saveCustomMob(WordUtils.capitalizeFully(args[0]), spawner.getSpawnedType(), block.getLocation(), 1, null, WordUtils.capitalizeFully(args[0]), null);
							}
						} else {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("custom-mobs.command-execution.invalid-tye").replace("%type%", WordUtils.capitalizeFully(args[0])));
						}
					} else if (args.length >= 2 && args.length <= 6) {
						if (list.contains(args[0].toUpperCase().replace(" ", "_"))) {
							Block block = player.getWorld().getBlockAt(player.getLocation());
							block.setType(Material.SPAWNER);

							if (block.getState() instanceof CreatureSpawner) {
								CreatureSpawner spawner = (CreatureSpawner) block.getState();
								if (EntityType.valueOf(args[1]) != null) {
									spawner.setSpawnedType(EntityType.valueOf(args[1]));
								} else {
									if(args[0].equalsIgnoreCase("Bat") || args[0].equalsIgnoreCase("Wisp")) {
										spawner.setSpawnedType(EntityType.BAT);
									} else {
										spawner.setSpawnedType(EntityType.SKELETON);
									}
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("custom-mobs.command-execution.invalid-entity-type").replace("%type%", WordUtils.capitalizeFully(args[1])));
								}
								
								int tier = 1;
								if(args.length >= 3) {
									if(!Plugin.getCore().isInt(args[2])) {
										player.sendMessage(Plugin.getCore().getLangTools().getMessage("custom-mobs.command-execution.invalid-tier-number").replace("%tier%", args[2]));
										return true;
									} else {
										tier = Integer.parseInt(args[2]);
									}
								}
								
								String armorType = null;
								if(args.length >= 4) {
									if(!args[3].equalsIgnoreCase("IRON") && !args[3].equalsIgnoreCase("GOLD") && !args[3].equalsIgnoreCase("CHAINMAIL")) {
										player.sendMessage(Plugin.getCore().getLangTools().getMessage("custom-mobs.command-execution.invalid-armor-type").replace("%type%", WordUtils.capitalizeFully(args[3])));
										return true;
									} else {
										armorType = args[3].toUpperCase();
									}
								}
								
								String name = null;
								if(args.length >= 5) {
									name = ChatColor.translateAlternateColorCodes('&', args[4]).replace("_", " ");
								}
								
								String skullId = null;
								if(args.length >= 6) {
									skullId = ChatColor.translateAlternateColorCodes('&', args[5]).replace("_", " ");
								}
								
								saveCustomMob(WordUtils.capitalizeFully(args[0]), spawner.getSpawnedType(), block.getLocation(), tier, armorType, name, skullId);
							}
						} else {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("custom-mobs.command-execution.invalid-entity-type").replace("%type%", WordUtils.capitalizeFully(args[1])));
						}
					} else {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.too-many-arguments"));
					}
				} else {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.no-permissions"));
				}
			} else if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.player-only"));
			}
		}
		return false;
	}

	private void saveCustomMob(String mobType, EntityType entityType, Location location, int tier, String armorType, String name, String skullId) {
		File file = new File(Plugin.getCore().getDataFolder() + "/dat0/", "spawners.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		Random random = new Random();
		int r = random.nextInt(Integer.MAX_VALUE);
		while (config.isConfigurationSection(Integer.toString(r))) {
			r = random.nextInt(Integer.MAX_VALUE);
		}
		
		String key = Integer.toString(r);
		
		config.set(key + ".type", mobType.toUpperCase());
		if(entityType == EntityType.ENDER_DRAGON) {
			config.set(key + ".type", "DRAGON");
		}
		config.set(key + ".entity-type", entityType.name().toUpperCase());
		config.set(key + ".tier", tier);
		config.set(key + ".armorType", armorType);
		config.set(key + ".name", name);
		config.set(key + ".skullId", skullId);
		config.set(key + ".elite", false);
		
		config.set(key + ".world", location.getWorld().getName());
		config.set(key + ".x", location.getBlockX());
		config.set(key + ".y", location.getBlockY());
		config.set(key + ".z", location.getBlockZ());
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Plugin.getCore().getCustomMobManager().getSpawnerCounter().put(location, Integer.MAX_VALUE - 1);
	}
}