package me.ItsJasonn.HexRPG.Listener.onPlayerInteract;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.itemnbtapi.NBTItem;
import me.ItsJasonn.HexRPG.Main.Plugin;

public class ProjectileThrow implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		ItemStack handItem = player.getInventory().getItemInMainHand();
		if(handItem.equals(null) || handItem.getType().equals(Material.AIR)) {
			return;
		}
		
		if(!Plugin.getCore().getStatsManager().isThrowable(handItem)) {
			return;
		}
		
		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);
		
		if(handItem.getAmount() > 1) {
			handItem.setType(Material.AIR);
		} else {
			handItem.setAmount(handItem.getAmount() - 1);
		}
		
		_throw(player, handItem);
	}
	
	private void _throw(Player player, ItemStack item) {
		FileConfiguration config = Plugin.getCore().getConfig();
		
		String configPath = "items.projectiles.explosives";
		int radius = config.getInt(configPath + ".radius");
		int damage = config.getInt(configPath + ".damage");
		
		NBTItem entItem = new NBTItem(item);
		entItem.setBoolean("hexProjectile_a", true);
		entItem.setInteger("hexProjectile_b", radius);
		entItem.setInteger("hexProjectile_c", damage);
		
		Item projectile = player.getWorld().dropItem(player.getLocation(), entItem.getItem());
		projectile.setPickupDelay(Integer.MAX_VALUE);
		projectile.setVelocity(player.getLocation().getDirection().multiply(1.2).setY(1.2));
	}
}