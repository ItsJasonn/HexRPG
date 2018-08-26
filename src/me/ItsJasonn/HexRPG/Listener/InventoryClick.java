package me.ItsJasonn.HexRPG.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.RandomLib.RandomNumbers;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class InventoryClick implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClickEvent(final InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
			Plugin.getCore().getStatsManager().resetPlayerStats(player);
		}
		
		//Stealing
		if(event.getInventory().getTitle().startsWith("Stealing - ")) {
			OfflinePlayer offlineTarget = Plugin.getCore().getOfflinePlayerByName(event.getInventory().getTitle().replace("Stealing - ", ""));
			if(offlineTarget == null || !offlineTarget.isOnline()) {
				player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.unknown-player"));
				player.closeInventory();
				
				return;
			}
			
			Player target = offlineTarget.getPlayer();
			
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				event.setCancelled(true);
				
				if(!Plugin.getCore().stealCooldownId.containsKey(player)) {
					Random random = new Random();
					int r = random.nextInt(100) + 1;
					
					if(r <= Plugin.getCore().getConfig().getInt("stealing.chance")) {
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
						if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.stealing.success").replace("%item%", ChatColor.WHITE + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%amount%", "" + event.getCurrentItem().getAmount()).replace("%player%", target.getName()));
						} else {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.stealing.success").replace("%item%", ChatColor.WHITE + WordUtils.capitalizeFully(event.getCurrentItem().getType().toString().replace("_", " "))).replace("%amount%", "" + event.getCurrentItem().getAmount()).replace("%player%", target.getName()));
						}
						
						player.getInventory().addItem(event.getCurrentItem());
						target.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
						
						event.setCancelled(true);
					} else {
						if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.stealing.failed").replace("%item%", ChatColor.WHITE + event.getCurrentItem().getItemMeta().getDisplayName()).replace("%amount%", "" + event.getCurrentItem().getAmount()).replace("%player%", target.getName()));
						} else {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.stealing.failed").replace("%item%", ChatColor.WHITE + WordUtils.capitalizeFully(event.getCurrentItem().getType().toString().replace("_", " "))).replace("%amount%", "" + event.getCurrentItem().getAmount()).replace("%player%", target.getName()));
						}
					}
				} else {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.stealing.cooldown").replace("%seconds%", "" + Plugin.getCore().stealCooldown.get(player)));
				}
				
				player.closeInventory();
				
				Plugin.getCore().stealCooldown.put(player, Plugin.getCore().getConfig().getInt("stealing.cooldown"));
				Plugin.getCore().stealCooldownId.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
					public void run() {
						Plugin.getCore().stealCooldown.put(player, Plugin.getCore().stealCooldown.get(player) - 1);
						if(Plugin.getCore().stealCooldown.get(player) == 0) {
							Plugin.getCore().stealCooldown.put(player, 0);
							
							int taskId = Plugin.getCore().stealCooldownId.get(player);
							Bukkit.getServer().getScheduler().cancelTask(taskId);
							Plugin.getCore().stealCooldownId.remove(player);
						}
					}
				}, 0, 20));
			}
		}
		
		//Player menu
		if (event.getInventory().getName().startsWith("Player Menu -")) {
			event.setCancelled(true);
			
			Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(event.getInventory().getName()).replace("Player Menu - ", ""));
			
			if (event.getCurrentItem() != null) {
				if (event.getView().getTopInventory() == event.getClickedInventory()) {
					ItemStack item = event.getCurrentItem();
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
						if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Trade")) {
							if (Plugin.getCore().tradeRequest.containsKey(target) && Plugin.getCore().tradeRequest.get(target) == player) {
								Plugin.getCore().tradeRequest.remove(player);
								Plugin.getCore().tradeRequest.remove(target);
								
								Plugin.getCore().inTrade.put(player, target);
								Plugin.getCore().inTrade.put(target, player);
								
								Inventory inv = Bukkit.getServer().createInventory(null, 45, "Trading: " + player.getName() + " - " + target.getName());
								
								ItemStack trade = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
								ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
								
								ItemMeta tradeMeta = trade.getItemMeta();
								ItemMeta borderMeta = border.getItemMeta();
								
								tradeMeta.setDisplayName(ChatColor.GREEN + "Trade");
								borderMeta.setDisplayName(ChatColor.BLACK + "[-]");
								
								trade.setItemMeta(tradeMeta);
								border.setItemMeta(borderMeta);
								
								inv.setItem(0, trade);
								inv.setItem(8, trade);
								
								inv.setItem(4, border);
								inv.setItem(4 + 9, border);
								inv.setItem(4 + 18, border);
								inv.setItem(4 + 27, border);
								inv.setItem(4 + 36, border);
								
								player.openInventory(inv);
								target.openInventory(inv);
							} else {
								if (Plugin.getCore().tradeRequest.containsKey(player) && Plugin.getCore().tradeRequest.get(player) == target) {
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.invitation.already-sent").replace("%player%", target.getName()));
									player.closeInventory();
									return;
								} else {
									Plugin.getCore().tradeRequest.remove(player);
									
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.invitation.sent").replace("%player%", target.getName()));
									target.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.invitation.received").replace("%player%", player.getName()));
									
									player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 2);
									target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 2);
									
									Plugin.getCore().tradeRequest.put(player, target);
									
									player.closeInventory();
								}
							}
						} else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Dual")) {
							if (Plugin.getCore().dualRequest.containsKey(target) && Plugin.getCore().dualRequest.get(target) == player) {
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.dueling.duel.started").replace("%enemy%", target.getName()));
								target.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.dueling.duel.started").replace("%enemy%", player.getName()));
								
								Plugin.getCore().dualRequest.remove(player);
								Plugin.getCore().dualRequest.remove(target);
								
								Plugin.getCore().inDual.put(player, target);
								Plugin.getCore().inDual.put(target, player);
								
								player.closeInventory();
							} else {
								if (Plugin.getCore().dualRequest.containsKey(player) && Plugin.getCore().dualRequest.get(player) == target) {
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu-dueling.invitation.already-sent").replace("%player%", target.getName()));
									player.closeInventory();
								} else {
									Plugin.getCore().dualRequest.remove(player);
									
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.dueling.invitation.sent").replace("%player%", target.getName()));
									target.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.dueling.invitation.received").replace("%player%", player.getName()));
									
									player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 2);
									target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 2);
									
									Plugin.getCore().dualRequest.put(player, target);
									
									player.closeInventory();
								}
							}
						} else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Steal")) {
							if(!Plugin.getCore().stealCooldownId.containsKey(player)) {
								Inventory inv = Bukkit.getServer().createInventory(player, 45, "Stealing - " + target.getName());
								
								for(int i=0;i<inv.getSize();i++) {
									if(target.getInventory().getItem(i) == null || target.getInventory().getItem(i).getType() == Material.AIR) {
										continue;
									}
									
									inv.setItem(i, target.getInventory().getItem(i));
								}
								
								player.openInventory(inv);
							} else {
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.stealing.cooldown").replace("%seconds%", "" + Plugin.getCore().stealCooldown.get(player)));
								player.closeInventory();
							}
						}
					}
				}
			}
		} else if (event.getInventory().getName().startsWith("Trading:")) {
			event.setCancelled(true);
			
			int[] leftSlots = {1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39};
			int[] rightSlots = {5, 6, 7, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44};
			
			String[] playersInTitle = ChatColor.stripColor(event.getInventory().getTitle().replace("Trading: ", "")).split(" - ");
			Player leftPlayer = Bukkit.getServer().getPlayer(playersInTitle[0]);
			Player rightPlayer = Bukkit.getServer().getPlayer(playersInTitle[1]);
			
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				if(event.getRawSlot() == event.getSlot()) {
					//Top inventory
					
					if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
						if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Trade")) {
							if(event.getSlot() == 0) {
								if(leftPlayer == player) {
									ItemMeta meta = event.getCurrentItem().getItemMeta();
									meta.setDisplayName(ChatColor.GOLD + "Trade confirmed!");
									event.getCurrentItem().setItemMeta(meta);
									
									if(event.getInventory().getItem(8).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Trade confirmed!")) {
										finishTrade(leftPlayer, rightPlayer);
									}
								} else {
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.wrong-trade-button"));
								}
							} else if(event.getSlot() == 8) {
								if(rightPlayer == player) {
									ItemMeta meta = event.getCurrentItem().getItemMeta();
									meta.setDisplayName(ChatColor.GOLD + "Trade confirmed!");
									event.getCurrentItem().setItemMeta(meta);
									
									if(event.getInventory().getItem(0).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Trade confirmed!")) {
										finishTrade(leftPlayer, rightPlayer);
									}
								} else {
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.wrong-trade-button"));
								}
							}
						} else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Trade confirmed!")) {
							if(event.getSlot() == 0) {
								if(leftPlayer == player) {
									ItemMeta meta = event.getCurrentItem().getItemMeta();
									meta.setDisplayName(ChatColor.GREEN + "Trade");
									event.getCurrentItem().setItemMeta(meta);
								} else {
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.wrong-trade-button"));
								}
							} else if(event.getSlot() == 8) {
								if(rightPlayer == player) {
									ItemMeta meta = event.getCurrentItem().getItemMeta();
									meta.setDisplayName(ChatColor.GREEN + "Trade");
									event.getCurrentItem().setItemMeta(meta);
								} else {
									player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.wrong-trade-button"));
								}
							}
						}
					} else {
						int[] checkingSlots = leftSlots;
						if(rightPlayer == player) {
							checkingSlots = rightSlots;
						}
						for(int i : checkingSlots) {
							if(event.getSlot() == i) {
								player.getInventory().addItem(event.getCurrentItem());
								event.setCurrentItem(new ItemStack(Material.AIR));
								return;
							}
						}
						
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.wrong-inventory-clicked"));
					}
				} else {
					//Bottom inventory
					
					int spaceSpot = 0;
					int[] checkingSlots = leftSlots;
					if(rightPlayer == player) {
						checkingSlots = rightSlots;
					}
					for(int i : checkingSlots) {
						if(event.getInventory().getItem(i) == null || event.getInventory().getItem(i).getType() == Material.AIR) {
							spaceSpot = i;
							break;
						}
					}
					
					if(spaceSpot > 0) {
						event.getInventory().setItem(spaceSpot, event.getCurrentItem());
						event.setCurrentItem(new ItemStack(Material.AIR));
					} else {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.not-enough-space"));
					}
				}
			}
		} else if (event.getInventory().getName().endsWith("Bank")) {
			if (event.getCurrentItem() != null) {
				if (event.getView().getTopInventory() == event.getClickedInventory()) {
					if ((event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasDisplayName()) && (event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Rupee(s)") || event.getCurrentItem().getItemMeta().getDisplayName().startsWith("$"))) {
						if (event.getClick() == ClickType.LEFT) {
							event.setCancelled(true);

							if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
								RemoveRupeeFromBank(player, event.getCurrentItem());
							}
						} else if (event.getClick() == ClickType.RIGHT) {
							event.setCancelled(true);
							
							if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
								CreateBankNote(player, event.getCurrentItem());
							}
						}
					}
					File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
					YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
					for (int i = 0; i < 7; i++) {
						bankConfig.set(player.getUniqueId().toString() + "." + Integer.toString(i), event.getInventory().getItem(i));
					}
					try {
						bankConfig.save(bankFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if ((event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasDisplayName()) && (event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Rupee"))) {
						event.setCancelled(true);

						if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
							AddRupeeToBank(player, event.getCurrentItem().getAmount());
						} else {
							Plugin.getCore().getEconomy().depositPlayer(player, event.getCurrentItem().getAmount());
						}
						
						event.setCurrentItem(new ItemStack(Material.AIR));
						OpenBank(player);
					}
					if ((event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasLore()) && (event.getCurrentItem().getItemMeta().getLore().toString().contains("Bank Note"))) {
						event.setCancelled(true);

						if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
							AddBankNoteToBank(player, event.getCurrentItem());
						} else {
							int amount = RandomNumbers.getNumbersFromStringSequence(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
							Plugin.getCore().getEconomy().depositPlayer(player, amount);
						}
						
						event.setCurrentItem(new ItemStack(Material.AIR));
						OpenBank(player);
					}
					File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
					YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
					for (int i = 0; i < 7; i++) {
						bankConfig.set(player.getUniqueId().toString() + "." + Integer.toString(i), event.getInventory().getItem(i));
					}
					try {
						bankConfig.save(bankFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (event.getInventory().getName().endsWith("Shop")) {
			String[] splitter = ChatColor.stripColor(event.getInventory().getName()).split("'");
			String name = splitter[0];
			OfflinePlayer owner = Bukkit.getServer().getOfflinePlayer(name);

			File shopFile = new File(Plugin.getCore().getDataFolder(), "shop.yml");
			YamlConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
					event.setCancelled(true);

					if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Receive Rupees:")) {
						receiveRupees(player, event.getCurrentItem(), false);
					} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Delete Shop")) {
						for (Entity e : Plugin.getCore().clickedChest.get(player).getLocation().getChunk().getEntities()) {
							if ((int) e.getLocation().distance(Plugin.getCore().clickedChest.get(player).getLocation()) == 0 && e instanceof ArmorStand) {
								e.remove();
							}
						}

						if (Plugin.getCore().clickedChest.get(player).getLocation().add(0, -2, 0).getBlock().getState() instanceof Sign) {
							Block signBlock = Plugin.getCore().clickedChest.get(player).getLocation().add(0, -2, 0).getBlock();
							signBlock.setType(Material.AIR);
						}

						Plugin.getCore().clickedChest.get(player).getBlock().setType(Material.AIR);
						Plugin.getCore().clickedChest.remove(player);

						receiveRupees(player, event.getCurrentItem(), true);
						event.setCurrentItem(new ItemStack(Material.AIR));
						
						player.closeInventory();
					} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Rename Shop")) {
						AsyncChatEvent.chatState.put(player.getName(), "RENAME_PLAYER_SHOP");

						player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
						player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
						player.sendMessage(ChatColor.YELLOW + "Enter the new name of your shop.");
						player.sendMessage(ChatColor.YELLOW + "You can use color codes (e.g. &6&lMy &2Shop)");
						player.sendMessage(ChatColor.GRAY + "(Type " + ChatColor.ITALIC + "'cancel'" + ChatColor.GRAY + ", to cancel this option)");
						player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
						
						event.setCurrentItem(new ItemStack(Material.AIR));
						
						player.closeInventory();
					}
				}
				if (event.getView().getTopInventory() == event.getClickedInventory()) {
					event.setCancelled(true);
					if (name.equalsIgnoreCase(player.getName())) {
						if ((event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasDisplayName())
								&& ((event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLACK + "[-]")) || (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Receive Rupees:")))) {
							//Do nothing
						} else {
							ItemStack copy = event.getCurrentItem().clone();
							if (copy.hasItemMeta()) {
								ItemMeta meta = copy.getItemMeta();
								if (meta.hasLore()) {
									ArrayList<String> lore = (ArrayList<String>) meta.getLore();
									int size = lore.size();
									lore.remove(size - 1);
									lore.remove(size - 2);
									meta.setLore(lore);
								}
								copy.setItemMeta(meta);
							}
							int emptyChecker = 0;
							for (int i = 0; i < player.getInventory().getSize(); i++) {
								if (player.getInventory().getItem(i) == null) {
									emptyChecker++;
								} else if ((player.getInventory().getItem(i).getType() == copy.getType()) && (player.getInventory().getItem(i).getItemMeta().getDisplayName() == copy.getItemMeta().getDisplayName()) && (player.getInventory().getItem(i).getAmount() + copy.getAmount() <= 64)) {
									emptyChecker++;
								}
							}
							if (emptyChecker > 0) {
								shopConfig.set(owner.getUniqueId().toString() + ".content." + event.getSlot(), null);
								try {
									shopConfig.save(shopFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
								event.getCurrentItem().setType(Material.AIR);
								player.closeInventory();

								player.getInventory().addItem(copy);
							} else {
								player.closeInventory();
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("inventory-inventory-full"));
							}
						}
					} else {
						if ((event.getCurrentItem().hasItemMeta()) && (event.getCurrentItem().getItemMeta().hasDisplayName())
								&& ((event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLACK + "[-]")) || (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Receive Rupees:")))) {
							//Do nothing
						} else {
							int totalRupees = 0;
							int itemPrice = 0;
							int rupeesLeft = 0;
							
							for (ItemStack is : player.getInventory()) {
								if ((is != null) && (is.hasItemMeta()) && (is.getItemMeta().hasDisplayName())) {
									if (is.getItemMeta().getDisplayName().endsWith("Rupee(s)") || is.getItemMeta().getDisplayName().startsWith("$")) {
										String[] spaceSplitter = ChatColor.stripColor(is.getItemMeta().getDisplayName()).split(" ");
										totalRupees = Integer.parseInt(spaceSplitter[0]);
									} else if (is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Rupee")) {
										totalRupees += is.getAmount();
									}
								}
							}
							
							String[] commaLine = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().toString()).split(",");
							String tempPrice = "";
							Character chars;
							for (int i = 0; i < commaLine[event.getCurrentItem().getItemMeta().getLore().size() - 1].length(); i++) {
								chars = Character.valueOf(commaLine[(event.getCurrentItem().getItemMeta().getLore().size() - 1)].charAt(i));
								if (Character.isDigit(chars)) {
									tempPrice = tempPrice + Character.toString(chars);
								}
							}
							
							itemPrice = Integer.parseInt(tempPrice);
							rupeesLeft = itemPrice;
							if (totalRupees >= itemPrice) {
								shopConfig.set(owner.getUniqueId().toString() + ".rupees", Integer.valueOf(shopConfig.getInt(owner.getUniqueId().toString() + ".rupees") + itemPrice));
								try {
									shopConfig.save(shopFile);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								for (int i = 0; i < player.getInventory().getSize(); i++) {
									ItemStack is = player.getInventory().getItem(i);
									if ((is != null) && (is.hasItemMeta()) && (is.getItemMeta().hasDisplayName())) {
										if (is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Rupee")) {
											int currentAmount = is.getAmount();
											if (currentAmount > rupeesLeft) {
												is.setAmount(currentAmount - rupeesLeft);
											} else {
												player.getInventory().setItem(i, new ItemStack(Material.AIR));
											}
											if (is.getAmount() <= 0) {
												player.getInventory().setItem(i, new ItemStack(Material.AIR));
											}
											rupeesLeft -= currentAmount;
											if (rupeesLeft <= 0) {
												ItemStack copy = event.getCurrentItem().clone();
												if (copy.hasItemMeta()) {
													ItemMeta meta = copy.getItemMeta();
													if (copy.getItemMeta().hasLore()) {
														ArrayList<String> lore = (ArrayList<String>) meta.getLore();
														int size = lore.size();
														lore.remove(size - 1);
														lore.remove(size - 2);

														meta.setLore(lore);
													}
													copy.setItemMeta(meta);
												}
												int emptyChecker = 0;
												for (int j = 0; j < player.getInventory().getSize(); j++) {
													if (player.getInventory().getItem(j) == null) {
														emptyChecker++;
													} else if ((player.getInventory().getItem(j).getType() == copy.getType()) && (player.getInventory().getItem(j).getItemMeta().getDisplayName() == copy.getItemMeta().getDisplayName())
															&& (player.getInventory().getItem(j).getAmount() + copy.getAmount() <= 64)) {
														emptyChecker++;
													}
												}
												if (emptyChecker > 0) {
													shopConfig.set(owner.getUniqueId().toString() + ".content." + event.getSlot(), null);
													try {
														shopConfig.save(shopFile);
													} catch (IOException e) {
														e.printStackTrace();
													}
													event.getCurrentItem().setType(Material.AIR);

													player.getInventory().addItem(copy);
													player.closeInventory();
												} else {
													player.closeInventory();
													player.sendMessage(Plugin.getCore().getLangTools().getMessage("inventory.inventory-full"));
												}
												player.closeInventory();
												break;
											}
										} else if (is.getItemMeta().getDisplayName().endsWith("Rupee(s)") || is.getItemMeta().getDisplayName().startsWith("$")) {
											String[] spaceSplitter = ChatColor.stripColor(is.getItemMeta().getDisplayName()).split(" ");
											int currentAmount = Integer.parseInt(spaceSplitter[0]);
											int newAmount = 0;
											if (currentAmount > rupeesLeft) {
												newAmount = currentAmount - rupeesLeft;

												ItemMeta meta = is.getItemMeta();
												
												if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
													meta.setDisplayName(ChatColor.GREEN + "" + newAmount + " Rupee(s)");
												} else {
													meta.setDisplayName(ChatColor.GREEN + "$" + newAmount);
												}
												
												is.setItemMeta(meta);
											} else {
												player.getInventory().setItem(i, new ItemStack(Material.AIR));
											}
											if (newAmount <= 0) {
												player.getInventory().setItem(i, new ItemStack(Material.AIR));
											}
											rupeesLeft -= currentAmount;
											if (rupeesLeft <= 0) {
												ItemStack copy = event.getCurrentItem().clone();
												if (copy.hasItemMeta()) {
													ItemMeta meta = copy.getItemMeta();
													if (copy.getItemMeta().hasLore()) {
														ArrayList<String> lore = (ArrayList<String>) meta.getLore();
														int size = lore.size();
														lore.remove(size - 1);
														lore.remove(size - 2);

														meta.setLore(lore);
													}
													copy.setItemMeta(meta);
												}
												int emptyChecker = 0;
												for (int j = 0; j < player.getInventory().getSize(); j++) {
													if (player.getInventory().getItem(j) == null) {
														emptyChecker++;
													} else if ((player.getInventory().getItem(j).getType() == copy.getType()) && (player.getInventory().getItem(j).getItemMeta().getDisplayName() == copy.getItemMeta().getDisplayName())
															&& (player.getInventory().getItem(j).getAmount() + copy.getAmount() <= 64)) {
														emptyChecker++;
													}
												}
												if (emptyChecker > 0) {
													shopConfig.set(owner.getUniqueId().toString() + ".content." + event.getSlot(), null);
													try {
														shopConfig.save(shopFile);
													} catch (IOException e) {
														e.printStackTrace();
													}
													event.getCurrentItem().setType(Material.AIR);

													player.getInventory().addItem(new ItemStack[] { copy });
													player.closeInventory();
												} else {
													player.closeInventory();
													player.sendMessage(Plugin.getCore().getLangTools().getMessage("inventory.inventory-full"));
												}
												player.closeInventory();
												break;
											}
										}
									}
								}
							} else {
								player.closeInventory();
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.buying.not-enough-rupees"));
							}
						}
					}
				} else if (name.equalsIgnoreCase(player.getName())) {
					AddItemToShop(player, event.getCurrentItem());
				}
			}
		} else if (event.getInventory().getName().startsWith("Chest (")) {
			event.setCancelled(true);

			if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("*")) {
				if (!Plugin.getCore().chestAnswers.containsKey(player)) {
					ArrayList<Integer> answers = new ArrayList<Integer>();
					for (int i = 0; i < 3; i++) {
						answers.add(0);
					}
					Plugin.getCore().chestAnswers.put(player, answers);
				}

				ItemStack newBar = new ItemStack(Material.IRON_BARS);
				ItemMeta newBarMeta = newBar.getItemMeta();
				newBarMeta.setDisplayName(ChatColor.WHITE + "*");
				newBar.setItemMeta(newBarMeta);
				
				if (event.getSlot() == 3 || event.getSlot() == 4 || event.getSlot() == 5) {
					Plugin.getCore().chestAnswers.get(player).set(0, 0);

					event.getClickedInventory().setItem(3, newBar);
					event.getClickedInventory().setItem(4, newBar);
					event.getClickedInventory().setItem(5, newBar);

					newBarMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "*");
					newBarMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					newBar.setItemMeta(newBarMeta);
					newBar.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					event.getClickedInventory().setItem(event.getSlot(), newBar);

					if (event.getSlot() == 3) {
						Plugin.getCore().chestAnswers.get(player).set(0, 1);
					} else if (event.getSlot() == 4) {
						Plugin.getCore().chestAnswers.get(player).set(0, 2);
					} else if (event.getSlot() == 5) {
						Plugin.getCore().chestAnswers.get(player).set(0, 3);
					}
				} else if (event.getSlot() == 12 || event.getSlot() == 13 || event.getSlot() == 14) {
					Plugin.getCore().chestAnswers.get(player).set(1, 0);

					event.getClickedInventory().setItem(12, newBar);
					event.getClickedInventory().setItem(13, newBar);
					event.getClickedInventory().setItem(14, newBar);

					newBarMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "*");
					newBarMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					newBar.setItemMeta(newBarMeta);
					newBar.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					event.getClickedInventory().setItem(event.getSlot(), newBar);

					if (event.getSlot() == 12) {
						Plugin.getCore().chestAnswers.get(player).set(1, 1);
					} else if (event.getSlot() == 13) {
						Plugin.getCore().chestAnswers.get(player).set(1, 2);
					} else if (event.getSlot() == 14) {
						Plugin.getCore().chestAnswers.get(player).set(1, 3);
					}
				} else if (event.getSlot() == 21 || event.getSlot() == 22 || event.getSlot() == 23) {
					Plugin.getCore().chestAnswers.get(player).set(2, 0);

					event.getClickedInventory().setItem(21, newBar);
					event.getClickedInventory().setItem(22, newBar);
					event.getClickedInventory().setItem(23, newBar);

					newBarMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "*");
					newBarMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					newBar.setItemMeta(newBarMeta);
					newBar.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					event.getClickedInventory().setItem(event.getSlot(), newBar);

					if (event.getSlot() == 21) {
						Plugin.getCore().chestAnswers.get(player).set(2, 1);
					} else if (event.getSlot() == 22) {
						Plugin.getCore().chestAnswers.get(player).set(2, 2);
					} else if (event.getSlot() == 23) {
						Plugin.getCore().chestAnswers.get(player).set(2, 3);
					}
				}

				boolean canContinue = false;
				for (int i = 0; i < event.getClickedInventory().getSize() / 9; i++) {
					if (Plugin.getCore().chestAnswers.get(player).get(i) >= 1 && Plugin.getCore().chestAnswers.get(player).get(i) <= 3) {
						canContinue = true;
					} else {
						canContinue = false;
						break;
					}
				}

				if (canContinue) {
					player.closeInventory();

					canContinue = false;
					for (int i = 0; i < event.getClickedInventory().getSize() / 9; i++) {
						if (Plugin.getCore().chestIndexMap.get(player)[Plugin.getCore().chestAnswers.get(player).get(i) + (i * 3)]) {
							canContinue = true;
						} else {
							canContinue = false;
							break;
						}
					}
					Plugin.getCore().chestAnswers.remove(player);

					if (canContinue) {
						player.openInventory(Plugin.getCore().clickedChest.get(player).getBlockInventory());
					} else {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("lockpicking-chest.lockpicking.incorrect-combination"));

						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 10, 2);
						}
					}
				}
			}
		}
	}

	public void AddRupeeToBank(Player player, int amount) {
		player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);

		if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-economy")) {
			File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
			YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
			bankConfig.set(player.getUniqueId().toString() + ".rupees", Integer.valueOf(bankConfig.getInt(player.getUniqueId().toString() + ".rupees") + amount));
			try {
				bankConfig.save(bankFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			player.sendMessage(ChatColor.GREEN + "-" + amount + " Rupee(s)");
		} else {
			Plugin.getCore().getEconomy().depositPlayer(player, amount);
			player.sendMessage(ChatColor.GREEN + "-$" + amount);
		}
	}

	public void AddBankNoteToBank(Player player, ItemStack bankNote) {
		player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);

		int amount = Integer.parseInt(ChatColor.stripColor(bankNote.getItemMeta().getDisplayName().replace(" Rupee(s)", "")));

		File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
		YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
		bankConfig.set(player.getUniqueId().toString() + ".rupees", Integer.valueOf(bankConfig.getInt(player.getUniqueId().toString() + ".rupees") + amount));
		try {
			bankConfig.save(bankFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "-" + amount + " Rupee(s)");
	}

	public void RemoveRupeeFromBank(Player player, ItemStack rupee) {
		player.closeInventory();

		AsyncChatEvent.clickedItem.put(player.getName(), rupee);
		AsyncChatEvent.chatState.put(player.getName(), "REMOVE_RUPEE_FROM_BANK");

		player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
		for(String msgLines : Plugin.getCore().getLangTools().getMessage("shop.chat-phases.remove-rupee-from-bank").trim().split(" ")) {
			player.sendMessage(msgLines);
		}
	}

	public void CreateBankNote(Player player, ItemStack rupee) {
		player.closeInventory();

		AsyncChatEvent.clickedItem.put(player.getName(), rupee);
		AsyncChatEvent.chatState.put(player.getName(), "CREATE_BANK_NOTE");

		player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
		for(String msgLines : Plugin.getCore().getLangTools().getMessage("shop.chat-phases.create-bank-note").trim().split(" ")) {
			player.sendMessage(msgLines);
		}
	}

	public void AddItemToShop(Player player, ItemStack item) {
		player.closeInventory();

		AsyncChatEvent.clickedItem.put(player.getName(), item);
		AsyncChatEvent.chatState.put(player.getName(), "ADD_PLAYER_SHOP_ITEM");

		player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
		for(String msgLines : Plugin.getCore().getLangTools().getMessage("shop.chat-phases.add-item-to-shop").trim().split(" ")) {
			player.sendMessage(msgLines);
		}
	}

	public static void OpenBank(Player player) {
		Inventory bankInv = Bukkit.getServer().createInventory(player, 9, player.getName() + "'s Bank");
		File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
		YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);

		ArrayList<String> bankRupeeLore = new ArrayList<String>();
		if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-economy")) {
			bankRupeeLore.add("");
			bankRupeeLore.add(ChatColor.YELLOW + "Left Click - Draw Rupee");
			bankRupeeLore.add(ChatColor.YELLOW + "Right Click - Bank Note");
		}
		
		if (bankConfig.isConfigurationSection(player.getUniqueId().toString())) {
			for (int i = 0; i < 7; i++) {
				if (bankConfig.isSet(player.getUniqueId().toString() + "." + i)) {
					String iString = Integer.toString(i);
					bankInv.setItem(i, bankConfig.getItemStack(player.getUniqueId().toString() + "." + iString));
				}
			}
			ItemStack rupee = new ItemStack(Material.EMERALD, 1);
			ItemMeta rupeeMeta = rupee.getItemMeta();
			
			if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-economy")) {
				rupeeMeta.setDisplayName("" + ChatColor.GREEN + bankConfig.getInt(player.getUniqueId().toString() + ".rupees") + " Rupee(s)");
			} else {
				rupeeMeta.setDisplayName("" + ChatColor.GREEN + "$" + bankConfig.getInt(player.getUniqueId().toString() + ".rupees"));
			}
			
			rupeeMeta.setLore(bankRupeeLore);
			rupee.setItemMeta(rupeeMeta);
			bankInv.setItem(8, rupee);
		}
		player.openInventory(bankInv);
	}

	public static void OpenShop(OfflinePlayer owner, Player player) {
		File shopFile = new File(Plugin.getCore().getDataFolder(), "shop.yml");
		YamlConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);

		int shopRows = shopConfig.getInt(owner.getUniqueId().toString() + ".rows") * 9;

		Inventory shopInv = Bukkit.getServer().createInventory(player, shopRows, owner.getName() + "'s Shop");
		if (owner.getName() == player.getName()) {
			shopInv = Bukkit.getServer().createInventory(player, shopRows + 9, owner.getName() + "'s Shop");
		}
		int rowsToCheck = shopRows - 1;
		if (owner.getName() == player.getName()) {
			rowsToCheck -= 9;
		}
		if (shopConfig.isConfigurationSection(owner.getUniqueId().toString())) {
			for (int i = 0; i < rowsToCheck; i++) {
				if (shopConfig.isSet(owner.getUniqueId().toString() + ".content." + i)) {
					shopInv.setItem(i, shopConfig.getItemStack(owner.getUniqueId().toString() + ".content." + i));
				}
			}
		}
		if (owner.getName() == player.getName()) {
			ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			ItemStack receiver = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);

			ItemMeta emptyMeta = empty.getItemMeta();
			ItemMeta receiverMeta = receiver.getItemMeta();

			emptyMeta.setDisplayName(ChatColor.BLACK + "[-]");
			
			if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-economy")) {
				receiverMeta.setDisplayName(ChatColor.GOLD + "Receive Rupees: " + ChatColor.GREEN + shopConfig.getInt(new StringBuilder(String.valueOf(owner.getUniqueId().toString())).append(".rupees").toString()));
			} else {
				receiverMeta.setDisplayName(ChatColor.GOLD + "Receive Cash: " + ChatColor.GREEN + shopConfig.getInt(new StringBuilder(String.valueOf(owner.getUniqueId().toString())).append(".rupees").toString()));
			}

			empty.setItemMeta(emptyMeta);
			receiver.setItemMeta(receiverMeta);

			shopInv.setItem(shopRows + 0, empty);
			shopInv.setItem(shopRows + 1, empty);
			shopInv.setItem(shopRows + 2, empty);
			shopInv.setItem(shopRows + 3, empty);
			shopInv.setItem(shopRows + 4, receiver);
			shopInv.setItem(shopRows + 5, empty);
			shopInv.setItem(shopRows + 6, empty);
			shopInv.setItem(shopRows + 7, empty);
			shopInv.setItem(shopRows + 8, empty);
		}
		player.openInventory(shopInv);
	}

	public void openSmithingInventory(Player player) {
		Inventory smithing = Bukkit.getServer().createInventory(player, 9, ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("smithing.smithing-window")));

		ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		ItemStack furnace = new ItemStack(Material.FURNACE);
		ItemStack armor = new ItemStack(Material.BOOK);
		ItemStack weapon = new ItemStack(Material.BOOK);
		ItemStack gunSmithing = new ItemStack(Material.GUNPOWDER);

		ItemMeta borderMeta = border.getItemMeta();
		ItemMeta furnaceMeta = furnace.getItemMeta();
		ItemMeta armorMeta = armor.getItemMeta();
		ItemMeta weaponMeta = weapon.getItemMeta();
		ItemMeta gunSmithingMeta = gunSmithing.getItemMeta();

		ArrayList<String> furnaceLore = new ArrayList<String>();
		furnaceLore.add(ChatColor.GRAY + "Click Ore");

		furnaceMeta.setLore(furnaceLore);

		borderMeta.setDisplayName(ChatColor.BLACK + "[-]");
		furnaceMeta.setDisplayName(ChatColor.WHITE + "Smelt");
		armorMeta.setDisplayName(ChatColor.WHITE + "Armor");
		weaponMeta.setDisplayName(ChatColor.WHITE + "Weapon");
		gunSmithingMeta.setDisplayName(ChatColor.WHITE + "Gun Smithing");

		border.setItemMeta(borderMeta);
		furnace.setItemMeta(furnaceMeta);
		armor.setItemMeta(armorMeta);
		weapon.setItemMeta(weaponMeta);
		gunSmithing.setItemMeta(gunSmithingMeta);

		smithing.setItem(0, furnace);
		smithing.setItem(3, armor);
		smithing.setItem(4, weapon);
		smithing.setItem(7, gunSmithing);
		for (int i = 0; i < smithing.getSize(); i++) {
			if (smithing.getItem(i) == null) {
				smithing.setItem(i, border);
			}
		}
		player.openInventory(smithing);
	}

	private void receiveRupees(Player player, ItemStack button, boolean autoClose) {
		String tempReceiver = "";
		String displayName = ChatColor.stripColor(button.getItemMeta().getDisplayName());
		for (int i = 0; i < displayName.length(); i++) {
			Character chars = Character.valueOf(displayName.charAt(i));
			if (Character.isDigit(chars.charValue())) {
				tempReceiver = tempReceiver + chars;
			}
		}
		int receivedRupees = 0;
		if (!tempReceiver.equalsIgnoreCase("")) {
			receivedRupees = Integer.parseInt(tempReceiver);
		}

		if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-economy")) {
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "Commonly used among trading");

			ItemStack rupee = new ItemStack(Material.EMERALD, receivedRupees);
			ItemMeta rupeeMeta = rupee.getItemMeta();
			rupeeMeta.setDisplayName(ChatColor.GREEN + "Rupee");
			rupeeMeta.setLore(lore);
			rupee.setItemMeta(rupeeMeta);
			
			if (rupee.getAmount() <= 0) {
				if (!autoClose) {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.personal-shop.receiving-rupees.no-rupees"));
				}
			} else {
				player.getInventory().addItem(rupee);
			}
		} else {
			Plugin.getCore().getEconomy().depositPlayer(player, receivedRupees);
		}

		player.closeInventory();
	}
	
	private void finishTrade(Player leftPlayer, Player rightPlayer) {
		InventoryClose._invClose.put(leftPlayer, "TRADE_CONFIRM");
		InventoryClose._invClose.put(rightPlayer, "TRADE_CONFIRM");
		
		Inventory inv = leftPlayer.getOpenInventory().getTopInventory();
		
		int[] leftSlots = {1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39};
		int[] rightSlots = {5, 6, 7, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44};
		
		ArrayList<ItemStack> leftItems = new ArrayList<ItemStack>();
		ArrayList<ItemStack> rightItems = new ArrayList<ItemStack>();
		
		for(int i : leftSlots) {
			if(inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
				leftItems.add(inv.getItem(i));
			}
		}
		for(int i : rightSlots) {
			if(inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
				rightItems.add(inv.getItem(i));
			}
		}
		
		int leftPlayerSpace = 0;
		int rightPlayerSpace = 0;
		
		for(int i=0;i<leftPlayer.getInventory().getSize();i++) {
			if(leftPlayer.getInventory().getItem(i) == null || leftPlayer.getInventory().getItem(i).getType() == Material.AIR) {
				leftPlayerSpace++;
			}
		}
		for(int i=0;i<rightPlayer.getInventory().getSize();i++) {
			if(rightPlayer.getInventory().getItem(i) == null || rightPlayer.getInventory().getItem(i).getType() == Material.AIR) {
				rightPlayerSpace++;
			}
		}
		
		if(leftPlayerSpace < rightItems.size() || rightPlayerSpace < leftItems.size()) {
			leftPlayer.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.not-enough-space"));
			rightPlayer.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.not-enough-space"));
			
			if(leftItems.size() > 0) {
				for(ItemStack is : leftItems) {
					leftPlayer.getInventory().addItem(is);
				}
			}
			if(rightItems.size() > 0) {
				for(ItemStack is : rightItems) {
					rightPlayer.getInventory().addItem(is);
				}
			}
			
			leftPlayer.closeInventory();
			return;
		} else {
			leftPlayer.playSound(leftPlayer.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
			rightPlayer.playSound(rightPlayer.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
			
			if(leftItems.size() > 0) {
				for(ItemStack is : leftItems) {
					rightPlayer.getInventory().addItem(is);
				}
			}
			if(rightItems.size() > 0) {
				for(ItemStack is : rightItems) {
					leftPlayer.getInventory().addItem(is);
				}
			}
			
			Plugin.getCore().inTrade.remove(leftPlayer);
			Plugin.getCore().inTrade.remove(rightPlayer);
			
			leftPlayer.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.success").replace("%player%", rightPlayer.getName()));
			rightPlayer.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.success").replace("%player%", leftPlayer.getName()));
			
			leftPlayer.closeInventory();
			rightPlayer.closeInventory();
		}
	}
}