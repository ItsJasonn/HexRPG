package me.ItsJasonn.HexRPG.Listener.onInventoryClick;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class ScrollApplying implements Listener {
	@EventHandler
	public void onInventoryClickEvent(final InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		
		if(event.getInventory().getType().equals(InventoryType.ANVIL)) {
			return;
		}
		
		if(event.getCursor() != null && event.getCursor().hasItemMeta() && event.getCursor().getItemMeta().hasDisplayName() && event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
			YamlConfiguration scrollsConfig = new SubConfig(SubConfig.TYPES.SCROLLS).getConfig();
			
			ItemStack scroll = event.getCursor();
			ItemStack item = event.getCurrentItem();
			
			String itemName = ChatColor.stripColor(event.getCursor().getItemMeta().getDisplayName());
			
			if(Plugin.getCore().getStatsManager().isScroll(scroll)) {
				if(Plugin.getCore().getStatsManager().isScroll(item)) {
					return;
				}
				
				String scrollType = itemName.substring(0, itemName.lastIndexOf(" ")).toLowerCase().replace(" ", "-");
				
				double successRate = scrollsConfig.getDouble("success." + scrollType);
				double destroyRate = scrollsConfig.getDouble("destroy." + scrollType);
				
				if(event.getCursor().getAmount() > 1) {
					player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.one-scroll-only"));
					event.setCancelled(false);
				} else {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
						public void run() {
							Random random = new Random();
							int r = random.nextInt(100);
							
							event.setCurrentItem(item);
							player.setItemOnCursor(scroll);
							
							if(r < successRate) {
								if((scrollType.equalsIgnoreCase("IDENTIFICATION") || scrollType.equalsIgnoreCase("POWERFUL-IDENTIFICATION")) && Plugin.getCore().getStatsManager().isUnidentified(event.getCurrentItem())) {
									event.setCurrentItem(Plugin.getCore().getStatsManager().checkStats(event.getCurrentItem(), 1, "none"));
								} else if(scrollType.equalsIgnoreCase("ANCIENT") && !Plugin.getCore().getStatsManager().isUnidentified(event.getCurrentItem())) {
									if(!Plugin.getCore().getStatsManager().isWeapon(item)) {
										player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.item-is-not-weapon"));
										return;
									}
									
									event.setCurrentItem(Plugin.getCore().getStatsManager().applyAncientScroll(player, event.getCurrentItem()));
								} else if(scrollType.equalsIgnoreCase("CHAOS") && !Plugin.getCore().getStatsManager().isUnidentified(event.getCurrentItem())) {
									event.setCurrentItem(Plugin.getCore().getStatsManager().applyChaosScroll(player, event.getCurrentItem()));
								} else if(scrollType.equalsIgnoreCase("PRIME") && !Plugin.getCore().getStatsManager().isUnidentified(event.getCurrentItem())) {
									if(!Plugin.getCore().getStatsManager().isWeapon(item)) {
										player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.item-is-not-weapon"));
										return;
									}
									
									event.setCurrentItem(Plugin.getCore().getStatsManager().applyPrimeScroll(player, event.getCurrentItem()));
								} else if(scrollType.equalsIgnoreCase("DARK") && !Plugin.getCore().getStatsManager().isUnidentified(event.getCurrentItem())) {
									if(!Plugin.getCore().getStatsManager().isArmor(item)) {
										player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.item-is-not-armor"));
										return;
									}
									
									event.setCurrentItem(Plugin.getCore().getStatsManager().applyDarkScroll(player, event.getCurrentItem()));
								}
								
								player.setItemOnCursor(new ItemStack(Material.AIR));
								
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.success"));
								for (Player players : Bukkit.getOnlinePlayers()) {
									players.playEffect(player.getEyeLocation(), Effect.STEP_SOUND, Material.IRON_BLOCK);
									players.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 2.0F);
								}
							} else {
								if((scrollType.equalsIgnoreCase("ANCIENT") || scrollType.equalsIgnoreCase("PRIME")) && !Plugin.getCore().getStatsManager().isUnidentified(event.getCurrentItem())) {
									if(!Plugin.getCore().getStatsManager().isWeapon(item)) {
										player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.item-is-not-weapon"));
										event.setCancelled(false);
										return;
									}
								}
								
								if(scrollType.equalsIgnoreCase("DARK") && !Plugin.getCore().getStatsManager().isUnidentified(event.getCurrentItem())) {
									if(!Plugin.getCore().getStatsManager().isArmor(item)) {
										player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.item-is-not-armor"));
										event.setCancelled(false);
										return;
									}
								}
								
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.drag-and-drop.failed"));
								
								player.setItemOnCursor(new ItemStack(Material.AIR));
								
								if(destroyRate > 0) {
									int rDestroy = random.nextInt(100);
									
									if(rDestroy < destroyRate) {
										event.setCurrentItem(new ItemStack(Material.AIR));
										
										for(Player players : Bukkit.getOnlinePlayers()) {
											players.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10.0F, 1);
										}
									} else {
										event.setCurrentItem(item);
									}
								}
								
								for(Player players : Bukkit.getOnlinePlayers()) {
									players.playEffect(player.getEyeLocation(), Effect.STEP_SOUND, Material.OBSIDIAN);
								}
							}
						}
					}, 1);
				}
			}
		}
	}
}