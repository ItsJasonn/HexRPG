package me.ItsJasonn.HexRPG.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class Items implements CommandExecutor, Listener {
	private static HashMap<Integer, ArrayList<ItemStack>> itemPages = new HashMap<Integer, ArrayList<ItemStack>>();
	private static HashMap<UUID, Integer> playersPage = new HashMap<UUID, Integer>();
	
	@SuppressWarnings({ "static-access" })
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		
		if(command.equalsIgnoreCase("Items")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.isOp() || player.hasPermission("hexrpg.items")) {
					if(args.length == 0) {
						if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
							itemPages = new HashMap<Integer, ArrayList<ItemStack>>();
							
							int currentPage = 0;
							for(int i=0;i<Plugin.getCore().itemList.size();i++) {
								ItemStack is = Plugin.getCore().itemList.get(i);
								
								ArrayList<ItemStack> itemsOnPage = new ArrayList<ItemStack>();
								if(!itemPages.isEmpty() && itemPages.get(currentPage).size() <= 36) {
									itemsOnPage = itemPages.get(currentPage);
								} else {
									currentPage++;
								}
								
								ItemMeta isMeta = is.getItemMeta();
								isMeta.setDisplayName(ChatColor.GOLD + Plugin.getCore().getStatsManager().getRPGName(is));
								
								isMeta.setUnbreakable(true);
								
								isMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
								isMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
								
								ArrayList<String> lore = new ArrayList<String>();
								lore.add(ChatColor.GRAY + "Left-click to get the item with random stats");
								lore.add(ChatColor.GRAY + "Right-click to get the unidentified item");
								isMeta.setLore(lore);
								
								is.setItemMeta(isMeta);
								
								itemsOnPage.add(is);
								itemPages.put(currentPage, itemsOnPage);
							}
							
							openItemPage(player, 1);
						} else {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.command-disabled"));
						}
					} else if(args.length >= 1) {
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
	
	@EventHandler
	public void onPlayerIntentoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(ChatColor.stripColor(event.getInventory().getTitle()).equals(ChatColor.stripColor(Plugin.getCore().getLangTools().getMessage("navigation.menu-names.items")))) {
			event.setCancelled(true);
			
			if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
				return;
			}
			
			String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
			
			if(itemName.equals(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"))) {
				openItemPage(player, playersPage.get(player.getUniqueId()) - 1);
			} else if(itemName.equals(Plugin.getCore().getLangTools().getMessage("navigation.next-page"))) {
				openItemPage(player, playersPage.get(player.getUniqueId()) + 1);
			} else if(!event.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE)) {
				ItemStack item = event.getCurrentItem();
				if(event.getClick() == ClickType.LEFT) {
					item = Plugin.getCore().getStatsManager().checkStats(item, 1, "none");
				} else if(event.getClick() == ClickType.RIGHT) {
					Plugin.getCore().getStatsManager().setUnidentified(item);
				}
				
				player.getInventory().addItem(item);
				player.closeInventory();
			}
		}
	}
	
	private void openItemPage(Player player, int page) {
		playersPage.put(player.getUniqueId(), page);
		
		Inventory inv = Bukkit.getServer().createInventory(player, 45, Plugin.getCore().getLangTools().getMessage("navigation.menu-names.items"));
		
		if(itemPages.isEmpty()) {
			player.openInventory(inv);
			return;
		}
		
		for(int i=0;i<inv.getSize() - 9;i++) {
			if(itemPages.get(page).size() > i) {
				inv.setItem(i, itemPages.get(page).get(i));
			} else {
				ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
				ItemMeta paneMeta = pane.getItemMeta();
				paneMeta.setDisplayName(ChatColor.WHITE + "");
				pane.setItemMeta(paneMeta);
				
				inv.setItem(i, pane);
			}
		}
		
		ItemStack nextPage = new ItemStack(Material.ARROW, 1);
		ItemStack previousPage = new ItemStack(Material.ARROW, 1);
		
		ItemMeta nextPageMeta = nextPage.getItemMeta();
		ItemMeta previousPageMeta = previousPage.getItemMeta();
		
		nextPageMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.next-page"));
		previousPageMeta.setDisplayName(Plugin.getCore().getLangTools().getMessage("navigation.previous-page"));
		
		nextPage.setItemMeta(nextPageMeta);
		previousPage.setItemMeta(previousPageMeta);
		
		if(page > 1) {
			inv.setItem(39, previousPage);
		}
		if(page < itemPages.size()) {
			inv.setItem(41, nextPage);
		}
		
		player.openInventory(inv);
	}
}