package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.EffectTool;

public class Wisp extends CustomMob {
	public Wisp(Entity entity) {
		super(entity);
	}
	
	public void executeDefault() {
		((LivingEntity) getVanillaEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
			public void run() {
				if(getVanillaEntity() != null && !getVanillaEntity().isDead()) {
					for(Player players : Bukkit.getOnlinePlayers()) {
						for(int i=0;i<10;i++) {
							Random random = new Random();
							int[] rgb = new int[]{random.nextInt(255) + 1, random.nextInt(255) + 1, random.nextInt(255) + 1};
							
							EffectTool.playColoredSmoke(players, getVanillaEntity().getLocation(), rgb);
						}
					}
				}
			}
		}, 0, 1);
	}
}