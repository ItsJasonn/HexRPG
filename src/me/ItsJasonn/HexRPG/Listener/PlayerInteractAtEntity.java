package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class PlayerInteractAtEntity implements Listener {
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		if((event.getRightClicked() instanceof Player)) {
			if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.player-menu")) {
				Player clickedPlayer = (Player) event.getRightClicked();
				
				if(clickedPlayer.hasMetadata("NPC")) {
					return;
				}

				if(!Plugin.getCore().inDual.containsKey(player)) {
					if(!Plugin.getCore().inTrade.containsKey(player)) {
						if(Plugin.getCore().inDual.containsKey(clickedPlayer)) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.target-in-trade").replace("%player%", clickedPlayer.getName()));
							return;
						}
						if(Plugin.getCore().inTrade.containsKey(clickedPlayer)) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.target-in-duel").replace("%player%", clickedPlayer.getName()));
							return;
						}

						Inventory inv = Bukkit.getServer().createInventory(player, 9, "Player Menu - " + clickedPlayer.getName());

						ItemStack trade = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
						ItemStack dual = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
						ItemStack steal = new ItemStack(Material.RED_STAINED_GLASS_PANE);

						ItemMeta tradeMeta = trade.getItemMeta();
						ItemMeta dualMeta = dual.getItemMeta();
						ItemMeta stealMeta = steal.getItemMeta();

						tradeMeta.setDisplayName(ChatColor.GREEN + "Trade");
						dualMeta.setDisplayName(ChatColor.GOLD + "Dual");
						stealMeta.setDisplayName(ChatColor.RED + "Steal");

						trade.setItemMeta(tradeMeta);
						dual.setItemMeta(dualMeta);
						steal.setItemMeta(stealMeta);

						ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
						ItemMeta borderMeta = border.getItemMeta();
						borderMeta.setDisplayName(ChatColor.BLACK + "[-]");
						border.setItemMeta(borderMeta);

						inv.setItem(3, trade);
						inv.setItem(4, dual);
						inv.setItem(5, steal);
						for (int i = 0; i < inv.getSize(); i++) {
							if(inv.getItem(i) == null) {
								inv.setItem(i, border);
							}
						}

						player.openInventory(inv);
					} else {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.player-in-trade").replace("%player%", clickedPlayer.getName()));
					}
				} else {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("player-menu.trading.trade.player-in-duel").replace("%player%", clickedPlayer.getName()));
				}
			}
		} else {
			if(player.isOp() && player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD && !player.getInventory().getItemInMainHand().hasItemMeta()) {
				event.getRightClicked().remove();
			}
		}
	}
}
