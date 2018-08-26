package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;

import me.ItsJasonn.HexRPG.Main.Plugin;

@SuppressWarnings("deprecation")
public class Witch extends CustomMob implements Listener {
	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		if(event.getEntity() == getVanillaEntity()) {
			Location loc = getVanillaEntity().getLocation().clone();
			loc.setDirection(event.getTarget().getLocation().subtract(loc).toVector());
			getVanillaEntity().teleport(loc);
			
			event.setCancelled(true);
			
			Random random = new Random();
			int r = random.nextInt(100);
			if(r <= 50) {
				return;
			}
			
			ItemStack handItem = ((LivingEntity) getVanillaEntity()).getEquipment().getItemInHand();
			if(handItem.getType() == Material.SPLASH_POTION) {
				ThrownPotion thrownPotion = (ThrownPotion) getVanillaEntity().getWorld().spawnEntity(getVanillaEntity().getLocation().add(event.getTarget().getLocation().subtract(getVanillaEntity().getLocation()).toVector().normalize()), EntityType.SPLASH_POTION);
				thrownPotion.setItem(handItem);
				thrownPotion.setShooter((ProjectileSource) getVanillaEntity());
				thrownPotion.setVelocity(getVanillaEntity().getLocation().getDirection());
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		int radius = 3;
		if(player.getLocation().distance(getVanillaEntity().getLocation()) <= radius) {
			player.setVelocity(player.getLocation().subtract(getVanillaEntity().getLocation()).toVector().normalize().multiply(0.5));
			
			for(Player players : Bukkit.getOnlinePlayers()) {
				players.playSound(getVanillaEntity().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 10, 2);
				
				players.playEffect(player.getLocation().add(0, -1, 0), Effect.STEP_SOUND, player.getLocation().add(0, -1, 0).getBlock().getType());
				players.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
				players.playEffect(getVanillaEntity().getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
			}
		}
	}
	
	public Witch(Entity entity) {
		super(entity);
	}

	public void executeDefault() {
		org.bukkit.entity.LivingEntity witch = (org.bukkit.entity.LivingEntity) getVanillaEntity();
		
		witch.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		witch.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 2));
		
		ArrayList<PotionType> types = new ArrayList<PotionType>();
		types.add(PotionType.INSTANT_DAMAGE);
		types.add(PotionType.POISON);
		types.add(PotionType.SLOWNESS);
		
		Random random = new Random();
		
		Potion potion = new Potion(types.get(random.nextInt(types.size())), 1, true);
		ItemStack potionItem = potion.toItemStack(16);
		witch.getEquipment().setItemInHand(potionItem);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getCore(), new Runnable() {
			public void run() {
				executeDefault();
			}
		}, 10 * 20);
	}
}