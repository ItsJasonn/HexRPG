package me.ItsJasonn.HexRPG.Listener;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import me.ItsJasonn.HexRPG.Main.CombatLogger;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class EntityDamage implements Listener {
	@EventHandler (priority=EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
			return;
		}

		double damageDone = event.getDamage();
		
		if(event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			
			CombatLogger.putInCombat(player);
			
			for(ItemStack armor : player.getInventory().getArmorContents()) {
				if(armor == null || armor.getType() == Material.AIR || !Plugin.getCore().getStatsManager().isArmor(armor)) {
					continue;
				}
				
				if(armor.getType().toString().contains("_HELMET")) {
					player.getInventory().setHelmet(Plugin.getCore().getStatsManager().removeDurability(player, armor));
				} else if(armor.getType().toString().contains("_CHESTPLATE")) {
					player.getInventory().setChestplate(Plugin.getCore().getStatsManager().removeDurability(player, armor));
				} else if(armor.getType().toString().contains("_LEGGINGS")) {
					player.getInventory().setLeggings(Plugin.getCore().getStatsManager().removeDurability(player, armor));
				} else if(armor.getType().toString().contains("_BOOTS")) {
					player.getInventory().setBoots(Plugin.getCore().getStatsManager().removeDurability(player, armor));
				}
			}
			
			if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
				Plugin.getCore().getStatsManager().resetPlayerStats(player);
			}

			if((event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) && (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) && (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)) {
				int percentDefense = 0;
				if(Plugin.getCore().getStatsManager().getPlayerDefense().containsKey(player)) {
					percentDefense = Plugin.getCore().getStatsManager().getPlayerDefense().get(player);
				}
				
				double ignoredDefense = damageDone - damageDone / 100.0D * percentDefense;

				event.setDamage(ignoredDefense);
			}
		}
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.damage-indicator")) {
			if(event.getEntity() instanceof LivingEntity && event.getEntity() instanceof Damageable && (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_EXPLOSION)) {
				LivingEntity entity = (LivingEntity) event.getEntity();
				if(entity.hasPotionEffect(PotionEffectType.INVISIBILITY) || (entity instanceof ArmorStand && ((ArmorStand) entity).isVisible()) || event.isCancelled()) {
					return;
				}
				
				Location hologramSpawn = entity.getEyeLocation();
				
				ArmorStand hologram = (ArmorStand) entity.getWorld().spawnEntity(hologramSpawn, EntityType.ARMOR_STAND);
				
				DecimalFormat df = new DecimalFormat("##0.00");
				String formattedDamage = df.format(damageDone).replace(",", ".");
				hologram.setCustomName(ChatColor.RED + "-" + formattedDamage);
				hologram.setCustomNameVisible(true);
				
				hologram.setVisible(false);
				hologram.setGravity(false);
				hologram.setSmall(true);
				
				entity.addPassenger(hologram);
				
				CombatLogger.registerDamageHologram(hologram);
			}
		}
	}
}
