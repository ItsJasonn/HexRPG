package me.ItsJasonn.HexRPG.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class AsyncChatEvent implements Listener {
	public static HashMap<String, ItemStack> clickedItem = new HashMap<String, ItemStack>();
	public static HashMap<String, String> chatState = new HashMap<String, String>();

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		String msg = event.getMessage();
		if(clickedItem.containsKey(player.getName()) && chatState.get(player.getName()).equalsIgnoreCase("REMOVE_RUPEE_FROM_BANK")) {
			event.setCancelled(true);
			if(event.getMessage().contains("cancel")) {
				clickedItem.remove(player.getName());
				chatState.remove(player.getName());

				player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
				player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.transfer-cancelled"));
			} else if(Plugin.getCore().isInt(event.getMessage())) {
				int amount = Integer.parseInt(event.getMessage());

				int emptyChecker = 0;
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if(player.getInventory().getItem(i) == null) {
						emptyChecker++;
					} else if((player.getInventory().getItem(i).getType() == ((ItemStack) clickedItem.get(player.getName())).getType()) && (player.getInventory().getItem(i).getItemMeta().getDisplayName() == ((ItemStack) clickedItem.get(player.getName())).getItemMeta().getDisplayName())
							&& (player.getInventory().getItem(i).getAmount() + amount <= 64)) {
						emptyChecker++;
					}
				}
				if(emptyChecker > 0) {
					if(amount < 1) {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.amount-too-low"));
						return;
					}
					int rupeeAmount = Integer.parseInt(ChatColor.stripColor(((ItemStack) clickedItem.get(player.getName())).getItemMeta().getDisplayName().replace(" Rupee(s)", "").replace("$", "")));
					if(rupeeAmount < amount) {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.amount-too-high"));
						return;
					}
					
					if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
						File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
						YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
						bankConfig.set(player.getUniqueId().toString() + ".rupees", Integer.valueOf(bankConfig.getInt(player.getUniqueId().toString() + ".rupees") - amount));
						
						try {
							bankConfig.save(bankFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if(Plugin.getCore().getConfig().getBoolean("inventory.show-message-on-rupee-pickup")) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("inventory.rupee-pickup-message").replace("{AMOUNT}", "" + amount)));
					}
					if(Plugin.getCore().getConfig().getBoolean("inventory.rupee-pickup-sound")) {
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 2);
					}

					ArrayList<String> newRupeeLore = new ArrayList<String>();
					newRupeeLore.add(ChatColor.WHITE + "Commonly used among trading");

					ItemStack newRupee = new ItemStack(Material.EMERALD, amount);
					ItemMeta newRupeeMeta = newRupee.getItemMeta();
					newRupeeMeta.setDisplayName(ChatColor.GREEN + "Rupee");
					newRupeeMeta.setLore(newRupeeLore);
					newRupee.setItemMeta(newRupeeMeta);

					player.getInventory().addItem(newRupee);

					clickedItem.remove(player.getName());
					chatState.remove(player.getName());

					InventoryClick.OpenBank(player);
				} else {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("inventory.inventory-full"));

					clickedItem.remove(player.getName());
					chatState.remove(player.getName());
				}
			} else {
				player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.invalid-amount"));
				player.sendMessage(ChatColor.GRAY + "(Type " + ChatColor.ITALIC + "'cancel'" + ChatColor.GRAY + ", to cancel the current action)");
			}
		} else if(chatState.containsKey(player.getName())) {
			event.setCancelled(true);
			if(((String) chatState.get(player.getName())).equalsIgnoreCase("CREATE_BANK_NOTE")) {
				if(event.getMessage().contains("cancel")) {
					clickedItem.remove(player.getName());
					chatState.remove(player.getName());

					player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.bank-note-transfer-cancelled"));
				} else if(Plugin.getCore().isInt(event.getMessage())) {
					int amount = Integer.parseInt(event.getMessage());

					int emptyChecker = 0;
					for (int i = 0; i < player.getInventory().getSize(); i++) {
						if(player.getInventory().getItem(i) == null) {
							emptyChecker++;
						} else if((player.getInventory().getItem(i).getType() == ((ItemStack) clickedItem.get(player.getName())).getType()) && (player.getInventory().getItem(i).getItemMeta().getDisplayName() == ((ItemStack) clickedItem.get(player.getName())).getItemMeta().getDisplayName())) {
							emptyChecker++;
						}
					}
					if(emptyChecker > 0) {
						if(amount < 1) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.amount-too-low"));
							return;
						}
						int rupeeAmount = Integer.parseInt(ChatColor.stripColor(((ItemStack) clickedItem.get(player.getName())).getItemMeta().getDisplayName().replace(" Rupee(s)", "").replace("$", "")));
						if(rupeeAmount < amount) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.amount-too-high"));
							return;
						}
						ArrayList<String> paperLore = new ArrayList<String>();
						paperLore.add(ChatColor.GRAY + "Bank Note");

						ItemStack paper = new ItemStack(Material.PAPER, 1);
						ItemMeta paperMeta = paper.getItemMeta();
						paperMeta.setDisplayName("" + ChatColor.GREEN + amount + " Rupee(s)");
						paperMeta.setLore(paperLore);
						paper.setItemMeta(paperMeta);
						player.getInventory().addItem(new ItemStack[] { paper });

						File bankFile = new File(Plugin.getCore().getDataFolder() + "/dat0/", "bank.yml");
						YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
						bankConfig.set(player.getUniqueId().toString() + ".rupees", Integer.valueOf(bankConfig.getInt(player.getUniqueId().toString() + ".rupees") - amount));
						try {
							bankConfig.save(bankFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						InventoryClick.OpenBank(player);

						clickedItem.remove(player.getName());
						chatState.remove(player.getName());
					} else {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("inventory.inventory-full"));

						clickedItem.remove(player.getName());
						chatState.remove(player.getName());
					}
				} else {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.rupee-transfer.invalid-amount"));
					player.sendMessage(ChatColor.GRAY + "(Type " + ChatColor.ITALIC + "'cancel'" + ChatColor.GRAY + ", to cancel the current action)");
				}
			} else if(((String) chatState.get(player.getName())).equalsIgnoreCase("ADD_PLAYER_SHOP_ITEM")) {
				if(event.getMessage().contains("cancel")) {
					chatState.remove(player.getName());
					chatState.remove(player.getName());

					player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.personal-shop.item-transfer.cancelled"));
				} else if(Plugin.getCore().isInt(event.getMessage())) {
					int price = Integer.parseInt(event.getMessage());

					ItemStack copy = ((ItemStack) clickedItem.get(player.getName())).clone();
					ItemMeta meta = copy.getItemMeta();

					ArrayList<String> lore = new ArrayList<String>();
					if(meta.hasLore()) {
						lore = (ArrayList<String>) meta.getLore();
					}
					lore.add("");
					
					if(!Plugin.getCore().getConfig().getBoolean("economy.use-essentials-money")) {
						lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Price: " + price + ChatColor.GREEN + "R");
					} else {
						lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Price: $" + price);
					}
					
					meta.setLore(lore);
					copy.setItemMeta(meta);

					File shopFile = new File(Plugin.getCore().getDataFolder(), "shop.yml");
					YamlConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
					int spot = 0;
					for (int i = 0; i < shopConfig.getInt(player.getUniqueId().toString() + ".rows") * 9; i++) {
						if(!shopConfig.isItemStack(player.getUniqueId().toString() + ".content." + i)) {
							spot = i;
							break;
						}
					}
					shopConfig.set(player.getUniqueId().toString() + ".content." + spot, copy);
					try {
						shopConfig.save(shopFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					player.getInventory().remove((ItemStack) clickedItem.get(player.getName()));
					clickedItem.remove(player.getName());
					chatState.remove(player.getName());

					player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.personal-shop.item-transfer.added"));
					player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);
				} else {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.personal-shop.item-transfer.invalid-amount"));
					player.sendMessage(ChatColor.GRAY + "(Type " + ChatColor.ITALIC + "'cancel'" + ChatColor.GRAY + ", to cancel the current action)");
				}
			} else if(chatState.get(player.getName()).equalsIgnoreCase("RENAME_PLAYER_SHOP")) {
				if(event.getMessage().contains("cancel")) {
					chatState.remove(player.getName());
					chatState.remove(player.getName());

					player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0F, 1.0F);
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.personal-shop.renaming.cancelled"));
				} else {
					for (Entity e : Plugin.getCore().clickedChest.get(player).getLocation().getChunk().getEntities()) {
						if((int) e.getLocation().distance(Plugin.getCore().clickedChest.get(player).getLocation()) == 0) {
							e.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + "[S] » " + ChatColor.GOLD + "" + ChatColor.translateAlternateColorCodes('&', msg));
						}
					}

					chatState.remove(player.getName());

					player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.personal-shop.renaming.renamed").replace("%shop_name%", ChatColor.translateAlternateColorCodes('&', msg)));
				}
			}
		} else if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.chat")) {
			event.setCancelled(true);
			if(event.getMessage().startsWith(Plugin.getCore().getConfig().getString("chat.function-characters.global"))) {
				String message = event.getMessage().substring(Plugin.getCore().getConfig().getString("chat.function-characters.global").length());
				
				for(Player players : Bukkit.getOnlinePlayers()) {
					players.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("chat.format.global").replace("%player%", player.getName()).replace("%message%", message)));
				}
			} else if(event.getMessage().startsWith(Plugin.getCore().getConfig().getString("chat.function-characters.close-range"))) {
				String message = event.getMessage().substring(Plugin.getCore().getConfig().getString("chat.function-characters.close-range").length());
				
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("chat.format.close-range").replace("%player%", player.getName()).replace("%message%", message)));
				
				for(Entity e : player.getNearbyEntities(Plugin.getCore().getConfig().getInt("chat.close-range-radius.x"), Plugin.getCore().getConfig().getInt("chat.close-range-radius.y"), Plugin.getCore().getConfig().getInt("chat.close-range-radius.z"))) {
					if(e instanceof Player) {
						((Player) e).sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("chat.format.close-range").replace("%player%", player.getName()).replace("%message%", message)));
					}
				}
			} else if(event.getMessage().startsWith(Plugin.getCore().getConfig().getString("chat.function-characters.sell"))) {
				String message = event.getMessage().substring(Plugin.getCore().getConfig().getString("chat.function-characters.sell").length());
				
				for(Player players : Bukkit.getOnlinePlayers()) {
					players.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("chat.format.sell").replace("%player%", player.getName()).replace("%message%", message)));
				}
			} else if(event.getMessage().startsWith(Plugin.getCore().getConfig().getString("chat.function-characters.buy"))) {
				String message = event.getMessage().substring(Plugin.getCore().getConfig().getString("chat.function-characters.buy").length());
				
				for(Player players : Bukkit.getOnlinePlayers()) {
					players.sendMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("chat.format.buy").replace("%player%", player.getName()).replace("%message%", message)));
				}
			}
		}
	}
}