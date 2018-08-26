package me.ItsJasonn.HexRPG.Commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class RespawnResources implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if ((command.equalsIgnoreCase("respawnResources")) && ((sender instanceof Player))) {
			Player player = (Player) sender;
			if((player.isOp() || player.hasPermission("hexrpg.respawnresources")) && args.length == 0) {
				player.sendMessage(Plugin.getCore().getLangTools().getMessage("resources.command-execution.respawn"));

				File blockFile = new File(Plugin.getCore().getDataFolder(), "/dat0/block.yml");
				YamlConfiguration blockConfig = YamlConfiguration.loadConfiguration(blockFile);
				for (String keys : blockConfig.getKeys(false)) {
					blockConfig.set(keys + ".timer", 1);
				}
				try {
					blockConfig.save(blockFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				player.sendMessage(ChatColor.WHITE + "Unknown command. Type \"/help\" for help.");
			}
		}
		return false;
	}
}
