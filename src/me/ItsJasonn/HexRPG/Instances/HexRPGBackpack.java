package me.ItsJasonn.HexRPG.Instances;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class HexRPGBackpack {
	private Player player;
	
	private final int rows = Plugin.getCore().getConfig().getInt("backpacks.rows");
	
	public HexRPGBackpack(Player player) {
		this.player = player;
	}
	
	public void openBackpack() {
		Inventory inv = Bukkit.getServer().createInventory(null, rows * 9, Plugin.getCore().getLangTools().getUncoloredMessage("backpacks.translation.backpack"));
		
		if(!hasBackpack()) {
			this.player.openInventory(inv);
		}
		
		ArrayList<ItemStack> items = getItems();
		for(int i=0;i<items.size();i++) {
			inv.setItem(i, items.get(i));
		}
		
		this.player.openInventory(inv);
	}
	
	public void addItem(ItemStack item) {
		YamlConfiguration c = getConfig();
		
		for(String keys : c.getKeys(false)) {
			if(c.getItemStack(keys).getType() == Material.AIR) {
				setItem(item, Integer.parseInt(keys));
				break;
			}
		}
	}
	
	public void removeItem(int slot) {
		File f = getFile();
		YamlConfiguration c = getConfig();
		
		c.set(Integer.toString(slot), new ItemStack(Material.AIR));
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setItem(ItemStack item, int slot) {
		File f = getFile();
		YamlConfiguration c = getConfig();
		
		c.set(Integer.toString(slot), item);
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ItemStack getItem(int slot) {
		YamlConfiguration c = getConfig();
		return c.getItemStack(Integer.toString(slot));
	}
	
	public boolean hasBackpack() {
		return getFile().exists();
	}
	
	public void generateBackpack() {
		File f = getFile();
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration c = getConfig();
		
		for(int i=0;i<Plugin.getCore().getConfig().getInt("backpacks.rows") * 9;i++) {
			c.set(Integer.toString(i), new ItemStack(Material.AIR));
		}
		
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<ItemStack> getItems() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		YamlConfiguration config = getConfig();
		
		for(String key : config.getKeys(false)) {
			list.add(config.getItemStack(key));
		}
		
		return list;
	}
	
	private File getFile() {
		File folder = new File(Plugin.getCore().getDataFolder(), "/backpacks/");
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		return new File(Plugin.getCore().getDataFolder() + "/backpacks/", this.player.getUniqueId().toString() + ".yml");
	}
	
	private YamlConfiguration getConfig() {
		return YamlConfiguration.loadConfiguration(getFile());
	}
}