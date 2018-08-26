package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class BlockPlace implements Listener {
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE) {
			if(event.getBlock().getType() == Material.GRASS) {
				player.sendMessage(Plugin.getCore().getLangTools().getMessage("resources.on-place.long-grass"));
				event.setCancelled(true);
			}
		}
	}
}