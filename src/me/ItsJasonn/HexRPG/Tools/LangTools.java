package me.ItsJasonn.HexRPG.Tools;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class LangTools {
	public LangTools() {
		String[] languages = new String[] {"english"};
		
		for(String language : languages) {
			File file = new File(Plugin.getCore().getDataFolder(), language + ".yml");
			if(!file.exists()) {
				createNewLanguageFile(language);
			}
		}
	}
	
	private void createNewLanguageFile(String language) {
		Plugin.getCore().saveResource(language + ".yml", true);
	}
	
	private File getLanguageFile() {
		return new File(Plugin.getCore().getDataFolder(), Plugin.getCore().getConfig().getString("language_file"));
	}
	
	private YamlConfiguration getLanguageConfig() {
		return YamlConfiguration.loadConfiguration(getLanguageFile());
	}
	
	public String getMessage(String path) {
		return ChatColor.translateAlternateColorCodes('&', getLanguageConfig().getString(path));
	}
	
	public String getUncoloredMessage(String path) {
		 return getLanguageConfig().getString(path);
	}
}