package me.ItsJasonn.HexRPG.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class Placechest implements CommandExecutor {
	@SuppressWarnings("unchecked")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Placechest")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp() || player.hasPermission("hexrpg.placechest")) {
					if (args.length == 0) {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("lockpicking-chest.command-execution.placed"));

						File file = new File(Plugin.getCore().getDataFolder(), "/dat0/chests.yml");
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						
						ArrayList<Location> list = new ArrayList<Location>();
						if(config.isList("locations")) {
							list = (ArrayList<Location>) config.getList("locations");
						}
						list.add(player.getLocation());
						
						config.set("locations", list);
						try {
							config.save(file);
						} catch (IOException e) {
							e.printStackTrace();
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
