package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.EffectTool;
import net.minecraft.server.v1_13_R1.EntityInsentient;
import net.minecraft.server.v1_13_R1.Navigation;

public class NecroMancer extends CustomMob {
	private ArrayList<Entity> minions = new ArrayList<Entity>();
	
	public NecroMancer(Entity entity) {
		super(entity);
	}

	public void executeDefault() {
		ArrayList<EntityType> entityList = new ArrayList<EntityType>();
		entityList.add(EntityType.ZOMBIE);
		entityList.add(EntityType.PIG_ZOMBIE);
		
		for(int i=0;i<2;i++) {
			Random random = new Random();
			int r = random.nextInt(entityList.size());
			
			Entity entity = getVanillaEntity().getWorld().spawnEntity(getVanillaEntity().getLocation(), entityList.get(r));
			if(entity instanceof Zombie) {
				((Zombie) entity).setBaby(true);
			} else if(entity instanceof PigZombie) {
				((PigZombie) entity).setBaby(true);
			}
			entity.setCustomName(ChatColor.RED + "Necromancer's minion");
			entity.setCustomNameVisible(true);
			((Damageable) entity).setHealth(6);
			
			minions.add(entity);
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
			public void run() {
				if(getVanillaEntity() != null && !getVanillaEntity().isDead()) {
					executeDefault();
				}
			}
		}, 120 * 20);
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
			public void run() {
				if(getVanillaEntity() != null && !getVanillaEntity().isDead()) {
					//Necromancer effect
					for(Player players : Bukkit.getOnlinePlayers()) {
						int[] rgb = new int[]{1, 1, 1};
						EffectTool.generateParticleArroundEntity(players, getVanillaEntity(), 2.0f, 20, 0.3f, rgb);
					}
					
					//Minion navigation
					for(Entity e : minions) {
						if(getVanillaEntity().getLocation().distance(e.getLocation()) > 2) {
							Navigation nav = (Navigation) ((EntityInsentient) ((CraftEntity) e).getHandle()).getNavigation();
							nav.a(getVanillaEntity().getLocation().getX(), getVanillaEntity().getLocation().getY(), getVanillaEntity().getLocation().getZ(), 1.3d);
						}
					}
				}
			}
		}, 0, 2);
	}
	
	public ArrayList<Entity> getMinions() {
		return minions;
	}
}