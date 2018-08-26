package me.ItsJasonn.HexRPG.Commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class ChooseRace implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Chooserace")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.isOp() || player.hasPermission("hexrpg.chooserace")) {
					if(args.length == 0) {
						File statsFile = new SubConfig(SubConfig.TYPES.PLAYERSTATS).getFile();
						YamlConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);

						if(!statsConfig.getString(player.getUniqueId().toString() + ".RACE").equalsIgnoreCase("NONE")) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("races.already-have-a-race"));
							return true;
						}
						
						Plugin.getCore().openMenu(player, "RACE");
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