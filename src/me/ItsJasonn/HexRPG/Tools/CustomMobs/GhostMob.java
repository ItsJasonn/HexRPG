package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class GhostMob extends CustomMob implements Listener {
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof LivingEntity && event.getEntity() == getVanillaEntity()) {
			if(!getVanillaEntity().isDead()) {
				teleportAway(14);
			}
		}
	}
	
	public GhostMob(Entity entity) {
		super(entity);
	}
	
	public void executeDefault() {
		
	}
	
	public void teleportAway(int radius) {
		Random random = new Random();
		
		Location newLocation = getVanillaEntity().getLocation().add(random.nextInt(radius * 2) - radius, random.nextInt(radius * 2) - radius, random.nextInt(radius * 2) - radius);
		for(int i=0;i<Integer.MAX_VALUE;i++) {
			if(newLocation.getBlock().getType() != Material.AIR || newLocation.add(0, -1, 0).getBlock().getType() == Material.AIR) {
				newLocation = getVanillaEntity().getLocation().add(random.nextInt(radius * 2) - radius, random.nextInt(radius * 2) - radius, random.nextInt(radius * 2) - radius);
			} else {
				break;
			}
		}
		
		for(Player players : Bukkit.getOnlinePlayers()) {
			players.playSound(getVanillaEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
			players.playSound(newLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
		}
		for(int i=0;i<2;i++) {
			Firework fw = getVanillaEntity().getWorld().spawn(getVanillaEntity().getLocation(), Firework.class);
			if(i == 1) {
				fw = getVanillaEntity().getWorld().spawn(newLocation, Firework.class);
			}
			
			FireworkMeta fwMeta = fw.getFireworkMeta();
			fwMeta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(Type.BALL).build());
			fw.setFireworkMeta(fwMeta);
			
			final Firework fwFinal = fw;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					fwFinal.detonate();
				}
			}, 1);
		}
		
		getVanillaEntity().teleport(newLocation.add(0, 1, 0));
	}
}