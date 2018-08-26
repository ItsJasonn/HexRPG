package me.ItsJasonn.HexRPG.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class Tools implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Tools")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp() || player.hasPermission("hexrpg.tools")) {
					if (args.length == 0) {
						Inventory inv = Bukkit.getServer().createInventory(player, 9, Plugin.getCore().getLangTools().getUncoloredMessage("navigation.menu-names.tools"));
						
						ItemStack hammer = new ItemStack(Material.IRON_HOE, 1, (short) 21);
						ItemStack hoe = new ItemStack(Material.IRON_HOE, 1);
						ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE, 1);
						ItemStack rod = new ItemStack(Material.FISHING_ROD, 1);
						ItemStack lockpick = new ItemStack(Material.FLINT_AND_STEEL, 1);
						
						for(ItemStack tool : new ItemStack[] { hammer, hoe, pickaxe, rod, lockpick }) {
							ItemMeta toolMeta = tool.getItemMeta();
							
							if(tool.equals(hammer)) {
								toolMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.hammer"));
							} else if(tool.equals(hoe)) {
								toolMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.hoe"));
							} else if(tool.equals(pickaxe)) {
								toolMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.pickaxe"));
							} else if(tool.equals(rod)) {
								toolMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.fishing-rod"));
							} else if(tool.equals(lockpick)) {
								toolMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.lockpick"));
							}
							
							toolMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
							toolMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
							toolMeta.setUnbreakable(true);
							
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(Plugin.getCore().getLangTools().getMessage("navigation.translations.click-to-retrieve"));
							toolMeta.setLore(lore);
							
							tool.setItemMeta(toolMeta);
						}
						
						inv.setItem(2, hammer);
						inv.setItem(3, hoe);
						inv.setItem(4, pickaxe);
						inv.setItem(5, rod);
						inv.setItem(6, lockpick);
						
						player.openInventory(inv);
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