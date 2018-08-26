package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class HealthRegen implements Listener {
	@EventHandler
	public void onHealthRegen(EntityRegainHealthEvent event) {
		double regeneration = event.getAmount();
		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			regeneration += Plugin.getCore().getStatsManager().getHpRegenerationCount().get(player.getName());
		}
		
		event.setAmount(regeneration);
	}
}