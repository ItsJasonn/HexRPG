package me.ItsJasonn.HexRPG.Tools;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class SubConfig {
	private File _f;
	private YamlConfiguration _c;
	
	public enum TYPES {
		FEATURES, SCROLLS, STATS, PETSTATS, PLAYERSTATS, STARTINGKIT
	}
	
	public SubConfig(TYPES type) {
		_f = new File(Plugin.getCore().getDataFolder(), Plugin.getCore().getConfig().getString("reference-files." + type.toString().toLowerCase()));
		_c = YamlConfiguration.loadConfiguration(_f);
	}
	
	public File getFile() {
		return _f;
	}
	
	public YamlConfiguration getConfig() {
		return _c;
	}
}