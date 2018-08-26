package me.ItsJasonn.HexRPG.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Instances.HexRPGBackpack;
import me.ItsJasonn.HexRPG.Main.Plugin;

public class Backpack implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandSender, String[] args) {
		String command = cmd.getName();
		
		if(command.equalsIgnoreCase("Backpack")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.isOp() || player.hasPermission("hexrpg.backpack")) {
					if(args.length == 0) {
						HexRPGBackpack backpack = new HexRPGBackpack(player);
						if(!backpack.hasBackpack()) {
							backpack.generateBackpack();
						}
						
						backpack.openBackpack();
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