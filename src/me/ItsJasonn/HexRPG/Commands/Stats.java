package me.ItsJasonn.HexRPG.Commands;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;

public class Stats implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Stats")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.isOp() || player.hasPermission("hexrpg.stats")) {
					if(args.length == 0) {
						Plugin.getCore().getStatsManager().resetPlayerStats(player);
						
						Inventory stats = Bukkit.getServer().createInventory(player, Plugin.getCore().getConfig().getInt("statsmenu.row-amount") * 9, player.getName() + "'s stats");
						
						double addMeleeC = Plugin.getCore().getConfig().getDouble("classes." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".melee"), addMeleeR = Plugin.getCore().getConfig().getDouble("races." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".melee");
						double addRangedC = Plugin.getCore().getConfig().getDouble("classes." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".ranged"), addRangedR = Plugin.getCore().getConfig().getDouble("races." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".ranged");
						double addMagicC = Plugin.getCore().getConfig().getDouble("classes." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".magic"), addMagicR = Plugin.getCore().getConfig().getDouble("races." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".magic");
						double addSpeedC = Plugin.getCore().getConfig().getDouble("classes." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".speed"), addSpeedR = Plugin.getCore().getConfig().getDouble("races." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".speed");
						
						double addMelee = Math.abs((addMeleeC < 1.0 ? -addMeleeC : addMeleeC) + (addMeleeR < 1.0 ? -addMeleeR : addMeleeR));
						double addRanged = Math.abs((addRangedC < 1.0 ? -addRangedC : addRangedC) + (addRangedR < 1.0 ? -addRangedR : addRangedR));
						double addMagic = Math.abs((addMagicC < 1.0 ? -addMagicC : addMagicC) + (addMagicR < 1.0 ? -addMagicR : addMagicR));
						double addSpeed = Math.abs((addSpeedC < 1.0 ? -addSpeedC : addSpeedC) + (addSpeedR < 1.0 ? -addSpeedR : addSpeedR));
						
						ArrayList<String> profileLore = new ArrayList<String>();
						profileLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.class") + ": " + WordUtils.capitalizeFully(Plugin.getCore().getStatsManager().getClass(player)));
						profileLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.race") + ": " + WordUtils.capitalizeFully(Plugin.getCore().getStatsManager().getRace(player)));
						profileLore.add("");
						profileLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.melee") + ": " + addMelee + "x");
						profileLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.ranged") + ": " + addRanged + "x");
						profileLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.magic") + ": " + addMagic + "x");
						profileLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.speed") + ": " + addSpeed + "x");
						
						ArrayList<String> hpLore = new ArrayList<String>();
						hpLore.add("" + ChatColor.GRAY + ((int) player.getHealth()) + " / " + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
						
						ArrayList<String> dmgLore = new ArrayList<String>();
						dmgLore.add(ChatColor.GRAY + "Base damage: " + Plugin.getCore().getStatsManager().getPlayerDMG().get(player));
						dmgLore.add(ChatColor.GRAY + "Additional Damage: " + Plugin.getCore().getStatsManager().getPlayerDPS().get(player) + "%");
						dmgLore.add("");
						double calculation = (double) Plugin.getCore().getStatsManager().getPlayerDMG().get(player) / 100 * (100 + (double) Plugin.getCore().getStatsManager().getPlayerDPS().get(player));
						dmgLore.add(ChatColor.GRAY + "Result damage: " + calculation + ChatColor.RED + " HP");
						dmgLore.add(ChatColor.GOLD + "" + Plugin.getCore().getStatsManager().getPlayerDMG().get(player) + " / 100 x (100 + " + Plugin.getCore().getStatsManager().getPlayerDPS().get(player) + ")");
						
						ArrayList<String> defLore = new ArrayList<String>();
						defLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.defense") + ": " + Plugin.getCore().getStatsManager().getPlayerDefense().get(player) + "%");
						
						ArrayList<String> agLore = new ArrayList<String>();
						agLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.agility") + ": +" + Plugin.getCore().getStatsManager().getPlayerAgility().get(player));
						
						ArrayList<String> lsLore = new ArrayList<String>();
						lsLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.lifesteal") + ": " + Plugin.getCore().getStatsManager().getPlayerLifesteal().get(player) + ChatColor.RED + " HP");
						
						ArrayList<String> strLore = new ArrayList<String>();
						strLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.strength") + ": +" + Plugin.getCore().getStatsManager().getPlayerStrength().get(player));
						
						ArrayList<String> endLore = new ArrayList<String>();
						endLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.endurance") + ": +" + Plugin.getCore().getStatsManager().getPlayerEndurance().get(player));
						
						ArrayList<String> dexLore = new ArrayList<String>();
						dexLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.dexterity") + ": +" + Plugin.getCore().getStatsManager().getPlayerDexterity().get(player));
						
						ArrayList<String> critChanceLore = new ArrayList<String>();
						critChanceLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.critical-chance") + ": " + Plugin.getCore().getStatsManager().getPlayerCriticalChance().get(player) + "%");
						
						ArrayList<String> critDamageLore = new ArrayList<String>();
						critDamageLore.add(ChatColor.GRAY + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.critical-damage") + ": " + Plugin.getCore().getStatsManager().getPlayerCriticalDamage().get(player) + "%");
						
						PlayerLevel level = new PlayerLevel(player);
						
						ArrayList<String> woodcuttingLore = new ArrayList<String>();
						woodcuttingLore.add(ChatColor.GRAY + "Level: " + level.getSkillLevel(PlayerLevel.WOODCUTTING));
						woodcuttingLore.add(ChatColor.GRAY + "Exp: " + level.getSkillExp(PlayerLevel.WOODCUTTING) + " / " + level.getRequiredSkillExp(PlayerLevel.WOODCUTTING));
						woodcuttingLore.add("");
						woodcuttingLore.add(ChatColor.GRAY + "/ No function yet /");
						
						ArrayList<String> farmingLore = new ArrayList<String>();
						farmingLore.add(ChatColor.GRAY + "Level: " + level.getSkillLevel(PlayerLevel.FARMING));
						farmingLore.add(ChatColor.GRAY + "Exp: " + level.getSkillExp(PlayerLevel.FARMING) + " / " + level.getRequiredSkillExp(PlayerLevel.FARMING));
						farmingLore.add("");
						farmingLore.add(ChatColor.GRAY + "Success chance: " + ChatColor.GREEN + "+" + (Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-farming") * level.getSkillLevel("FARMING")) + "%");
						
						ArrayList<String> miningLore = new ArrayList<String>();
						miningLore.add(ChatColor.GRAY + "Level: " + level.getSkillLevel(PlayerLevel.MINING));
						miningLore.add(ChatColor.GRAY + "Exp: " + level.getSkillExp(PlayerLevel.MINING) + " / " + level.getRequiredSkillExp(PlayerLevel.MINING));
						miningLore.add("");
						miningLore.add(ChatColor.GRAY + "Success chance: " + ChatColor.GREEN + "+" + (Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-mining") * level.getSkillLevel("MINING")) + "%");
						
						ArrayList<String> hitpointsLore = new ArrayList<String>();
						hitpointsLore.add(ChatColor.GRAY + "Level: " + level.getSkillLevel(PlayerLevel.HITPOINTS));
						hitpointsLore.add(ChatColor.GRAY + "Exp: " + level.getSkillExp(PlayerLevel.HITPOINTS) + " / " + level.getRequiredSkillExp(PlayerLevel.HITPOINTS));
						hitpointsLore.add("");
						hitpointsLore.add(ChatColor.GRAY + "Extra HP: " + ChatColor.GREEN + "+" + (Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-hitpoints") * level.getSkillLevel("HITPOINTS")) + ChatColor.RED + " HP");
						
						ArrayList<String> fishingLore = new ArrayList<String>();
						fishingLore.add(ChatColor.GRAY + "Level: " + level.getSkillLevel(PlayerLevel.FISHING));
						fishingLore.add(ChatColor.GRAY + "Exp: " + level.getSkillExp(PlayerLevel.FISHING) + " / " + level.getRequiredSkillExp(PlayerLevel.FISHING));
						fishingLore.add("");
						fishingLore.add(ChatColor.GRAY + "Success chance: " + ChatColor.GREEN + "+" + (Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-fishing") * level.getSkillLevel("FISHING")) + "%");
						
						ArrayList<String> smithingLore = new ArrayList<String>();
						smithingLore.add(ChatColor.GRAY + "Level: " + level.getSkillLevel(PlayerLevel.SMITHING));
						smithingLore.add(ChatColor.GRAY + "Exp: " + level.getSkillExp(PlayerLevel.SMITHING) + " / " + level.getRequiredSkillExp(PlayerLevel.SMITHING));
						smithingLore.add("");
						smithingLore.add(ChatColor.GRAY + "Success chance: " + ChatColor.GREEN + "+" + (Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-smithing") * level.getSkillLevel("SMITHING")) + "%");
						
						ArrayList<String> masterLore = new ArrayList<String>();
						masterLore.add(ChatColor.GRAY + "Level: " + level.getMasterLevel());
						
						ItemStack profile = new ItemStack(Material.PLAYER_HEAD, 1);
						
						ItemStack hp = new ItemStack(Material.APPLE);
						ItemStack dmg = new ItemStack(Material.IRON_SWORD);
						ItemStack def = new ItemStack(Material.IRON_HELMET);
						ItemStack ag = new ItemStack(Material.FEATHER);
						ItemStack ls = new ItemStack(Material.ROSE_RED);
						ItemStack str = new ItemStack(Material.ANVIL);
						ItemStack end = new ItemStack(Material.ARROW);
						ItemStack dex = new ItemStack(Material.FIREWORK_STAR);
						ItemStack critChance = new ItemStack(Material.DIAMOND_SWORD);
						ItemStack critDamage = new ItemStack(Material.DIAMOND_SWORD);
						
						ItemStack woodcutting = new ItemStack(Material.OAK_LOG);
						ItemStack farming = new ItemStack(Material.WOODEN_HOE);
						ItemStack mining = new ItemStack(Material.WOODEN_PICKAXE);
						ItemStack hitpoints =  new ItemStack(Material.APPLE);
						ItemStack fishing = new ItemStack(Material.FISHING_ROD);
						ItemStack smithing = new ItemStack(Material.IRON_HOE, 1, (short) 21);
						ItemStack master = new ItemStack(Material.BEACON);
						
						SkullMeta profileMeta = (SkullMeta) profile.getItemMeta();
						ItemMeta hpMeta = hp.getItemMeta();
						ItemMeta dmgMeta = dmg.getItemMeta();
						ItemMeta defMeta = def.getItemMeta();
						ItemMeta agMeta = ag.getItemMeta();
						ItemMeta lsMeta = ls.getItemMeta();
						ItemMeta strMeta = str.getItemMeta();
						ItemMeta endMeta = end.getItemMeta();
						ItemMeta dexMeta = dex.getItemMeta();
						ItemMeta critChanceMeta = critChance.getItemMeta();
						ItemMeta critDamageMeta = critDamage.getItemMeta();
						
						ItemMeta woodcuttingMeta = woodcutting.getItemMeta();
						ItemMeta farmingMeta = farming.getItemMeta();
						ItemMeta miningMeta = mining.getItemMeta();
						ItemMeta hitpointsMeta = hitpoints.getItemMeta();
						ItemMeta fishingMeta = fishing.getItemMeta();
						ItemMeta smithingMeta = smithing.getItemMeta();
						ItemMeta masterMeta = master.getItemMeta();
						
						profileMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + player.getName() + "'s profile");
						hpMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.hitpoints"));
						dmgMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.damage"));
						defMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.defense"));
						agMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.agility"));
						lsMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.lifesteal"));
						strMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.strength"));
						endMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.endurance"));
						dexMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.dexterity"));
						critChanceMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.critical-chance"));
						critDamageMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.critical-damage"));
						
						woodcuttingMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.woodcutting"));
						farmingMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.farming"));
						miningMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.mining"));
						hitpointsMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.hitpoints"));
						fishingMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.fishing"));
						smithingMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.smithing"));
						masterMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + Plugin.getCore().getLangTools().getUncoloredMessage("stats.translation.master"));
						
						profileMeta.setOwningPlayer(player);
						
						profileMeta.setLore(profileLore);
						hpMeta.setLore(hpLore);
						dmgMeta.setLore(dmgLore);
						defMeta.setLore(defLore);
						agMeta.setLore(agLore);
						lsMeta.setLore(lsLore);
						strMeta.setLore(strLore);
						endMeta.setLore(endLore);
						dexMeta.setLore(dexLore);
						critChanceMeta.setLore(critChanceLore);
						critDamageMeta.setLore(critDamageLore);
						
						woodcuttingMeta.setLore(woodcuttingLore);
						farmingMeta.setLore(farmingLore);
						miningMeta.setLore(miningLore);
						hitpointsMeta.setLore(hitpointsLore);
						fishingMeta.setLore(fishingLore);
						smithingMeta.setLore(smithingLore);
						masterMeta.setLore(masterLore);
						
						profile.setItemMeta(profileMeta);
						hp.setItemMeta(hpMeta);
						dmg.setItemMeta(dmgMeta);
						def.setItemMeta(defMeta);
						ag.setItemMeta(agMeta);
						ls.setItemMeta(lsMeta);
						str.setItemMeta(strMeta);
						end.setItemMeta(endMeta);
						dex.setItemMeta(dexMeta);
						critChance.setItemMeta(critChanceMeta);
						critDamage.setItemMeta(critDamageMeta);
						
						woodcutting.setItemMeta(woodcuttingMeta);
						farming.setItemMeta(farmingMeta);
						mining.setItemMeta(miningMeta);
						hitpoints.setItemMeta(hitpointsMeta);
						fishing.setItemMeta(fishingMeta);
						smithing.setItemMeta(smithingMeta);
						master.setItemMeta(masterMeta);
						
						master.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						
						for(String rowKey : Plugin.getCore().getConfig().getConfigurationSection("statsmenu.rows").getKeys(false)) {
							int row = Integer.parseInt(rowKey);
							
							int slot = 9 * (row - 1);
							for(String slotValue : Plugin.getCore().getConfig().getStringList("statsmenu.rows." + rowKey)) {
								if(slotValue.equals("")) {
									slot++;
									continue;
								}
								
								ItemStack item = new ItemStack(Material.AIR);
								
								switch(slotValue) {
									case "PLAYER_PROFILE":
										item = profile;
										break;
									case "ITEM_HP":
										item = hp;
										break;
									case "ITEM_DAMAGE":
										item = dmg;
										break;
									case "ITEM_DEFENSE":
										item = def;
										break;
									case "ITEM_AGILITY":
										item = ag;
										break;
									case "ITEM_LIFESTEAL":
										item = ls;
										break;
									case "ITEM_STRENGTH":
										item = str;
										break;
									case "ITEM_ENDURANCE":
										item = end;
										break;
									case "ITEM_DEXTERITY":
										item = dex;
										break;
									case "ITEM_CRITICAL_CHANCE":
										item = critChance;
										break;
									case "ITEM_CRITICAL_DAMAGE":
										item = critDamage;
										break;
									case "SKILL_WOODUCTTING":
										item = woodcutting;
										break;
									case "SKILL_FARMING":
										item = farming;
										break;
									case "SKILL_MINING":
										item = mining;
										break;
									case "SKILL_HITPOINTS":
										item = hitpoints;
										break;
									case "SKILL_FISHING":
										item = fishing;
										break;
									case "SKILL_SMITHING":
										item = smithing;
										break;
									case "SKILL_MASTER":
										item = master;
										break;
									default:
										break;
								}
								
								stats.setItem(slot, item);
								slot++;
							}
						}
						
						for(int i=0;i<stats.getSize();i++) {
							if(stats.getItem(i) == null || stats.getItem(i).getType() == Material.AIR) {
								continue;
							}
							
							ItemStack is = stats.getItem(i);
							ItemMeta isMeta = is.getItemMeta();
							
							isMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
							isMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
							isMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
							
							isMeta.setUnbreakable(true);
							
							is.setItemMeta(isMeta);
						}
						
						player.openInventory(stats);
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
}