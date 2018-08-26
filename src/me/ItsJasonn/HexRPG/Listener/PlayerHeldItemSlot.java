package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class PlayerHeldItemSlot implements Listener {
	@EventHandler
	public void onPlayerHeldItemSlot(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		Plugin.getCore().getStatsManager().resetPlayerStats(player);
	}
}