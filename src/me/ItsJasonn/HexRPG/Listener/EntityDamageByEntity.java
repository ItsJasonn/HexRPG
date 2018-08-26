package me.ItsJasonn.HexRPG.Listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.ItsJasonn.HexRPG.Main.CombatLogger;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.Caster;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.CustomMob;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.Witch;

public class EntityDamageByEntity implements Listener {
	@EventHandler (priority=EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof ArmorStand) {
			return;
		}
		
		if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
			return;
		}
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
			if(event.getDamager() instanceof Player) {
				Player damager = (Player) event.getDamager();
				Plugin.getCore().getStatsManager().resetPlayerStats(damager);
			}
		}
		
		double damageDone = event.getDamage();
		
		if(event.getDamager() instanceof LivingEntity && !(event.getDamager() instanceof Player)) {
			if(CustomMob.isCustomMob(event.getDamager())) {
				CustomMob customMob = CustomMob.getCustomMob(event.getDamager());
				damageDone = customMob.getRandomDamage();
			}
		} else if(event.getDamager() instanceof ThrownPotion) {
			ThrownPotion potion = (ThrownPotion) event.getDamager();
			if(CustomMob.isCustomMob((Entity) potion.getShooter()) && (CustomMob.getCustomMob((Entity) potion.getShooter()) instanceof Witch || CustomMob.getCustomMob((Entity) potion.getShooter()) instanceof Caster)) {
				event.setDamage(CustomMob.getCustomMob((Entity) potion.getShooter()).getRandomDamage());
			}
		} else if(event.getDamager() instanceof Player) {
			final Player damager = (Player) event.getDamager();
			
			if(Plugin.getCore().getStatsManager().isWeapon(damager.getInventory().getItemInMainHand())) {
				damager.getInventory().setItemInMainHand(Plugin.getCore().getStatsManager().removeDurability(damager, damager.getInventory().getItemInMainHand()));
			}
			
			if(Plugin.getCore().getStatsManager().isWeapon(damager.getInventory().getItemInOffHand())) {
				damager.getInventory().setItemInOffHand(Plugin.getCore().getStatsManager().removeDurability(damager, damager.getInventory().getItemInOffHand()));
			}
			
			damageDone = Plugin.getCore().getStatsManager().getPlayerDMG().get(damager) + Plugin.getCore().getStatsManager().getPlayerLifesteal().get(damager);
			
			if(event.getEntity() instanceof LivingEntity) {
				CombatLogger.putInCombat(damager);
				
				LivingEntity le = (LivingEntity) event.getEntity();
				
				event.setDamage(Plugin.getCore().getStatsManager().getDamage(damager, le, damageDone));
				
				if(event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					if(Plugin.getCore().inDual.containsKey(damager) && Plugin.getCore().inDual.get(damager) != player && Plugin.getCore().inDual.containsKey(player) && Plugin.getCore().inDual.get(player) != damager) {
						event.setCancelled(true);
					}
				}
				
				double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
				if(damager.getHealth() + Plugin.getCore().getStatsManager().getPlayerLifesteal().get(damager) / 2 > maxHealth) {
					damager.setHealth(maxHealth);
				} else {
					damager.setHealth(damager.getHealth() + Plugin.getCore().getStatsManager().getPlayerLifesteal().get(damager) / 2);
				}
			}
		} else if(event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if((arrow.getShooter() instanceof Player)) {
				Player player = (Player) arrow.getShooter();
				
				if(event.getEntity() instanceof Damageable) {
					double damage = event.getDamage();
					if(ProjectileLaunch.bowShot.containsKey(arrow)) {
						damage = ProjectileLaunch.bowShot.get(arrow);
					}
					
					damage *= Plugin.getCore().getConfig().getDouble("classes." + Plugin.getCore().getStatsManager().getClass(player).toLowerCase() + ".ranged");
					
					event.setDamage(damage);
				}
			}
		} else if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if(!(event.getDamager() instanceof Player) && event.getDamager() instanceof LivingEntity) {
				if(Plugin.getCore().getConfig().isConfigurationSection("stats.mobs." + event.getDamager().getType().toString().toUpperCase())) {
					damageDone = Plugin.getCore().getConfig().getInt("stats.mobs." + event.getDamager().getType().toString().toUpperCase() + ".damage");
				} else {
					damageDone = Plugin.getCore().getConfig().getInt("stats.mobs.DEFAULT.damage");
				}

				double percentDefense = Plugin.getCore().getStatsManager().getPlayerDefense().get(player) + (Plugin.getCore().getStatsManager().getPlayerEndurance().get(player) / 10);
				double ignoredDefense = damageDone - damageDone / 100.0D * percentDefense;
				
				event.setDamage(ignoredDefense);
			} else if((event.getDamager() instanceof Player)) {
				final Player damager = (Player) event.getDamager();

				event.setDamage(Plugin.getCore().getStatsManager().getDamage(damager, player, damageDone));
			}
		} else if(event.getEntity() instanceof LivingEntity) {
			if(event.getDamager() instanceof Player) {
				LivingEntity le = (LivingEntity) event.getEntity();
				Player damager = (Player) event.getDamager();
				
				event.setDamage(Plugin.getCore().getStatsManager().getDamage(damager, le, damageDone));
			}
		}
	}
}
