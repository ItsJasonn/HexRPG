package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class ProjectileHit implements Listener {
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if ((event.getEntity() instanceof Arrow)) {
			final Arrow arrow = (Arrow) event.getEntity();
			
			if(!ProjectileLaunch.bowShot.containsKey(arrow)) {
				return;
			}
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					ProjectileLaunch.bowShot.remove(arrow);
					arrow.remove();
				}
			}, 1L);
		}
	}
}
