package me.ItsJasonn.HexRPG.Listener.onInventoryClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.RandomLib.RandomStrings;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;

public class SmithingWindow implements Listener {
	private HashMap<Integer, Integer> smeltCount = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> smeltTask = new HashMap<Integer, Integer>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerLevel level = new PlayerLevel(player);
		
		if(event.getInventory().getType().equals(InventoryType.ANVIL)) {
			return;
		}
		
		if(event.getInventory().getTitle().equals(Plugin.getCore().getConfig().getString("smithing.smithing-window"))) {
			if(event.getView().getTopInventory() != event.getClickedInventory()) {
				if(event.getCurrentItem() != null) {
					ItemStack clickedItem = event.getCurrentItem();
					if(clickedItem.getType() == Material.IRON_ORE || clickedItem.getType() == Material.GOLD_ORE || clickedItem.getType() == Material.DIAMOND_ORE) {
						player.closeInventory();
						event.setCancelled(true);

						Random random = new Random();
						final int r = random.nextInt(Integer.MAX_VALUE);

						final ItemStack item = event.getCurrentItem();
						smeltCount.put(Integer.valueOf(r), Integer.valueOf(item.getAmount() * 3));

						this.smeltTask.put(Integer.valueOf(r), Integer.valueOf(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
							public void run() {
								if(smeltCount.get(r) == 0) {
									player.sendTitle(Plugin.getCore().getLangTools().getMessage("smithing.smithing"), Plugin.getCore().getLangTools().getMessage("smithing.done"));
									player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);

									Bukkit.getServer().getScheduler().cancelTask(((Integer) smeltTask.get(Integer.valueOf(r))).intValue());

									player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);

									ArrayList<String> lore = new ArrayList<String>();
									lore.add(ChatColor.GRAY + "Drag and Drop on Equipment");
									lore.add(ChatColor.GRAY + "Repairs 1-12 Durability");

									ItemStack scraps = new ItemStack(Material.INK_SAC);
									ItemMeta scrapsMeta = scraps.getItemMeta();
									
									if(item.getType() == Material.IRON_ORE) {
										scrapsMeta.setDisplayName(ChatColor.GREEN + "Iron Scrap");
										scraps.setDurability((short) 7);
									} else if(item.getType() == Material.GOLD_ORE) {
										scrapsMeta.setDisplayName(ChatColor.GREEN + "Gold Scrap");
										scraps.setDurability((short) 11);
									} else if(item.getType() == Material.DIAMOND_ORE) {
										scrapsMeta.setDisplayName(ChatColor.GREEN + "Diamond Scrap");
										scraps.setDurability((short) 12);
									}
									
									scrapsMeta.setLore(lore);
									scraps.setItemMeta(scrapsMeta);
									scraps.setAmount(item.getAmount());

									player.getInventory().addItem(scraps);
									
									int rSkillExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min");
									PlayerLevel level = new PlayerLevel(player);
									level.setSkillExp("SMITHING", level.getSkillExp("SMITHING") + rSkillExp);
								} else {
									player.sendTitle(Plugin.getCore().getLangTools().getMessage("smithing.smithing"), Plugin.getCore().getLangTools().getMessage("smithing.progress").replace("%seconds%", "" + smeltCount.get(r)));
									smeltCount.put(r, smeltCount.get(r) - 1);
								}
							}
						}, 0L, 20L)));

