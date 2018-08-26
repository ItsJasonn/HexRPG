package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import me.ItsJasonn.HexRPG.Tools.SubConfig;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.CustomMob;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.Wraith;

public class CreatureSpawn implements Listener {
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		
		if(entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
			LivingEntity le = (LivingEntity) entity;
			
			if(CustomMob.isCustomMob(entity) && CustomMob.getCustomMob(entity) instanceof Wraith) {
				le.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
				le.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
				le.getEquipment().setLeggings(new ItemStack(Material.AIR));
				le.getEquipment().setBoots(new ItemStack(Material.AIR));
			}
			
			le.getEquipment().setHelmetDropChance(0.0F);
			le.getEquipment().setChestplateDropChance(0.0F);
			le.getEquipment().setLeggingsDropChance(0.0F);
			le.getEquipment().setBootsDropChance(0.0F);
			
			if(!CustomMob.isCustomMob(entity) && entity instanceof Damageable) {
				if(!(entity instanceof Attributable)) {
					return;
				}
				Attributable attr = (Attributable) entity;
				
				YamlConfiguration statsConfig = new SubConfig(SubConfig.TYPES.STATS).getConfig();
				
				if(statsConfig.isConfigurationSection("stats.mobs." + event.getEntity().getType().toString().toUpperCase())) {
					attr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(statsConfig.getInt("stats.mobs." + event.getEntity().getType().toString().toUpperCase() + ".health"));
				} else {
					attr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(statsConfig.getInt("stats.mobs.DEFAULT.health"));
				}
				attr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(attr.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			}
		}
	}
}
