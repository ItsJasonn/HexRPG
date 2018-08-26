package me.ItsJasonn.HexRPG.Listener;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class ProjectileLaunch implements Listener {
	public static HashMap<Arrow, Integer> bowShot = new HashMap<Arrow, Integer>();
	public static HashMap<Snowball, Integer> bulletShot = new HashMap<Snowball, Integer>();

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if(event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			if(arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
				final Player player = (Player) arrow.getShooter();
				
				Plugin.getCore().getStatsManager().resetPlayerStats(player);
				
				ItemStack weapon = null;
				if(player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() == Material.BOW) {
					weapon = player.getInventory().getItemInMainHand();
				}

				if(weapon != null && weapon.getType() == Material.BOW) {
					bowShot.put(arrow, Plugin.getCore().getStatsManager().getPlayerDMG().get(player));
				}
			}
		}
	}
}
