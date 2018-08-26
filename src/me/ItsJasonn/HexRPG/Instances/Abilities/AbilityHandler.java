package me.ItsJasonn.HexRPG.Instances.Abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tr7zw.itemnbtapi.NBTItem;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.SubConfig;

public class AbilityHandler implements Listener {
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(Ability.getStunnedPlayers().contains(player.getUniqueId())) {
			Ability.setStunned(player.getUniqueId(), false);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(Ability.getStunnedPlayers().contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			
			if(Ability.getSilencedPlayers().contains(damager.getUniqueId())) {
				event.setDamage(0);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(!new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.abilities")) {
			return;
		}
		
		if(event.getAction() == Action.PHYSICAL) {
			return;
		}
		
		ItemStack handItem = player.getInventory().getItemInMainHand();
		if(handItem == null || handItem.getType() == Material.AIR) {
			return;
		}
		
		NBTItem nbtItem = new NBTItem(handItem);
		if(!nbtItem.hasKey("hexAbility_a")) {
			return;
		}
		
		String key = event.getAction().toString().substring(0, event.getAction().toString().indexOf("_"));
		String ability = Ability.getAbility(handItem);
		
		if(handItem.getType() == Material.BOW) {
			if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
				return;
			}
		} else {
			if(!key.equalsIgnoreCase(Plugin.getCore().getConfig().getString("abilities." + ability + ".display.key"))) {
				return;
			}
		}
		
		if(Ability.hasCooldown(player.getUniqueId())) {
			player.sendMessage(Plugin.getCore().getLangTools().getMessage("abilities.cooldown"));
			return;
		}
		
		FileConfiguration config = Plugin.getCore().getConfig();
		String configPath = "abilities." + ability + ".settings.";
		
		Ability.setCooldown(player.getUniqueId(), true, config.getInt(configPath + "cooldown"));
		
		boolean stopMovingOnActivation = config.getBoolean(configPath + "stop-moving-on-activation");
		double damage = config.getDouble(configPath + "damage");
		double radius = config.getDouble(configPath + "radius");
		double stunDuration = config.getDouble(configPath + "stun-duration");
		double silenceDuration = config.getDouble(configPath + "silence-duration");
		int poisonLevel = config.getInt(configPath + "poison-damage");
		double poisonDuration = config.getDouble(configPath + "poision-duration");
		int movementSpeedLevel = config.getInt(configPath + "movement-speed-level");
		double movementSpeedDuration = config.getDouble(configPath + "movement-speed-duration");
		int jumpBoostLevel = config.getInt(configPath + "jump-boost-level");
		double jumpBoostDuration = config.getDouble(configPath + "jump-boost-duration");
		boolean removeStun = config.getBoolean(configPath + "remove-stun");
		boolean removeSilence = config.getBoolean(configPath + "remove-silence");
		List<String> giveItems = config.getStringList(configPath + "give-items");
		
		if(stopMovingOnActivation) {
			float walkSpeed = player.getWalkSpeed();
			player.setWalkSpeed(0.01f);
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
				public void run() {
					player.setWalkSpeed(walkSpeed);
				}
			}, 1);
		}
		
		if(radius > 0) {
			int loopRadius = (int) radius;
			for(int x=-loopRadius;x<=loopRadius;x++) {
				for(int y=-loopRadius;y<=loopRadius;y++) {
					for(int z=-loopRadius;z<=loopRadius;z++) {
						Location loc = player.getLocation().add(x, y, z);
						player.playEffect(loc, Effect.STEP_SOUND, loc.getBlock().getType());
					}
				}
			}
			
			ArrayList<LivingEntity> entities = new ArrayList<LivingEntity>();
			for(Entity e : player.getNearbyEntities(radius, radius, radius)) {
				if(e instanceof LivingEntity) {
					entities.add((LivingEntity) e);
				}
			}
			if(!entities.isEmpty()) {
				if(damage > 0) {
					for(LivingEntity le : entities) {
						le.damage(damage, player);
					}
				}
				
				if(stunDuration > 0) {
					for(LivingEntity le : entities) {
						Ability.setStunned(le.getUniqueId(), true, stunDuration);
					}
				}
				
				if(silenceDuration > 0) {
					for(LivingEntity le : entities) {
						Ability.setSilenced(le.getUniqueId(), true, silenceDuration);
					}
				}
				
				if(poisonLevel > 0 && poisonDuration > 0) {
					for(LivingEntity le : entities) {
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) (poisonDuration * 20), poisonLevel - 1));
					}
				}
			}
		}
		
		if(movementSpeedLevel > 0 && movementSpeedDuration > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (movementSpeedDuration * 20), movementSpeedLevel - 1));
		}
		
		if(jumpBoostLevel > 0 && jumpBoostDuration > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (int) (jumpBoostDuration * 20), jumpBoostLevel - 1));
		}
		
		if(removeStun) {
			Ability.setStunned(player.getUniqueId(), false);
		}
		
		if(removeSilence) {
			Ability.setSilenced(player.getUniqueId(), false);
		}
		
		if(!giveItems.equals(null) && !giveItems.isEmpty()) {
			for(String str : giveItems) {
				if(str.equals("") || str.isEmpty()) {
					continue;
				}
				
				String[] strSplitted = str.split(":");
				ItemStack item = new ItemStack(Material.matchMaterial(strSplitted[0]), Integer.parseInt(strSplitted[1]));
				player.getInventory().addItem(item);
			}
		}
	}
}
