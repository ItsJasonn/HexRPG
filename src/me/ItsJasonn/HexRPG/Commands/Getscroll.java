package me.ItsJasonn.HexRPG.Commands;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Instances.HexRPGScroll;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class Getscroll implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Getscroll")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp() || player.hasPermission("hexrpg.getscroll")) {
					SubConfig subConfig = new SubConfig(SubConfig.TYPES.SCROLLS);
					YamlConfiguration config = YamlConfiguration.loadConfiguration(subConfig.getFile());
					
					if (args.length == 0) {
						player.sendMessage(ChatColor.YELLOW + "/Getscroll " + ChatColor.GOLD + "- Popup this help page.");
						player.sendMessage(ChatColor.YELLOW + "/Getscroll [Type] <Player> " + ChatColor.GOLD + "- Receive or give a scroll");
						
						player.sendMessage("");
						
						player.sendMessage(ChatColor.YELLOW + "Available scrolls (" + ChatColor.RED + "case-sensitive" + ChatColor.YELLOW + "):");
						
						String scrolls = "";
						for(String scrollNames : config.getConfigurationSection("additionals").getKeys(false)) {
							if(!scrolls.isEmpty()) {
								scrolls += ", ";
							} else {
								scrolls = ChatColor.GOLD + "";
							}
							scrolls += ChatColor.translateAlternateColorCodes('&', config.getString("color." + scrollNames)) + scrollNames + ChatColor.GOLD;
						}
						player.sendMessage(ChatColor.GOLD + scrolls);
					} else if (args.length == 1 || args.length == 2) {
						for(String keys : config.getKeys(false)) {
							if(config.isSet(keys + "." + args[0])) {
								continue;
							}
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.command-execution.does-not-exist"));
							return true;
						}
						
						Player target = player;
						if(args.length == 2) {
							if(Plugin.getCore().getOfflinePlayerByName(args[1]) != null && Plugin.getCore().getOfflinePlayerByName(args[1]).isOnline()) {
								target = Plugin.getCore().getOfflinePlayerByName(args[1]).getPlayer();
							} else {
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("command-generals.unknown-player"));
								return true;
							}
						}
						
						target.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.command-execution.received").replace("%scroll%", WordUtils.capitalizeFully(args[0])));
						if(!target.equals(player)) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("scrolls.command-execution.given").replace("%scroll%", WordUtils.capitalizeFully(args[0])).replace("%player%", target.getName()));
						}

						HexRPGScroll scroll = new HexRPGScroll(args[0]);
						target.getInventory().addItem(scroll.getScroll());
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