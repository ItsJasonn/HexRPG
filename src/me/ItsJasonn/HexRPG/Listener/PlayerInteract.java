package me.ItsJasonn.HexRPG.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;
import me.ItsJasonn.HexRPG.Tools.UUIDTools;

public class PlayerInteract implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		
		if(event.getAction() == Action.PHYSICAL) {
			Block block = event.getClickedBlock();
			
			if(block == null) {
				return;
			}
			
			if(block.getType() == Material.FARMLAND) {
				if(!Plugin.getCore().getConfig().getBoolean("world.remove-farm-land-on-step")) {
					event.setUseInteractedBlock(Event.Result.DENY);
					event.setCancelled(true);
					block.setBlockData(block.getBlockData(), true);
				}
			}
		}
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(event.getClickedBlock() != null) {
				if(event.getClickedBlock().getType() == Material.CHEST) {
					Block clickedBlock = event.getClickedBlock();
					if((clickedBlock.getRelative(0, -2, 0) != null) && ((clickedBlock.getRelative(0, -2, 0).getState() instanceof Sign))) {
						Sign sign = (Sign) clickedBlock.getRelative(0, -2, 0).getState();
						if(sign.getLine(0).equalsIgnoreCase("[Bank]")) {
							event.setUseInteractedBlock(Event.Result.DENY);

							Inventory bankInv = Bukkit.getServer().createInventory(player, 9, player.getName() + "'s Bank");
							File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
							if(!bankFile.exists()) {
								try {
									bankFile.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
							if(!bankConfig.isConfigurationSection(player.getUniqueId().toString())) {
								bankConfig.createSection(player.getUniqueId().toString());
							}
							try {
								bankConfig.save(bankFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
							if(bankConfig.isConfigurationSection(player.getUniqueId().toString())) {
								for (int i = 0; i < 8; i++) {
									if(bankConfig.isSet(player.getUniqueId().toString() + "." + i)) {
										String iString = Integer.toString(i);
										bankInv.setItem(i, bankConfig.getItemStack(player.getUniqueId().toString() + "." + iString));
									}
								}
								ArrayList<String> bankRupeeLore = new ArrayList<String>();
								bankRupeeLore.add("");
								bankRupeeLore.add(ChatColor.YELLOW + "Left Click - Draw Rupee");
								bankRupeeLore.add(ChatColor.YELLOW + "Right Click - Bank Note");

								ItemStack rupee = new ItemStack(Material.EMERALD, 1);
								ItemMeta rupeeMeta = rupee.getItemMeta();
								rupeeMeta.setDisplayName(ChatColor.GREEN + "" + bankConfig.getInt(new StringBuilder(String.valueOf(player.getUniqueId().toString())).append(".rupees").toString()) + " Rupee(s)");
								rupeeMeta.setLore(bankRupeeLore);
								rupee.setItemMeta(rupeeMeta);
								bankInv.setItem(8, rupee);
							}
							player.openInventory(bankInv);
						} else if(sign.getLine(0).equalsIgnoreCase("[Shop]")) {
							event.setUseInteractedBlock(Event.Result.DENY);
							
							String playerName = ChatColor.stripColor(sign.getLine(1));
							OfflinePlayer owner = UUIDTools.getOfflinePlayerByName(playerName);

							File shopFile = new File(Plugin.getCore().getDataFolder(), "shop.yml");
							if(!shopFile.exists()) {
								try {
									shopFile.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							YamlConfiguration shopConfig = YamlConfiguration.loadConfiguration((File) shopFile);
							if(!shopConfig.isConfigurationSection(owner.getUniqueId().toString())) {
								shopConfig.createSection(owner.getUniqueId().toString());
								shopConfig.set(owner.getUniqueId().toString() + ".name", owner.getName());
								shopConfig.set(owner.getUniqueId().toString() + ".rows", Integer.valueOf(1));
								shopConfig.set(owner.getUniqueId().toString() + ".rupees", Integer.valueOf(0));
							}
							try {
								shopConfig.save(shopFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
							int shopRows = shopConfig.getInt(owner.getUniqueId().toString() + ".rows") * 9;

							boolean isOwner = false;
							if(owner.getName() == player.getName()) {
								isOwner = true;
							}

							Inventory shopInv = Bukkit.getServer().createInventory(player, shopRows, owner.getName() + "'s Shop");
							if(isOwner) {
								shopInv = Bukkit.getServer().createInventory(player, shopRows + 9, owner.getName() + "'s Shop");
							}
							int rowsToCheck = shopRows;
							if(shopConfig.isConfigurationSection(owner.getUniqueId().toString())) {
								for (int i = 0; i < rowsToCheck; i++) {
									if(shopConfig.isSet(owner.getUniqueId().toString() + ".content." + i)) {
										shopInv.setItem(i, shopConfig.getItemStack(owner.getUniqueId().toString() + ".content." + i));
									}
								}
							}
							if(isOwner) {
								ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
								ItemStack deleter = new ItemStack(Material.RED_STAINED_GLASS_PANE);
								ItemStack receiver = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
								ItemStack renamer = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

								ItemMeta emptyMeta = empty.getItemMeta();
								ItemMeta deleterMeta = deleter.getItemMeta();
								ItemMeta receiverMeta = receiver.getItemMeta();
								ItemMeta renamerMeta = renamer.getItemMeta();

								emptyMeta.setDisplayName(ChatColor.BLACK + "[-]");
								deleterMeta.setDisplayName(ChatColor.RED + "Delete Shop");
								receiverMeta.setDisplayName(ChatColor.GOLD + "Receive Rupees: " + ChatColor.GREEN + shopConfig.getInt(new StringBuilder(String.valueOf(owner.getUniqueId().toString())).append(".rupees").toString()));
								renamerMeta.setDisplayName(ChatColor.GREEN + "Rename Shop");

								empty.setItemMeta(emptyMeta);
								deleter.setItemMeta(deleterMeta);
								receiver.setItemMeta(receiverMeta);
								renamer.setItemMeta(renamerMeta);

								shopInv.setItem(shopRows + 0, empty);
								shopInv.setItem(shopRows + 1, empty);
								shopInv.setItem(shopRows + 2, empty);
								shopInv.setItem(shopRows + 3, deleter);
								shopInv.setItem(shopRows + 4, receiver);
								shopInv.setItem(shopRows + 5, renamer);
								shopInv.setItem(shopRows + 6, empty);
								shopInv.setItem(shopRows + 7, empty);
								shopInv.setItem(shopRows + 8, empty);
							}
							player.openInventory(shopInv);

							Plugin.getCore().clickedChest.put(player, (Chest) event.getClickedBlock().getState());
						} else if(sign.getLine(0).equalsIgnoreCase("[Picklocking]")) {
							if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools")) {
								event.setUseInteractedBlock(Event.Result.DENY);

								if(player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()
										&& ChatColor.stripColor(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).equalsIgnoreCase("Basic Lockpick")) {
									Inventory picklockingInv = null;
									if(sign.getLine(1).equalsIgnoreCase("Regular")) {
										player.openInventory(((Chest) event.getClickedBlock().getState()).getBlockInventory());
										return;
									} else if(sign.getLine(1).equalsIgnoreCase("Easy")) {
										picklockingInv = Bukkit.getServer().createInventory(player, 18, "Chest (II)");
									} else if(sign.getLine(1).equalsIgnoreCase("Advanced")) {
										picklockingInv = Bukkit.getServer().createInventory(player, 27, "Chest (III)");
									}

									Random random = new Random();
									for (int i = 0; i < picklockingInv.getSize() / 9; i++) {
										Plugin.getCore().chestIndex[i * 3 + 1] = false;
										Plugin.getCore().chestIndex[i * 3 + 2] = false;
										Plugin.getCore().chestIndex[i * 3 + 3] = false;

										int r = random.nextInt(3);
										if(r == 0) {
											Plugin.getCore().chestIndex[i * 3 + 1] = true;
										} else if(r == 1) {
											Plugin.getCore().chestIndex[i * 3 + 2] = true;
										} else if(r == 2) {
											Plugin.getCore().chestIndex[i * 3 + 3] = true;
										}

										ItemStack bar = new ItemStack(Material.IRON_BARS);
										ItemMeta barMeta = bar.getItemMeta();
										barMeta.setDisplayName(ChatColor.WHITE + "*");
										bar.setItemMeta(barMeta);

										if(!sign.getLine(1).equalsIgnoreCase("Regular")) {
											picklockingInv.setItem(i * 9 + 3, bar);
											picklockingInv.setItem(i * 9 + 4, bar);
											picklockingInv.setItem(i * 9 + 5, bar);
										}
									}

									if(!sign.getLine(1).equalsIgnoreCase("Regular")) {
										for (int i = 0; i < picklockingInv.getSize(); i++) {
											if(picklockingInv.getItem(i) == null || picklockingInv.getItem(i).getType() == Material.AIR) {
												ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
												ItemMeta paneMeta = pane.getItemMeta();
												paneMeta.setDisplayName(ChatColor.BLACK + "[-]");
												pane.setItemMeta(paneMeta);
												picklockingInv.setItem(i, pane);
											}
										}
									}

									Plugin.getCore().clickedChest.put(player, (Chest) event.getClickedBlock().getState());
									Plugin.getCore().chestIndexMap.put(player, Plugin.getCore().chestIndex);
									player.openInventory(picklockingInv);
								} else {
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("lockpicking-chest.lockpicking.no-picklock-equipped"));
								}
							}
						}
					}
				} else if((event.getClickedBlock().getType() == Material.ANVIL) && (player.getInventory().getItemInMainHand() != null) && (player.getInventory().getItemInMainHand().hasItemMeta()) && (player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
						&& (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Novice Hammer"))) {
					if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools") && new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.smithing-window")) {
						player.closeInventory();
					}
				}
			}
		}
	}
}