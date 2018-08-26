package me.ItsJasonn.HexRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class Smith implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		
		if(command.equalsIgnoreCase("Smith")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.isOp() || player.hasPermission("hexrpg.smith")) {
					if(args.length == 0) {
						if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools") && new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.smithing-window")) {
							if(player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() == Material.IRON_HOE && player.getInventory().getItemInMainHand().getDurability() == 21) {
								// TODO: Make the smithing window customizable using the config
								player.sendMessage(ChatColor.YELLOW + "This feature is still under development!");
								
								//SmithingWindow.openSmithingInventory(player);
							} else {
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("smithing.no-hammer-equipped"));
							}
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
}
