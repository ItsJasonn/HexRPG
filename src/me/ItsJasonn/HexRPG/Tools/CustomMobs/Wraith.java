package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class Wraith extends CustomMob {
	public Wraith(Entity entity) {
		super(entity);
	}
	
	public void executeDefault() {
		((LivingEntity) getVanillaEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
			public void run() {
				if(getVanillaEntity() instanceof Zombie || getVanillaEntity() instanceof Skeleton || getVanillaEntity() instanceof PigZombie || getVanillaEntity() instanceof WitherSkeleton || getVanillaEntity() instanceof Giant) {
					for(ItemStack is : ((LivingEntity) getVanillaEntity()).getEquipment().getArmorContents()) {
						is.setType(Material.AIR);
					}
					
					((LivingEntity) getVanillaEntity()).getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
					((LivingEntity) getVanillaEntity()).getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
					((LivingEntity) getVanillaEntity()).getEquipment().setLeggings(new ItemStack(Material.AIR));
					((LivingEntity) getVanillaEntity()).getEquipment().setBoots(new ItemStack(Material.AIR));
				}
			}
		}, 1);
	}
}