package me.ItsJasonn.HexRPG.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.RandomLib.RandomNumbers;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;

public class Leveling implements CommandExecutor {
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Leveling")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.isOp() || player.hasPermission("hexrpg.leveling")) {
					if(args.length == 0) {
						player.sendMessage(ChatColor.YELLOW + "/Leveling " + ChatColor.GOLD + "- Popup this help page.");
						player.sendMessage(ChatColor.YELLOW + "/Leveling SetExp [Amount] <Player> " + ChatColor.GOLD + "- Set the amount of experience");
						player.sendMessage(ChatColor.YELLOW + "/Leveling SetLevel [Level] <Player> " + ChatColor.GOLD + "- Set the level");
					} else if(args.length == 1) {
						if(args[0].equalsIgnoreCase("SetExp") || args[0].equalsIgnoreCase("SetLevel")) {
							player.sendMessage(ChatColor.YELLOW + "/Leveling SetExp [Amount] <Player> " + ChatColor.GOLD + "- Set the amount of experience");
							player.sendMessage(ChatColor.YELLOW + "/Leveling SetLevel [Level] <Player> " + ChatColor.GOLD + "- Set the level");
						} else {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.unknown-argument"));
						}
					} else if(args.length >= 2 && args.length <= 3) {
						int value = 0;
						if(RandomNumbers.isInt(args[1])) {
							value = Integer.parseInt(args[1]);
						} else {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.invalid-number"));
							return true;
						}
						
						OfflinePlayer target = player;
						if(args.length == 3) {
							if(Bukkit.getServer().getOfflinePlayer(args[2]) != null) {
								target = Bukkit.getServer().getOfflinePlayer(args[2]);
							} else {
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.unknown-player"));
								return true;
							}
						}
						
						if(!target.isOnline()) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.unknown-player"));
							return true;
						}
						PlayerLevel level = new PlayerLevel(target.getPlayer());
						
						if(args[0].equalsIgnoreCase("SetExp")) {
							level.setExp(value);
							
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("leveling.exp-changed-executor").replace("%exp%", "" + value).replace("%target%", target.getName()));
							
							if(target != player && target.isOnline()) {
								((Player) target).sendMessage(Plugin.getCore().getLangTools().getMessage("leveling.exp-changed-target").replace("%exp%", "" + value));
							}
						} else if(args[0].equalsIgnoreCase("SetLevel")) {
							level.setLevel(value);
							
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("leveling.level-changed-executor").replace("%level%", "" + value).replace("%target%", target.getName()));
							
							if(target != player && target.isOnline()) {
								((Player) target).sendMessage(Plugin.getCore().getLangTools().getMessage("leveling.level-changed-target").replace("%level%", "" + value));
							}
						}
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