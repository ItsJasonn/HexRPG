package me.ItsJasonn.HexRPG.Listener;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ItsJasonn.HexRPG.Instances.HexRPGScroll;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;

public class EntityDeath implements Listener {
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof LivingEntity) {
			Random random = new Random();
			float r = random.nextInt(100);
			
			float chance = Plugin.getCore().getConfig().getInt("entities.drop-chance");
			if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
				Player damager = event.getEntity().getKiller();
				chance = Plugin.getCore().getStatsManager().getPlayerDropChance().get(damager);
			}
			
			if(r <= chance && !(event.getEntity() instanceof Player)) {
				DropItems(event.getEntity());
			}
			
			if(!Plugin.getCore().getConfig().getBoolean("entities.drop-experience-orbs")) {
				event.setDroppedExp(0);
			}
			
			if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
				Player damager = (Player) event.getEntity().getKiller();
				
				PlayerLevel level = new PlayerLevel(damager);

				int expGained = Plugin.getCore().getConfig().getInt("leveling.gained-exp.DEFAULT");
				if(PlayerLevel.getExpList().keySet().contains(event.getEntity().getType())) {
					expGained = Plugin.getCore().getConfig().getInt("leveling.gained-exp." + event.getEntity().getType().toString().toUpperCase());
				}

				level.setExp(level.getExp() + expGained);
				damager.sendMessage(Plugin.getCore().getLangTools().getMessage("leveling.exp-gained").replace("%exp%", "" + expGained));
			}
		}
	}

	@SuppressWarnings("static-access")
	public void DropItems(Entity entity) {
		Random random = new Random();
		
		ItemStack droppedItem = null;
		if(Plugin.getCore().getConfig().getBoolean("death.entity-drops.weapons") || Plugin.getCore().getConfig().getBoolean("death.entity-drops.armor")) {
			int r = random.nextInt(Plugin.getCore().itemList.size());
			droppedItem = Plugin.getCore().getStatsManager().checkStats(Plugin.getCore().itemList.get(r), 1, "none");
			if(!Plugin.getCore().getConfig().getBoolean("death.entity-drops.weapons")) {
				while(Plugin.getCore().getStatsManager().isWeapon(droppedItem)) {
					r = random.nextInt(Plugin.getCore().itemList.size());
					droppedItem = Plugin.getCore().getStatsManager().checkStats(Plugin.getCore().itemList.get(r), 1, "none");
				}
			}
			if(!Plugin.getCore().getConfig().getBoolean("death.entity-drops.armor")) {
				while(Plugin.getCore().getStatsManager().isArmor(droppedItem)) {
					r = random.nextInt(Plugin.getCore().itemList.size());
					droppedItem = Plugin.getCore().getStatsManager().checkStats(Plugin.getCore().itemList.get(r), 1, "none");
				}
			}
		}

		ArrayList<String> rupeeLore = new ArrayList<String>();
		rupeeLore.add(ChatColor.WHITE + "Commonly used among trading");

		ItemStack rupee = new ItemStack(Material.EMERALD, random.nextInt(Plugin.getCore().getConfig().getInt("death.entity-drops.rupee-amount.max") - Plugin.getCore().getConfig().getInt("death.entity-drops.rupee-amount.min")) + Plugin.getCore().getConfig().getInt("death.entity-drops.rupee-amount.min"));
		ItemMeta rupeeMeta = rupee.getItemMeta();
		rupeeMeta.setDisplayName(ChatColor.GREEN + "Rupee");
		rupeeMeta.setLore(rupeeLore);
		rupee.setItemMeta(rupeeMeta);

		ArrayList<ItemStack> scrolls = new ArrayList<ItemStack>();
		for(HexRPGScroll scroll : HexRPGScroll.getScrolls()) {
			scrolls.add(scroll.getScroll());
		}
		
		ItemStack lockpick = new ItemStack(Material.FLINT_AND_STEEL);
		ItemMeta lockpickMeta = lockpick.getItemMeta();
		lockpickMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getLangTools().getMessage("tools.translation.lockpick"));
		lockpick.setItemMeta(lockpickMeta);
		
		double rChance = (double) random.nextInt(10000) / 100d;
		if(rChance > 0 && rChance < Plugin.getCore().getConfig().getDouble("death.entity-drops.chances.weapons") && Plugin.getCore().getStatsManager().isWeapon(droppedItem)) {
			entity.getWorld().dropItem(entity.getLocation(), droppedItem);
		}
		if(rChance > 0 && rChance < Plugin.getCore().getConfig().getDouble("death.entity-drops.chances.armor") && Plugin.getCore().getStatsManager().isArmor(droppedItem)) {
			entity.getWorld().dropItem(entity.getLocation(), droppedItem);
		}
		
		rChance = random.nextInt(100);
		if(rChance > 0 && rChance < Plugin.getCore().getConfig().getDouble("death.entity-drops.chances.rupees")) {
			entity.getWorld().dropItem(entity.getLocation(), rupee);
		}
		
		rChance = random.nextInt(100);
		if(rChance > 0 && rChance < Plugin.getCore().getConfig().getDouble("death.entity-drops.chances.scrolls")) {
			entity.getWorld().dropItem(entity.getLocation(), scrolls.get(random.nextInt(scrolls.size())));
		}
		
		rChance = random.nextInt(100);
		if(rChance > 0 && rChance < Plugin.getCore().getConfig().getDouble("death.entity-drops.chances.lockpicks")) {
			entity.getWorld().dropItem(entity.getLocation(), lockpick);
		}
	}
}
