package me.ItsJasonn.HexRPG.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import de.tr7zw.itemnbtapi.NBTEntity;
import me.ItsJasonn.HexRPG.Main.Plugin;

public class Createshop implements CommandExecutor {
	private ArrayList<Player> checkingShops = new ArrayList<Player>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		
		if(command.equalsIgnoreCase("Createshop")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				
				if(player.isOp() || player.hasPermission("hexrpg.createshop")) {
					if(args.length == 0) {
						boolean canContinue = true;
						
						if (!checkingShops.contains(player)) {
							player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.command-execution.checking"));
							checkingShops.add(player);
							for (World worlds : Bukkit.getServer().getWorlds()) {
								for (Chunk chunks : worlds.getLoadedChunks()) {
									for(Entity e : chunks.getEntities()) {
										NBTEntity nbtEntity = new NBTEntity(e);
										if(!nbtEntity.hasKey("hex_shopOwner")) {
											continue;
										}
										String shopOwner = nbtEntity.getString("hex_shopOwner");
										
										if(player.getUniqueId().toString().equals(shopOwner)) {
											player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.command-execution.already-exists"));
											canContinue = false;
										}
									}
								}
							}
							
							checkingShops.remove(player);
							
							if(canContinue) {
								player.sendMessage(Plugin.getCore().getLangTools().getMessage("shop.command-execution.placed"));

								Villager shopNpc = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
								shopNpc.setCanPickupItems(false);
								shopNpc.setAI(false);
								
								shopNpc.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + "[S] » " + player.getName());
								shopNpc.setCustomNameVisible(true);
								
								NBTEntity entity = new NBTEntity(shopNpc);
								entity.setString("hex_shopOwner", player.getUniqueId().toString());
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