package me.ItsJasonn.HexRPG.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import de.tr7zw.itemnbtapi.NBTEntity;
import me.ItsJasonn.HexRPG.Main.Plugin;
import net.md_5.bungee.api.ChatColor;

public class SetBank implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		if(command.equalsIgnoreCase("Setbank")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if (player.isOp() || player.hasPermission("hexrpg.setbank")) {
					if (args.length == 0) {
						player.sendMessage(Plugin.getCore().getLangTools().getMessage("bank.command-execution.placed"));

						Villager bankNpc = (Villager) player.getWorld().spawnEntity(player.getLocation().add(0.5D, 0.0D, 0.5D), EntityType.VILLAGER);
						bankNpc.setCanPickupItems(false);
						bankNpc.setAI(false);

						bankNpc.setCustomName(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString("npc.holograms.bank")));
						bankNpc.setCustomNameVisible(true);
						
						NBTEntity entity = new NBTEntity(bankNpc);
						entity.setBoolean("hex_isBank", true);
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
