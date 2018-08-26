package me.ItsJasonn.HexRPG.Listener;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;

public class PlayerFish implements Listener {
	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		Player player = event.getPlayer();
		
		Random random = new Random();
		int r = random.nextInt(100);
		int rExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.tools.exp-gained.min");
		
		PlayerLevel level = new PlayerLevel(player);
		double extraChance = Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-chance-fishing") * level.getSkillLevel("FISHING");
		
		if(event.getState() == State.CAUGHT_FISH) {
			if(r <= 50 + extraChance) {
				int rSkillExp = random.nextInt(Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.max") - Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min") + 1) + Plugin.getCore().getConfig().getInt("leveling.skills.exp-gained.min");
				level.setSkillExp("FISHING", level.getSkillExp("FISHING") + rSkillExp);
				
				player.sendMessage(ChatColor.YELLOW + "+" + rExp + " EXP");
				player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 1.0F);
			} else {
				player.sendMessage(Plugin.getCore().getLangTools().getMessage("resources.on-break.failed"));
			}
		}
	}
}