						event.setCurrentItem(new ItemStack(Material.AIR));
						event.setCursor(new ItemStack(Material.AIR));
					} else {
						event.setCancelled(true);
					}
				}
			} else {
				if(event.getSlot() == 3) {
					event.setCancelled(true);

					Inventory armor = Bukkit.getServer().createInventory(player, 45, Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.armor-crafting"));

					ItemStack back = new ItemStack(Material.BARRIER);
					ItemMeta backMeta = back.getItemMeta();
					backMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
					back.setItemMeta(backMeta);

					ArrayList<String> dragonArmorLore = new ArrayList<String>();
					dragonArmorLore.add(ChatColor.GRAY + "Unlocks at level " + Plugin.getCore().getConfig().getInt("smithing.required-level.dragonscale-armor"));
					
					ItemStack dragonArmor = new ItemStack(Material.BOOK);
					ItemMeta dragonArmorMeta = dragonArmor.getItemMeta();
					if(level.getSkillLevel("SMITHING") < Plugin.getCore().getConfig().getInt("smithing.required-level.dragonscale-armor")) {
						dragonArmorMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Dragonscale Armor");
					} else {
						dragonArmorMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Dragonscale Armor");
					}
					
					dragonArmorMeta.setLore(dragonArmorLore);
					dragonArmor.setItemMeta(dragonArmorMeta);

					armor.setItem(0, back);
					armor.setItem(8, dragonArmor);
					
					ItemStack chainmailHelmet = setRequiredScraps(player, new ItemStack(Material.CHAINMAIL_HELMET), new String[]{"CHAINMAIL"}, new int[]{16});
					ItemStack chainmailChestplate = setRequiredScraps(player, new ItemStack(Material.CHAINMAIL_CHESTPLATE), new String[]{"CHAINMAIL"}, new int[]{32});
					ItemStack chainmailLeggings = setRequiredScraps(player, new ItemStack(Material.CHAINMAIL_LEGGINGS), new String[]{"CHAINMAIL"}, new int[]{32});
					ItemStack chainmailBoots = setRequiredScraps(player, new ItemStack(Material.CHAINMAIL_BOOTS), new String[]{"CHAINMAIL"}, new int[]{16});

					ItemStack ironHelmet = setRequiredScraps(player, new ItemStack(Material.IRON_HELMET), new String[]{"IRON"}, new int[]{16});
					ItemStack ironChestplate = setRequiredScraps(player, new ItemStack(Material.IRON_CHESTPLATE), new String[]{"IRON"}, new int[]{32});
					ItemStack ironLeggings = setRequiredScraps(player, new ItemStack(Material.IRON_LEGGINGS), new String[]{"IRON"}, new int[]{32});
					ItemStack ironBoots = setRequiredScraps(player, new ItemStack(Material.IRON_BOOTS), new String[]{"IRON"}, new int[]{16});
					
					armor.setItem(3, chainmailHelmet);
					armor.setItem(5, ironHelmet);
					
					armor.setItem(12, chainmailChestplate);
					armor.setItem(14, ironChestplate);
					
					armor.setItem(21, chainmailLeggings);
					armor.setItem(23, ironLeggings);
					
					armor.setItem(30, chainmailBoots);
					armor.setItem(32, ironBoots);

					player.openInventory(armor);
				} else {
					if(event.getSlot() == 4) {
						event.setCancelled(true);

						ArrayList<String> goldWeaponsLore = new ArrayList<String>();
						ArrayList<String> crystalWeaponsLore = new ArrayList<String>();
						ArrayList<String> dragonWeaponsLore = new ArrayList<String>();
						ArrayList<String> legendaryWeaponsLore = new ArrayList<String>();

						goldWeaponsLore.add(ChatColor.GRAY + "Unlocks at level " + Plugin.getCore().getConfig().getInt("smithing.required-level.gold-weapons"));
						crystalWeaponsLore.add(ChatColor.GRAY + "Unlocks at level " + Plugin.getCore().getConfig().getInt("smithing.required-level.crystal-weapons"));
						dragonWeaponsLore.add(ChatColor.GRAY + "Unlocks at level " + Plugin.getCore().getConfig().getInt("smithing.required-level.dragon-weapons"));
						legendaryWeaponsLore.add(ChatColor.GRAY + "Unlocks at level " + Plugin.getCore().getConfig().getInt("smithing.required-level.legendary-weapons"));
						
						ItemStack goldWeapons = new ItemStack(Material.BOOK);
						ItemMeta goldWeaponsMeta = goldWeapons.getItemMeta();
						if(level.getSkillLevel("SMITHING") < Plugin.getCore().getConfig().getInt("smithing.required-level.gold-weapons")) {
							goldWeaponsMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Gold Weapons");
						} else {
							goldWeaponsMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Gold Weapons");
						}
						goldWeaponsMeta.setLore(goldWeaponsLore);
						goldWeapons.setItemMeta(goldWeaponsMeta);
						
						ItemStack crystalWeapons = new ItemStack(Material.BOOK);
						ItemMeta crystalWeaponsMeta = crystalWeapons.getItemMeta();
						if(level.getSkillLevel("SMITHING") < Plugin.getCore().getConfig().getInt("smithing.required-level.crystal-weapons")) {
							crystalWeaponsMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Crystal Weapons");
						} else {
							crystalWeaponsMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Crystal Weapons");
						}
						crystalWeaponsMeta.setLore(crystalWeaponsLore);
						crystalWeapons.setItemMeta(crystalWeaponsMeta);
						
						ItemStack dragonWeapons = new ItemStack(Material.BOOK);
						ItemMeta dragonWeaponsMeta = dragonWeapons.getItemMeta();
						if(level.getSkillLevel("SMITHING") < Plugin.getCore().getConfig().getInt("smithing.required-level.dragon-weapons")) {
							dragonWeaponsMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Dragon Weapons");
						} else {
							dragonWeaponsMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Dragon Weapons");
						}
						dragonWeaponsMeta.setLore(dragonWeaponsLore);
						dragonWeapons.setItemMeta(dragonWeaponsMeta);
						
						ItemStack legendaryWeapons = new ItemStack(Material.BOOK);
						ItemMeta LegendaryWeaponsMeta = legendaryWeapons.getItemMeta();
						if(level.getSkillLevel("SMITHING") < Plugin.getCore().getConfig().getInt("smithing.required-level.legendary-weapons")) {
							LegendaryWeaponsMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Legendary Weapons");
						} else {
							LegendaryWeaponsMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Legendary Weapons");
						}
						LegendaryWeaponsMeta.setLore(legendaryWeaponsLore);
						legendaryWeapons.setItemMeta(LegendaryWeaponsMeta);
						
						Inventory weapon = Bukkit.getServer().createInventory(player, 54, ChatColor.BLACK + "Weapon Crafting");

						ItemStack back = new ItemStack(Material.BARRIER);
						ItemMeta backMeta = back.getItemMeta();
						backMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
						back.setItemMeta(backMeta);
						
						weapon.setItem(3, goldWeapons);
						weapon.setItem(4, crystalWeapons);
						weapon.setItem(5, dragonWeapons);
						weapon.setItem(6, legendaryWeapons);
						weapon.setItem(0, back);
						
						ItemStack mace = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 9), new String[]{"IRON"}, new int[]{8});
						ItemStack rapier = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 10), new String[]{"IRON"}, new int[]{8});
						ItemStack cutlass = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 11), new String[]{"IRON"}, new int[]{8});
						ItemStack scythe = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 12), new String[]{"IRON"}, new int[]{8});
						ItemStack katar = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 14), new String[]{"IRON"}, new int[]{8});
						ItemStack axe = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 4), new String[]{"IRON"}, new int[]{8});
						ItemStack battleAxe = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 5), new String[]{"IRON"}, new int[]{8});
						ItemStack spear = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 6), new String[]{"IRON"}, new int[]{8});
						ItemStack halberd = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 7), new String[]{"IRON"}, new int[]{8});
						ItemStack warhammer = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 8), new String[]{"IRON"}, new int[]{8});
						ItemStack dagger = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 15), new String[]{"IRON"}, new int[]{8});
						ItemStack sword = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 1), new String[]{"IRON"}, new int[]{8});
						ItemStack longsword = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 2), new String[]{"IRON"}, new int[]{8});
						ItemStack sword1 = setRequiredScraps(player, new ItemStack(Material.IRON_SWORD, 1, (short) 0), new String[]{"IRON"}, new int[]{8});
						ItemStack sword2 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 3), new String[]{"IRON"}, new int[]{8});
						ItemStack flail = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 13), new String[]{"IRON"}, new int[]{8});
						ItemStack bow = setRequiredScraps(player, new ItemStack(Material.BOW, 1, (short) 0), new String[]{"WOODEN"}, new int[]{8});
						ItemStack combatBow = setRequiredScraps(player, new ItemStack(Material.BOW, 1, (short) 1), new String[]{"WOODEN"}, new int[]{16});
						ItemStack superiorBow = setRequiredScraps(player, new ItemStack(Material.BOW, 1, (short) 3), new String[]{"WOODEN"}, new int[]{16});
						
						weapon.setItem(20, mace);
						weapon.setItem(21, rapier);
						weapon.setItem(22, cutlass);
						weapon.setItem(23, scythe);
						weapon.setItem(24, katar);
						weapon.setItem(29, axe);
						weapon.setItem(30, battleAxe);
						weapon.setItem(31, spear);
						weapon.setItem(32, halberd);
						weapon.setItem(33, warhammer);
						weapon.setItem(38, dagger);
						weapon.setItem(39, sword);
						weapon.setItem(40, longsword);
						weapon.setItem(41, sword1);
						weapon.setItem(42, sword2);
						weapon.setItem(49, flail);
						
						weapon.setItem(18, bow);
						weapon.setItem(27, combatBow);
						weapon.setItem(36, superiorBow);
						
						if(level.getSkillLevel("SMITHING") >= 20) {
							player.openInventory(weapon);
						} else {
							player.closeInventory();
							player.sendMessage(ChatColor.RED + "Your hammer does not have the required level to access this section!");
						}
					}
				}
			}
		} else {
			if(event.getInventory().getName().equals(Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.armor-crafting")) || event.getInventory().getName().equals(Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.weapon-crafting"))) {
				event.setCancelled(true);
				if((event.getCurrentItem() != null) && (event.getCurrentItem().getType() != Material.AIR)) {
					if((event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasDisplayName()) && (event.getCurrentItem().getItemMeta().getDisplayName().equals(Plugin.getCore().getLangTools().getMessage("navigation.previous-page")))) {
						openSmithingInventory(player);
					} else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Dragonscale Armor")) {
						Inventory armor = Bukkit.getServer().createInventory(player, 45, Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.armor-crafting"));

						ItemStack back = new ItemStack(Material.BARRIER);
						ItemMeta backMeta = back.getItemMeta();
						backMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
						back.setItemMeta(backMeta);

						armor.setItem(0, back);
						
						ItemStack dragonscaleHelmet = setRequiredScraps(player, new ItemStack(Material.DIAMOND_HELMET), new String[]{"DRAGONSCALE"}, new int[]{16});
						ItemStack dragonscaleChestplate = setRequiredScraps(player, new ItemStack(Material.DIAMOND_CHESTPLATE), new String[]{"DRAGONSCALE"}, new int[]{32});
						ItemStack dragonscaleLeggings = setRequiredScraps(player, new ItemStack(Material.DIAMOND_LEGGINGS), new String[]{"DRAGONSCALE"}, new int[]{32});
						ItemStack dragonscaleBoots = setRequiredScraps(player, new ItemStack(Material.DIAMOND_BOOTS), new String[]{"DRAGONSCALE"}, new int[]{16});
						
						ItemStack dragonShield = setRequiredScraps(player, new ItemStack(Material.SHIELD, 1, (short) 1), new String[]{"DRAGONSCALE"}, new int[]{32});
						ItemStack demonicShield = setRequiredScraps(player, new ItemStack(Material.SHIELD, 1, (short) 2), new String[]{"DRAGONSCALE"}, new int[]{32});
						ItemStack dragonWings = setRequiredScraps(player, new ItemStack(Material.ELYTRA), new String[]{"DRAGONSCALE"}, new int[]{32});
						
						armor.setItem(11, dragonShield);
						armor.setItem(15, dragonWings);
						armor.setItem(20, demonicShield);
						
						armor.setItem(4, dragonscaleHelmet);
						armor.setItem(13, dragonscaleChestplate);
						armor.setItem(22, dragonscaleLeggings);
						armor.setItem(31, dragonscaleBoots);
						
						if(level.getSkillLevel("SMITHING") >= Plugin.getCore().getConfig().getInt("smithing.required-level.dragonscale-armor")) {
							player.openInventory(armor);
						} else {
							player.closeInventory();
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("smithing.too-low-level"));
						}
					} else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Gold Weapons")) {
						Inventory weapon = Bukkit.getServer().createInventory(player, 54, Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.weapon-crafting"));

						ItemStack back = new ItemStack(Material.BARRIER);
						ItemMeta backMeta = back.getItemMeta();
						backMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
						back.setItemMeta(backMeta);

						weapon.setItem(0, back);
						
						ItemStack mace = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 9), new String[]{"GOLD"}, new int[]{8});
						ItemStack rapier = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 10), new String[]{"GOLD"}, new int[]{8});
						ItemStack cutlass = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 11), new String[]{"GOLD"}, new int[]{8});
						ItemStack scythe = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 12), new String[]{"GOLD"}, new int[]{8});
						ItemStack katar = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 14), new String[]{"GOLD"}, new int[]{8});
						ItemStack axe = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 4), new String[]{"GOLD"}, new int[]{8});
						ItemStack battleAxe = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 5), new String[]{"GOLD"}, new int[]{8});
						ItemStack spear = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 6), new String[]{"GOLD"}, new int[]{8});
						ItemStack halberd = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 7), new String[]{"GOLD"}, new int[]{8});
						ItemStack warhammer = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 8), new String[]{"GOLD"}, new int[]{8});
						ItemStack dagger = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 15), new String[]{"GOLD"}, new int[]{8});
						ItemStack sword = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 1), new String[]{"GOLD"}, new int[]{8});
						ItemStack longsword = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 2), new String[]{"GOLD"}, new int[]{8});
						ItemStack sword1 = setRequiredScraps(player, new ItemStack(Material.GOLDEN_SWORD, 1, (short) 0), new String[]{"GOLD"}, new int[]{8});
						ItemStack sword2 = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 3), new String[]{"GOLD"}, new int[]{8});
						ItemStack flail = setRequiredScraps(player, new ItemStack(Material.GOLDEN_AXE, 1, (short) 13), new String[]{"GOLD"}, new int[]{8});
						
						weapon.setItem(20, mace);
						weapon.setItem(21, rapier);
						weapon.setItem(22, cutlass);
						weapon.setItem(23, scythe);
						weapon.setItem(24, katar);
						weapon.setItem(29, axe);
						weapon.setItem(30, battleAxe);
						weapon.setItem(31, spear);
						weapon.setItem(32, halberd);
						weapon.setItem(33, warhammer);
						weapon.setItem(38, dagger);
						weapon.setItem(39, sword);
						weapon.setItem(40, longsword);
						weapon.setItem(41, sword1);
						weapon.setItem(42, sword2);
						weapon.setItem(49, flail);
						
						if(level.getSkillLevel("SMITHING") >= Plugin.getCore().getConfig().getInt("smithing.required-level.gold-weapons")) {
							player.openInventory(weapon);
						} else {
							player.closeInventory();
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("smithing.too-low-level"));
						}
					} else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Crystal Weapons")) {
						Inventory weapon = Bukkit.getServer().createInventory(player, 54, Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.weapon-crafting"));

						ItemStack back = new ItemStack(Material.BARRIER);
						ItemMeta backMeta = back.getItemMeta();
						backMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
						back.setItemMeta(backMeta);

						weapon.setItem(0, back);
						
						ItemStack mace = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 9), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack rapier = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 10), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack cutlass = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 11), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack scythe = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 12), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack katar = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 14), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack axe = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 4), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack battleAxe = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 5), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack spear = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 6), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack halberd = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 7), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack warhammer = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 8), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack dagger = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 15), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack sword = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 1), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack longsword = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 2), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack sword1 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_SWORD, 1, (short) 0), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack sword2 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 3), new String[]{"CRYSTAL"}, new int[]{8});
						ItemStack flail = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 13), new String[]{"CRYSTAL"}, new int[]{8});
						
						weapon.setItem(20, mace);
						weapon.setItem(21, rapier);
						weapon.setItem(22, cutlass);
						weapon.setItem(23, scythe);
						weapon.setItem(24, katar);
						weapon.setItem(29, axe);
						weapon.setItem(30, battleAxe);
						weapon.setItem(31, spear);
						weapon.setItem(32, halberd);
						weapon.setItem(33, warhammer);
						weapon.setItem(38, dagger);
						weapon.setItem(39, sword);
						weapon.setItem(40, longsword);
						weapon.setItem(41, sword1);
						weapon.setItem(42, sword2);
						weapon.setItem(49, flail);
						
						if(level.getSkillLevel("SMITHING") >= Plugin.getCore().getConfig().getInt("smithing.required-level.crystal-weapons")) {
							player.openInventory(weapon);
						} else {
							player.closeInventory();
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("smithing.too-low-level"));
						}
					} else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Dragon Weapons")) {
						Inventory weapon = Bukkit.getServer().createInventory(player, 54, Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.weapon-crafting"));

						ItemStack back = new ItemStack(Material.BARRIER);
						ItemMeta backMeta = back.getItemMeta();
						backMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
						back.setItemMeta(backMeta);

						weapon.setItem(0, back);
						
						ItemStack mace = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 24), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack rapier = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 25), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack cutlass = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 26), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack scythe = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 27), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack katar = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 29), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack axe = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 19), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack battleAxe = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 20), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack spear = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 21), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack halberd = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 22), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack warhammer = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 23), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack dagger = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 40), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack sword = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 18), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack longsword = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 16), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack sword1 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 17), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack sword2 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 39), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack flail = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 28), new String[]{"DRAGONSCALE"}, new int[]{8});
						
						weapon.setItem(20, mace);
						weapon.setItem(21, rapier);
						weapon.setItem(22, cutlass);
						weapon.setItem(23, scythe);
						weapon.setItem(24, katar);
						weapon.setItem(29, axe);
						weapon.setItem(30, battleAxe);
						weapon.setItem(31, spear);
						weapon.setItem(32, halberd);
						weapon.setItem(33, warhammer);
						weapon.setItem(38, dagger);
						weapon.setItem(39, sword);
						weapon.setItem(40, longsword);
						weapon.setItem(41, sword1);
						weapon.setItem(42, sword2);
						weapon.setItem(49, flail);
						
						if(level.getSkillLevel("SMITHING") >= Plugin.getCore().getConfig().getInt("smithing.required-level.dragon-weapons")) {
							player.openInventory(weapon);
						} else {
							player.closeInventory();
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("smithing.too-low-level"));
						}
					} else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Legendary Weapons")) {
						Inventory weapon = Bukkit.getServer().createInventory(player, 54, Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.weapon-crafting"));

						ItemStack back = new ItemStack(Material.BARRIER);
						ItemMeta backMeta = back.getItemMeta();
						backMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
						back.setItemMeta(backMeta);

						weapon.setItem(0, back);
						
						ItemStack crystalDragonSword1 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 30), new String[]{"CRYSTAL", "DRAGONSCALE"}, new int[]{8, 8});
						ItemStack goldDragonSword1 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 31), new String[]{"GOLD", "DRAGONSCALE"}, new int[]{8, 8});
						ItemStack dragonSword1 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 32), new String[]{ "DRAGONSCALE"}, new int[]{8});
						ItemStack crystalDragonSword2 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 33), new String[]{"CRYSTAL", "DRAGONSCALE"}, new int[]{8, 8});
						ItemStack goldDragonSword2 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 34), new String[]{"GOLD", "DRAGONSCALE"}, new int[]{8, 8});
						ItemStack dragonSword2 = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 35), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack crystalVorpalSword = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 36), new String[]{"CRYSTAL", "DRAGONSCALE"}, new int[]{8, 8});
						ItemStack goldVorpalSword = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 37), new String[]{"GOLD", "DRAGONSCALE"}, new int[]{8, 8});
						ItemStack dragonVorpalSword = setRequiredScraps(player, new ItemStack(Material.DIAMOND_AXE, 1, (short) 38), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack taintedDagger = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 52), new String[]{"IRON"}, new int[]{8});
						ItemStack dragonBattleAxe = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 15), new String[]{"DRAGONSCALE"}, new int[]{8});
						ItemStack vorpalSword = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 20), new String[]{"CRYSTAL", "DRAGONSCALE", "IRON", "GOLD"}, new int[]{8, 8, 8, 8});
						ItemStack doomBlade = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 27), new String[]{"DRAGONSCALE", "IRON"}, new int[]{8, 8});
						ItemStack dragonSword = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 28), new String[]{"DRAGONSCALE", "IRON", "GOLD"}, new int[]{8, 8, 8});
						ItemStack rubySword1 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 43), new String[]{"DRAGONSCALE", "RUBY"}, new int[]{8, 8});
						ItemStack rubySword2 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 44), new String[]{"DRAGONSCALE", "RUBY"}, new int[]{8, 8});
						ItemStack emeraldSword1 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 45), new String[]{"DRAGONSCALE", "EMERALD"}, new int[]{8, 8});
						ItemStack emeraldSword2 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 46), new String[]{"DRAGONSCALE", "EMERALD"}, new int[]{8, 8});
						ItemStack flamesSword1 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 47), new String[]{"DRAGONSCALE", "BLAZE_POWDER"}, new int[]{8, 8});
						ItemStack flamesSword2 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 48), new String[]{"DRAGONSCALE", "BLAZE_POWDER"}, new int[]{8, 8});
						ItemStack fairySword = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 49), new String[]{"DRAGONSCALE", "AMETHYST"}, new int[]{8, 8});
						ItemStack topazSword1 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 50), new String[]{"DRAGONSCALE", "TOPAZ"}, new int[]{8, 8});
						ItemStack topazSword2 = setRequiredScraps(player, new ItemStack(Material.IRON_HOE, 1, (short) 51), new String[]{"DRAGONSCALE", "TOPAZ"}, new int[]{8, 8});
						
						weapon.setItem(3, flamesSword2);
						weapon.setItem(4, fairySword);
						weapon.setItem(5, flamesSword1);
						weapon.setItem(9, rubySword1);
						weapon.setItem(18, emeraldSword1);
						weapon.setItem(27, topazSword1);
						weapon.setItem(17, rubySword2);
						weapon.setItem(26, emeraldSword2);
						weapon.setItem(35, topazSword2);
						weapon.setItem(21, dragonBattleAxe);
						weapon.setItem(23, taintedDagger);
						weapon.setItem(39, vorpalSword);
						weapon.setItem(40, doomBlade);
						weapon.setItem(41, dragonSword);
						weapon.setItem(45, crystalDragonSword1);
						weapon.setItem(46, goldDragonSword1);
						weapon.setItem(47, dragonSword1);
						weapon.setItem(48, crystalDragonSword2);
						weapon.setItem(49, goldDragonSword2);
						weapon.setItem(50, dragonSword2);
						weapon.setItem(51, crystalVorpalSword);
						weapon.setItem(52, goldVorpalSword);
						weapon.setItem(53, dragonVorpalSword);
						
						if(level.getSkillLevel("SMITHING") >= Plugin.getCore().getConfig().getInt("smithing.required-level.legendary-weapons")) {
							player.openInventory(weapon);
						} else {
							player.closeInventory();
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("smithing.too-low-level"));
						}
					} else {
						ArrayList<String> lore = (ArrayList<String>) event.getCurrentItem().getItemMeta().getLore();
						int[] requiredScraps = new int[lore.size()];
						
						boolean canCraft = true;
						for(int i=0;i<lore.size();i++) {
							String line = lore.get(i);
							requiredScraps[i] = RandomStrings.getNumbersFromString(ChatColor.stripColor(line));
							int totalScraps = getTotalScraps(player, line.split(" ")[1].toUpperCase());
							
							if(totalScraps < requiredScraps[i]) {
								canCraft = false;
								break;
							}
						}
						
						if(canCraft) {
							player.closeInventory();

							Random random = new Random();
							int id = random.nextInt(Integer.MAX_VALUE);
							
							for(int slots=0;slots<player.getInventory().getSize();slots++) {
								if(player.getInventory().getItem(slots) == null || player.getInventory().getItem(slots).getType() == Material.AIR) {
									continue;
								}
								ItemStack is = player.getInventory().getItem(slots);
								
								if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
									for(int i=0;i<requiredScraps.length;i++) {
										if(requiredScraps[i] <= 0) {
											continue;
										}
										
										int firstSpaceIndex = lore.get(i).indexOf(" ") + 1;
										String scrapFind = WordUtils.capitalizeFully(lore.get(i).substring(firstSpaceIndex));
										
										if(ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase(scrapFind)) {
											if(is.getAmount() <= requiredScraps[i]) {
												player.getInventory().setItem(slots, new ItemStack(Material.AIR));
												requiredScraps[i] -= is.getAmount();
											} else {
												is.setAmount(is.getAmount() - requiredScraps[i]);
												requiredScraps[i] = 0;
											}
										}
									}
								}
							}
							
							Plugin.getCore().smithCraftCount.put(Integer.valueOf(id), Integer.valueOf(33));
							Plugin.getCore().smithCraftTask.put(Integer.valueOf(id), Integer.valueOf(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
								public void run() {
									Plugin.getCore().smithCraftCount.put(Integer.valueOf(id), Integer.valueOf(((Integer) Plugin.getCore().smithCraftCount.get(Integer.valueOf(id))).intValue() - 1));
									if(Plugin.getCore().smithCraftCount.get(id) > 0) {
										player.sendTitle(Plugin.getCore().getLangTools().getMessage("crafting.crafting"), Plugin.getCore().getLangTools().getMessage("crafting.progress").replace("%seconds%", "" + Plugin.getCore().smithCraftCount.get(id)));
									} else if(Plugin.getCore().smithCraftCount.get(id) == 0) {
										player.sendTitle(Plugin.getCore().getLangTools().getMessage("crafting.crafting"), Plugin.getCore().getLangTools().getMessage("crafting.done"));
										player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);

										Random random = new Random();
										int r = random.nextInt(100) + 1;
										
										int chance = Plugin.getCore().getConfig().getInt("smithing.success-chance");
										
										PlayerLevel level = new PlayerLevel(player);
										double extraChance = Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-smithing") * level.getSkillLevel("SMITHING");
										
										if(r >= 1 && r <= chance + extraChance) {
											ItemStack copy = event.getCurrentItem();
											player.getInventory().addItem(Plugin.getCore().getStatsManager().checkStats(copy, 1, "none"));
											player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);
											
											int rSkillExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min");
											level.setSkillExp("SMITHING", level.getSkillExp("SMITHING") + rSkillExp);
										} else {
											player.sendMessage(Plugin.getCore().getLangTools().getMessage("smithing.failed"));
											player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10.0F, 2.0F);
										}
										
										int taskID = ((Integer) Plugin.getCore().smithCraftTask.get(Integer.valueOf(id))).intValue();
										Plugin.getCore().smithCraftCount.remove(Integer.valueOf(id));
										Plugin.getCore().smithCraftTask.remove(Integer.valueOf(id));
										Bukkit.getServer().getScheduler().cancelTask(taskID);
									}
								}
							}, 0L, 20L)));
						} else {
							player.closeInventory();
							player.sendMessage(ChatColor.RED + "You don't have enough scraps to craft this item!");
						}
					}
				}
			}
		}
	}

	public static void openSmithingInventory(Player player) {
		Inventory smithing = Bukkit.getServer().createInventory(player, 9, Plugin.getCore().getConfig().getString("smithing.smithing-window"));

		ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		ItemStack furnace = new ItemStack(Material.FURNACE);
		ItemStack armor = new ItemStack(Material.BOOK);
		ItemStack weapon = new ItemStack(Material.BOOK);

		ItemMeta borderMeta = border.getItemMeta();
		ItemMeta furnaceMeta = furnace.getItemMeta();
		ItemMeta armorMeta = armor.getItemMeta();
		ItemMeta weaponMeta = weapon.getItemMeta();

		ArrayList<String> furnaceLore = new ArrayList<String>();
		furnaceLore.add(ChatColor.GRAY + "Click Ore");

		furnaceMeta.setLore(furnaceLore);

		borderMeta.setDisplayName(ChatColor.BLACK + "[-]");
		furnaceMeta.setDisplayName(ChatColor.WHITE + "Smelt");
		armorMeta.setDisplayName(ChatColor.WHITE + "Armor");
		weaponMeta.setDisplayName(ChatColor.WHITE + "Weapon");

		border.setItemMeta(borderMeta);
		furnace.setItemMeta(furnaceMeta);
		armor.setItemMeta(armorMeta);
		weapon.setItemMeta(weaponMeta);

		smithing.setItem(0, furnace);
		smithing.setItem(3, armor);
		smithing.setItem(4, weapon);
		for (int i = 0; i < smithing.getSize(); i++) {
			if(smithing.getItem(i) == null) {
				smithing.setItem(i, border);
			}
		}
		player.openInventory(smithing);
	}
	
	private int getTotalScraps(Player player, String type) {
		int totalScraps = 0;
		String scrapName = ChatColor.GREEN + WordUtils.capitalizeFully(type) + " Scrap";
		
		for (ItemStack is : player.getInventory().getContents()) {
			if((is != null) && (is.hasItemMeta()) && (is.getItemMeta().hasDisplayName())) {
				String name = is.getItemMeta().getDisplayName();
				if(name.equalsIgnoreCase(scrapName)) {
					totalScraps += is.getAmount();
				}
			}
		}
		return totalScraps;
	}
	
	public ItemStack setRequiredScraps(Player player, ItemStack is, String[] scrapType, int[] amount) {
		ItemMeta isMeta = is.getItemMeta();
		
		isMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getStatsManager().getRPGName(is));
		
		ArrayList<String> isLore = new ArrayList<String>();
		if(isMeta.hasLore()) {
			isLore = (ArrayList<String>) isMeta.getLore();
		}
		
		for(int i=0;i<scrapType.length;i++) {
			String scrapsTxt = " Scrap";
			if(scrapType[i].equalsIgnoreCase("DRAGONSCALE") || scrapType[i].equalsIgnoreCase("COBBLE ROCK") || scrapType[i].equalsIgnoreCase("RUBY") || scrapType[i].equalsIgnoreCase("EMERALD") || scrapType[i].equalsIgnoreCase("TOPAZ") || scrapType[i].equalsIgnoreCase("AMETHYST") || scrapType[i].equalsIgnoreCase("BLAZE_POWDER")) {
				scrapsTxt = "";
			}
			scrapType[i] = scrapType[i].replace("_", " ");
			
			int currentScraps = getTotalScraps(player, scrapType[i]);
			if(currentScraps >= amount[i]) {
				isLore.add("" + ChatColor.GREEN + amount[i] + "x " + WordUtils.capitalizeFully(scrapType[i]) + scrapsTxt);
			} else {
				isLore.add("" + ChatColor.RED + amount[i] + "x " + WordUtils.capitalizeFully(scrapType[i]) + scrapsTxt);
			}
		}
		isMeta.setLore(isLore);
		
		isMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		isMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		isMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		
		isMeta.setUnbreakable(true);
		
		is.setItemMeta(isMeta);
		
		return is;
	}
}