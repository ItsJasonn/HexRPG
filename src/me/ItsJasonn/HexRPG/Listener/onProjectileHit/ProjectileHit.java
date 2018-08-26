package me.ItsJasonn.HexRPG.Listener.onProjectileHit;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.itemnbtapi.NBTItem;

public class ProjectileHit implements Listener {
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		
		if(projectile.getType() != EntityType.DROPPED_ITEM) {
			return;
		}
		ItemStack item = ((Item) projectile).getItemStack();
		
		NBTItem nbtItem = new NBTItem(item);
		if(!nbtItem.hasKey("hexProjectile_a") || !nbtItem.getBoolean("hexProjectile_a")) {
			return;
		}
		int radius = nbtItem.getInteger("hexProjectile_b");
		int damage = nbtItem.getInteger("hexProjectile_c");
		
		projectile.getWorld().createExplosion(radius, radius, radius, damage, false, true);
		projectile.remove();
	}
}