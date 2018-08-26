package me.ItsJasonn.HexRPG.Listener.onPlayerInteract;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class Farmland implements Listener {
	@SuppressWarnings("static-access")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(event.getAction() == Action.PHYSICAL || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			for(ItemStack items : Plugin.getCore().itemList) {
				if(player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() == items.getType() && player.getInventory().getItemInMainHand().getDurability() == items.getDurability()) {
					event.setCancelled(true);
				}
			}
		}
	}
}
