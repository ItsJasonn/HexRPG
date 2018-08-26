package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;
import net.minecraft.server.v1_13_R1.EntityInsentient;
import net.minecraft.server.v1_13_R1.Navigation;

public class Bat extends CustomMob implements Listener {
	private HashMap<Player, Integer> movevementCounter = new HashMap<Player, Integer>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(player.getPassengers().get(0) == null) {
			return;
		}
		Entity passenger = player.getPassengers().get(0);
		
		if(isCustomMob(passenger) && getCustomMob(passenger) instanceof Bat && passenger.getUniqueId().equals(getVanillaEntity().getUniqueId())) {
			if(!movevementCounter.containsKey(player)) {
				movevementCounter.put(player, 0);
			}
			
			movevementCounter.put(player, movevementCounter.get(player) + 1);
			if(movevementCounter.get(player) >= 50) {
				player.removePassenger(passenger);
				target = null;
				
				movevementCounter.remove(player);
			}
		}
	}
	
	private Player target;
	
	public Bat(Entity entity) {
		super(entity);
	}

	public void executeDefault() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
			public void run() {
				if(target != null) {
					getVanillaEntity().setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "Bat");
					
					if(target.isDead() || !getVanillaEntity().getNearbyEntities(15, 15, 15).contains(target)) {
						target = null;
					}
				}
				
				for(Entity e : getVanillaEntity().getNearbyEntities(9, 9, 9)) {
					if(target != null && target.getVehicle() != null) {
						break;
					}
					
					if(e instanceof Player) {
						Player nearbyPlayer = (Player) e;
						if((nearbyPlayer.getGameMode() != GameMode.SURVIVAL && nearbyPlayer.getGameMode() != GameMode.ADVENTURE) || (nearbyPlayer.getVehicle() != null)) {
							continue;
						}
						
						if(target == null || e.getLocation().distance(getVanillaEntity().getLocation()) < target.getLocation().distance(getVanillaEntity().getLocation())) {
							target = (Player) e;
							break;
						}
					}
				}
				
				if(target != null) {
					Navigation nav = (Navigation) ((EntityInsentient) ((CraftEntity) getVanillaEntity()).getHandle()).getNavigation();
					Location targetLocation = target.getLocation().add(0, 0.5, 0);
					nav.a(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
					
					if(getVanillaEntity().getLocation().distance(target.getLocation()) < 0.5) {
						target.addPassenger(getVanillaEntity());
					}
				}
			}
		}, 0, 1);
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
			public void run() {
				if(getVanillaEntity() != null && !getVanillaEntity().isDead()) {
					if(getVanillaEntity().getVehicle() != null && getVanillaEntity().getVehicle() instanceof Player) {
						Player vehicle = (Player) getVanillaEntity().getVehicle();
						vehicle.damage(getRandomDamage() / 30);
					}
				}
			}
		}, 0, 7);
	}
}