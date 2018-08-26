package me.ItsJasonn.HexRPG.Listener;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class SpawnerSpawn implements Listener {
	@EventHandler
	public void onSpawnerSpawn(SpawnerSpawnEvent event) {
		File file = new File(Plugin.getCore().getDataFolder() + "/dat0/", "spawners.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(String keys : config.getKeys(false)) {
			Location location = new Location(Bukkit.getServer().getWorld(config.getString(keys + ".world")), config.getInt(keys + ".x"), config.getInt(keys + ".y"), config.getInt(keys + ".z"));
			if(event.getSpawner().getLocation().getWorld() == location.getWorld() && event.getSpawner().getLocation().getBlockX() == location.getBlockX() && event.getSpawner().getLocation().getBlockY() == location.getBlockY() && event.getSpawner().getLocation().getBlockZ() == location.getBlockZ()) {
				event.setCancelled(true);
				if(event.getEntity() != null && !event.getEntity().isDead()) {
					event.getEntity().remove();
				}
			}
		}
	}
}