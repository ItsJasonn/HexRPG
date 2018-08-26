package me.ItsJasonn.HexRPG.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.tr7zw.itemnbtapi.NBTEntity;
import de.tr7zw.itemnbtapi.NBTItem;
import me.ItsJasonn.HexRPG.Commands.Backpack;
import me.ItsJasonn.HexRPG.Commands.ChooseClass;
import me.ItsJasonn.HexRPG.Commands.ChooseRace;
import me.ItsJasonn.HexRPG.Commands.Createshop;
import me.ItsJasonn.HexRPG.Commands.Getscroll;
import me.ItsJasonn.HexRPG.Commands.Items;
import me.ItsJasonn.HexRPG.Commands.Leveling;
import me.ItsJasonn.HexRPG.Commands.Placechest;
import me.ItsJasonn.HexRPG.Commands.RespawnResources;
import me.ItsJasonn.HexRPG.Commands.SetBank;
import me.ItsJasonn.HexRPG.Commands.Smith;
import me.ItsJasonn.HexRPG.Commands.Spawnblock;
import me.ItsJasonn.HexRPG.Commands.Stats;
import me.ItsJasonn.HexRPG.Commands.Tools;
import me.ItsJasonn.HexRPG.Instances.Abilities.Ability;
import me.ItsJasonn.HexRPG.Instances.Abilities.AbilityHandler;
import me.ItsJasonn.HexRPG.Listener.ArmorStandManipulation;
import me.ItsJasonn.HexRPG.Listener.AsyncChatEvent;
import me.ItsJasonn.HexRPG.Listener.BlockBreak;
import me.ItsJasonn.HexRPG.Listener.BlockPlace;
import me.ItsJasonn.HexRPG.Listener.CreatureSpawn;
import me.ItsJasonn.HexRPG.Listener.EntityCombust;
import me.ItsJasonn.HexRPG.Listener.EntityDamage;
import me.ItsJasonn.HexRPG.Listener.EntityDamageByEntity;
import me.ItsJasonn.HexRPG.Listener.EntityDeath;
import me.ItsJasonn.HexRPG.Listener.EntityPickupItem;
import me.ItsJasonn.HexRPG.Listener.HealthRegen;
import me.ItsJasonn.HexRPG.Listener.InventoryClick;
import me.ItsJasonn.HexRPG.Listener.InventoryClose;
import me.ItsJasonn.HexRPG.Listener.InventoryOpen;
import me.ItsJasonn.HexRPG.Listener.ItemCraft;
import me.ItsJasonn.HexRPG.Listener.PlayerDeath;
import me.ItsJasonn.HexRPG.Listener.PlayerFish;
import me.ItsJasonn.HexRPG.Listener.PlayerHeldItemSlot;
import me.ItsJasonn.HexRPG.Listener.PlayerInteract;
import me.ItsJasonn.HexRPG.Listener.PlayerInteractAtEntity;
import me.ItsJasonn.HexRPG.Listener.PlayerJoin;
import me.ItsJasonn.HexRPG.Listener.ProjectileHit;
import me.ItsJasonn.HexRPG.Listener.ProjectileLaunch;
import me.ItsJasonn.HexRPG.Listener.SpawnerSpawn;
import me.ItsJasonn.HexRPG.Listener.onEntityTame.PetStats;
import me.ItsJasonn.HexRPG.Listener.onInventoryClick.BackpackListener;
import me.ItsJasonn.HexRPG.Listener.onInventoryClick.ClassChooser;
import me.ItsJasonn.HexRPG.Listener.onInventoryClick.RaceChooser;
import me.ItsJasonn.HexRPG.Listener.onInventoryClick.ScrollApplying;
import me.ItsJasonn.HexRPG.Listener.onInventoryClick.SmithingWindow;
import me.ItsJasonn.HexRPG.Listener.onInventoryClick.StatsWindow;
import me.ItsJasonn.HexRPG.Listener.onInventoryClick.ToolsWindow;
import me.ItsJasonn.HexRPG.Listener.onPlayerInteract.Farmland;
import me.ItsJasonn.HexRPG.Listener.onPlayerInteract.ProjectileThrow;
import me.ItsJasonn.HexRPG.PlaceholderAPI.HexRPGPlaceholders;
import me.ItsJasonn.HexRPG.Tools.LangTools;
import me.ItsJasonn.HexRPG.Tools.Metrics;
import me.ItsJasonn.HexRPG.Tools.SQLManager;
import me.ItsJasonn.HexRPG.Tools.StatsManager;
import me.ItsJasonn.HexRPG.Tools.SubConfig;
import me.ItsJasonn.HexRPG.Tools.UUIDTools;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.CustomMob;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.CustomMobManager;
import net.milkbowl.vault.economy.Economy;

public class Core extends JavaPlugin {
	private Economy economy;

	public StatsManager statsManager = new StatsManager();
	private CustomMobManager customMobManager;
	private LangTools langTools;

	private SQLManager sqlManager;

	public HashMap<Player, Chest> clickedChest = new HashMap<Player, Chest>();
	public HashMap<Player, ArrayList<Integer>> chestAnswers = new HashMap<Player, ArrayList<Integer>>();
	public HashMap<Player, boolean[]> chestIndexMap = new HashMap<Player, boolean[]>();
	public boolean[] chestIndex = new boolean[10];

	public static HashMap<ItemStack, String> itemMaterial = new HashMap<ItemStack, String>();
	public static ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
	public static ArrayList<ItemStack> armorList = new ArrayList<ItemStack>();
	public static ArrayList<ItemStack> weaponList = new ArrayList<ItemStack>();
	public static ArrayList<ItemStack> throwableList = new ArrayList<ItemStack>();

	public ArrayList<String> toolList = new ArrayList<String>();

	public HashMap<Player, Player> inDual = new HashMap<Player, Player>();
	public HashMap<Player, Player> inTrade = new HashMap<Player, Player>();
	public HashMap<Player, Player> dualRequest = new HashMap<Player, Player>();
	public HashMap<Player, Player> tradeRequest = new HashMap<Player, Player>();
	public HashMap<Player, Integer> stealCooldown = new HashMap<Player, Integer>();
	public HashMap<Player, Integer> stealCooldownId = new HashMap<Player, Integer>();

	public HashMap<Integer, Integer> smithCraftCount = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> smithCraftTask = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> reloadItemCount = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> reloadItemTask = new HashMap<Integer, Integer>();

	public HashMap<String, ArrayList<ItemStack>> dropList = new HashMap<String, ArrayList<ItemStack>>();
	
	public static ArrayList<UUID> damageHolograms = new ArrayList<UUID>();

	private int chestCount = 0;
	
	public void onDisable() {
		for(World worlds : Bukkit.getServer().getWorlds()) {
			for(Entity e : worlds.getEntities()) {
				if(damageHolograms.contains(e.getUniqueId())) {
					e.remove();
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		saveDefaultConfig();
		
		File featuresFile = new File(getDataFolder(), "features.yml");
		if(!featuresFile.exists()) {
			saveResource("features.yml", true);
		}
		
		File petStatsFile = new File(getDataFolder(), "petStats.yml");
		if(!petStatsFile.exists()) {
			saveResource("petStats.yml", true);
		}
		
		File playerStatsFile = new File(getDataFolder(), "playerStats.yml");
		if(!playerStatsFile.exists()) {
			saveResource("playerStats.yml", true);
		}
		
		File scrollsFile = new File(getDataFolder(), "scrolls.yml");
		if(!scrollsFile.exists()) {
			saveResource("scrolls.yml", true);
		}
		
		File startingKitFile = new File(getDataFolder(), "startingKit.yml");
		if(!startingKitFile.exists()) {
			saveResource("startingKit.yml", true);
		}
		
		File statsFile = new File(getDataFolder(), "stats.yml");
		if(!statsFile.exists()) {
			saveResource("stats.yml", true);
		}
		
		new Plugin(this);
		
		CombatLogger.startScheduler();
		Ability.startScheduler();
		
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceHolderAPI")) {
			new HexRPGPlaceholders(this).hook();
		}
		
		if(getConfig().getBoolean("economy.use-essentials-money")) {
			setupEconomy();
		}

		if(getConfig().getBoolean("world.disable-storm")) {
			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					for (World worlds : Bukkit.getServer().getWorlds()) {
						worlds.setStorm(false);
					}
				}
			}, 0, 100);
		}
		
		Bukkit.getServer().getPluginManager().registerEvents(new PetStats(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new Items(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new BackpackListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ClassChooser(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new RaceChooser(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ScrollApplying(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new SmithingWindow(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new StatsWindow(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ToolsWindow(), this);

		Bukkit.getServer().getPluginManager().registerEvents(new Farmland(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ProjectileThrow(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new ProjectileHit(), this);

		Bukkit.getServer().getPluginManager().registerEvents(new ArmorStandManipulation(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new AsyncChatEvent(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BlockBreak(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BlockPlace(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CreatureSpawn(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityCombust(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new HealthRegen(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClose(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryOpen(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ItemCraft(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerFish(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerHeldItemSlot(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityPickupItem(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityDamage(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageByEntity(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityDeath(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ProjectileHit(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ProjectileLaunch(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractAtEntity(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new SpawnerSpawn(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new AbilityHandler(), this);
		
		File file = new File(getDataFolder() + "/", "dat0/");
		file.mkdirs();
		
		File itemConversionFile = new File(getDataFolder(), "itemConversion.yml");
		if(!itemConversionFile.exists()) {
			saveResource("itemConversion.yml", true);
		}

		customMobManager = new CustomMobManager();
		langTools = new LangTools();
		
		if(SQLManager.using()) {
			sqlManager = new SQLManager(getConfig().getString("mysql.database"), getConfig().getString("mysql.ip"), getConfig().getString("mysql.port"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"));
			try {
				sqlManager.createTable("stats", new String[] { "kills", "deaths", "lastLogin", "firstLogin" });
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
		
		getCommand("backpack").setExecutor(new Backpack());
		getCommand("chooseclass").setExecutor(new ChooseClass());
		getCommand("chooserace").setExecutor(new ChooseRace());
		getCommand("respawnresources").setExecutor(new RespawnResources());
		getCommand("createshop").setExecutor(new Createshop());
		getCommand("getscroll").setExecutor(new Getscroll());
		getCommand("items").setExecutor(new Items());
		getCommand("leveling").setExecutor(new Leveling());
		getCommand("placechest").setExecutor(new Placechest());
		getCommand("setbank").setExecutor(new SetBank());
		getCommand("smith").setExecutor(new Smith());
		getCommand("spawnblock").setExecutor(new Spawnblock());
		getCommand("stats").setExecutor(new Stats());
		getCommand("tools").setExecutor(new Tools());

		armorList.clear();
		weaponList.clear();
		throwableList.clear();
		itemList.clear();
		toolList.clear();

		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
			itemConversionFile = new File(getDataFolder(), "itemConversion.yml");
			YamlConfiguration itemConversionConfig = YamlConfiguration.loadConfiguration(itemConversionFile);
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-=/     HexRPG item generator     \\=-");
			
			for(String keysA : itemConversionConfig.getKeys(false)) {
				for(String keysB : itemConversionConfig.getConfigurationSection(keysA).getKeys(false)) {
					String state = "";
					ItemStack item = null;
					
					try {
						String type = itemConversionConfig.getString(keysA + "." + keysB + ".type");
						String spigotMaterialEnum = keysA.split(":")[1];
						
						item = new ItemStack(Material.matchMaterial(spigotMaterialEnum), 1, Short.parseShort(keysB));
						if(type.equalsIgnoreCase("WEAPON")) {
							weaponList.add(item);
						} else if(type.equalsIgnoreCase("ARMOR")) {
							armorList.add(item);
						} else if(type.equalsIgnoreCase("THROWABLE")) {
							throwableList.add(item);
						}
						
						state = ChatColor.GREEN + "" + ChatColor.BOLD + "SUCCESS";
					} catch(Exception e) {
						state = ChatColor.RED + "" + ChatColor.UNDERLINE + "FAILED";
					}
					
					String rpgName = item == null ? "" : getStatsManager().getRPGName(item);
					Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "- '"  + ChatColor.WHITE + rpgName + ChatColor.GRAY + "'...." + state);
				}
			}
			
			for (ItemStack is : armorList) {
				itemList.add(is);
			}
			for (ItemStack is : weaponList) {
				itemList.add(is);
			}
			for (ItemStack is : throwableList) {
				itemList.add(is);
			}
			
			@SuppressWarnings("unchecked")
			ArrayList<ItemStack> itemListCopy = (ArrayList<ItemStack>) itemList.clone();
			for(ItemStack is : itemListCopy) {
				String key = "minecraft:" + is.getType().getKey().getKey();
				
				try {
					itemMaterial.put(is, itemConversionConfig.getString(key + "." + is.getDurability() + ".scraps.type").toUpperCase());
				} catch(NullPointerException e) {
					itemList.remove(is);
					
					Bukkit.getConsoleSender().sendMessage(ChatColor.RESET + "[HexRPG] " + ChatColor.RED + "Failed trying to convert item with item id '" + key + ":" + is.getDurability() + "'! Please check if everything is correct, and try again later.");
					continue;
				}
			}
		}

		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools")) {
			toolList.add(Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.hammer"));
			toolList.add(Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.hoe"));
			toolList.add(Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.pickaxe"));
			toolList.add(Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.fishing-rod"));
			toolList.add(Plugin.getCore().getLangTools().getUncoloredMessage("tools.translation.lockpick"));
		}
		
		recipeGeneratorLoop:
		for (ItemStack items : itemList) {
			NamespacedKey key = new NamespacedKey(this, getStatsManager().getRPGName(items).replace(" ", "_"));
			
			for (ItemStack ingredients : getScrapByItem(items)) {
				try {
					ShapelessRecipe recipe = new ShapelessRecipe(key, ingredients);
					recipe.addIngredient(items.getAmount(), items.getData());
					
					Bukkit.getServer().addRecipe(recipe);
				} catch(NullPointerException | IllegalArgumentException e) {
					continue recipeGeneratorLoop;
				}
			}
		}
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-=/  HexRPG item generator (END) \\=-");

		for (Player players : Bukkit.getOnlinePlayers()) {
			getStatsManager().generateStatsData(players);
			getStatsManager().resetPlayerStats(players);
			
			if(!getConfig().getBoolean("use-essentials-money")) {
				File bankFile = new File(getDataFolder() + "/dat0/", "bank.yml");
				if(!bankFile.exists()) {
					try {
						bankFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	
				YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
				if(!bankConfig.isConfigurationSection(players.getUniqueId().toString())) {
					bankConfig.createSection(players.getUniqueId().toString());
				}
				if(!bankConfig.isInt(players.getUniqueId().toString() + ".rupees")) {
					bankConfig.set(players.getUniqueId().toString() + ".rupees", 0);
				}
				try {
					bankConfig.save(bankFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			int loopIndex = 0;
			for(ItemStack handItem : new ItemStack[] { players.getInventory().getItemInMainHand(), players.getInventory().getItemInOffHand() }) {
				if(handItem == null || handItem.getType() == Material.AIR) {
					continue;
				}
				
				NBTItem nbtItem = new NBTItem(handItem);
				if(!nbtItem.hasKey("hexAbility_a")) {
					continue;
				}
				
				nbtItem.setBoolean("hexAbilityCooldown_a", false);
				
				ItemStack newNbtItem = nbtItem.getItem();
				ItemMeta newNbtItemMeta = newNbtItem.getItemMeta();
				newNbtItemMeta.setDisplayName(Plugin.getCore().getStatsManager().resetDisplayName(newNbtItem));
				newNbtItemMeta.setLore(Plugin.getCore().getStatsManager().resetStatsLore(newNbtItem));
				newNbtItem.setItemMeta(newNbtItemMeta);
				
				if(loopIndex == 0) {
					players.getInventory().setItemInMainHand(newNbtItem);
				} else if(loopIndex == 1) {
					players.getInventory().setItemInOffHand(newNbtItem);
				}
				
				loopIndex++;
			}
		}
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.custom-mobs")) {
			getCustomMobManager().startScheduler();
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.stats")) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						for (ItemStack is : player.getInventory().getContents()) {
							if(is == null || is.getType() == Material.AIR) {
								continue;
							}

							if(getStatsManager().isCustomItem(is) && !getStatsManager().isUnidentified(is) && !is.hasItemMeta()) {
								getStatsManager().setUnidentified(is);
							}
						}
						
						for (ItemStack contents : player.getInventory().getContents()) {
							if(contents == null || contents.getType() == Material.AIR) {
								continue;
							}

							ItemMeta meta = contents.getItemMeta();

							if(getStatsManager().isCustomItem(contents) && !contents.hasItemMeta()) {
								meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
								
								meta.setUnbreakable(true);
								
								meta.setDisplayName(ChatColor.WHITE + getStatsManager().getRPGName(contents));
							}
							
							contents.setItemMeta(meta);
						}
					}
				}
				
				File dat0Folder = new File(getDataFolder(), "/dat0/");
				if(!dat0Folder.exists() || dat0Folder.listFiles().length == 0) {
					return;
				}

				File spawnersFile = new File(getDataFolder() + "/dat0/", "spawners.yml");
				YamlConfiguration spawnersConfig = YamlConfiguration.loadConfiguration(spawnersFile);
				for (String keys1 : spawnersConfig.getKeys(false)) {
					for (String keys2 : spawnersConfig.getKeys(false)) {
						if(keys1 == keys2) {
							continue;
						}

						Location loc1 = new Location(Bukkit.getServer().getWorld(spawnersConfig.getString(keys1 + ".world")), spawnersConfig.getInt(keys1 + ".x"), spawnersConfig.getInt(keys1 + ".y"), spawnersConfig.getInt(keys1 + ".z"));
						Location loc2 = new Location(Bukkit.getServer().getWorld(spawnersConfig.getString(keys2 + ".world")), spawnersConfig.getInt(keys2 + ".x"), spawnersConfig.getInt(keys2 + ".y"), spawnersConfig.getInt(keys2 + ".z"));

						if(loc1 == loc2) {
							spawnersConfig.set(keys2, null);
							try {
								spawnersConfig.save(spawnersFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				
				@SuppressWarnings("unchecked")
				ArrayList<CustomMob> customMobsCopy = (ArrayList<CustomMob>) getCustomMobManager().getCustomMobs().clone();
				for (CustomMob mobs : customMobsCopy) {
					if(mobs.getVanillaEntity() == null && mobs.getVanillaEntity().isDead()) {
						getCustomMobManager().getCustomMobs().remove(mobs);
					}
				}

				File blockFile = new File(getDataFolder(), "/dat0/block.yml");
				if(!blockFile.exists()) {
					try {
						blockFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				YamlConfiguration blockConfig = YamlConfiguration.loadConfiguration(blockFile);

				if(blockFile.exists()) {
					if(!blockConfig.getKeys(false).isEmpty()) {
						for (String keys : blockConfig.getKeys(false)) {
							if(blockConfig.getInt(keys + ".timer") > 0) {
								blockConfig.set(keys + ".timer", Integer.valueOf(blockConfig.getInt(keys + ".timer") - 1));
							} else {
								World world = Bukkit.getServer().getWorld(blockConfig.getString(keys + ".location.world"));
								int x = blockConfig.getInt(keys + ".location.x");
								int y = blockConfig.getInt(keys + ".location.y");
								int z = blockConfig.getInt(keys + ".location.z");
								Block block = world.getBlockAt(new Location(world, x, y, z));
								Material type = Material.valueOf(blockConfig.getString(keys + ".type"));

								blockConfig.set(keys, null);
								if(((type == Material.WHEAT_SEEDS) || (type == Material.CARROT) || (type == Material.POTATO) || (type == Material.MELON)) && (block.getRelative(BlockFace.DOWN) != null) && (block.getRelative(BlockFace.DOWN).getType() == Material.FARMLAND)) {
									block.setType(type);
								} else {
									block.setType(type);
								}
							}
						}
						try {
							blockConfig.save(blockFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools")) {
					chestCount++;
					if(chestCount == getConfig().getInt("world.random-chest-seconds-cooldown")) {
						chestCount = 0;
						respawnChests();
					}
				}
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					if(player.getOpenInventory().getTopInventory().getTitle().startsWith("Trading:")) {
						String[] playersInTitle = ChatColor.stripColor(player.getOpenInventory().getTopInventory().getTitle().replace("Trading: ", "")).split(" - ");
						Player leftPlayer = Bukkit.getServer().getPlayer(playersInTitle[0]);
						Player rightPlayer = Bukkit.getServer().getPlayer(playersInTitle[1]);

						if(leftPlayer == player) {
							if(!inTrade.containsKey(rightPlayer)) {
								player.closeInventory();
							}
						} else if(rightPlayer == player) {
							if(!inTrade.containsKey(leftPlayer)) {
								player.closeInventory();
							}
						}
					}
				}
			}
		}, 0, 2);
		
		Thread petThread = new Thread() {
			public void run() {
				SubConfig subConfig = new SubConfig(SubConfig.TYPES.PETSTATS);
				YamlConfiguration config = subConfig.getConfig();
				
				Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getCore(), new Runnable() {
					public void run() {
						for(World worlds : Bukkit.getServer().getWorlds()) {
							for(Entity e : worlds.getEntities()) {
								if(e instanceof Wolf || e instanceof Horse) {
									NBTEntity nbtEntity = new NBTEntity(e);
									
									if(!nbtEntity.hasKey("hexPet")) {
										continue;
									}
									
									String type = e.getType().toString().toLowerCase().replace(" ", "_");
									int level = (int) Math.floor(e.getTicksLived() / config.getInt("pet-stats." + type + ".ticks-per-level")) + config.getInt("pet-stats." + type + ".starting-level");
									
									LivingEntity le = (LivingEntity) e;
									
									if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.pet-stats")) {
										le.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue() + (config.getInt("pet-stats." + type + ".health") * level));
										le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getDefaultValue() + (config.getInt("pet-stats." + type + ".movement-speed") * level));
										le.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(le.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getDefaultValue() + (config.getInt("pet-stats." + type + ".attack-damage") * level));
										le.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(le.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getDefaultValue() + (config.getInt("pet-stats." + type + ".attack-speed") * level));
									} else {
										le.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
										le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getDefaultValue());
										le.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(le.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getDefaultValue());
										le.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(le.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getDefaultValue());
									}
								}
							}
						}
					}
				}, 0, config.getInt("pet-stats.ticks"));
			}
		};
		petThread.start();

		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.tools")) {
			respawnChests();
		}
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private void respawnChests() {
		File file = new File(getDataFolder(), "/dat0/chests.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(config.isList("locations")) {
			for (Object l : config.getList("locations")) {
				Location locations = (Location) l;

				try {
					locations.getBlock().setType(Material.CHEST);
					locations.getBlock().getRelative(0, -2, 0).setType(Material.SIGN);

					Chest chest = (Chest) locations.getBlock().getState();
					chest.getBlockInventory().clear();

					Sign sign = (Sign) locations.getBlock().getRelative(0, -2, 0).getState();
					sign.setLine(0, "[Picklocking]");

					Random random = new Random();
					int r = random.nextInt(100) + 1;
					String type = "";
					if(r >= 1 && r <= 50) {
						type = "Regular";
					} else if(r >= 51 && r <= 83) {
						type = "Easy";
					} else if(r >= 84 && r <= 100) {
						type = "Advanced";
					}
					sign.setLine(1, type);
					sign.update();

					// Weapons and Armor
					ArrayList<ItemStack> chestItemList = new ArrayList<ItemStack>();
					for (ItemStack is : itemList) {
						ItemStack isCopy = is.clone();
						chestItemList.add(getStatsManager().checkStats(isCopy, 1, "none"));
					}
					// ---

					// Food 1.8+
					ItemStack chicken = new ItemStack(Material.CHICKEN, random.nextInt(4) + 1);
					ItemStack rawBeef = new ItemStack(Material.BEEF, random.nextInt(4) + 1);
					ItemStack wheat = new ItemStack(Material.WHEAT, random.nextInt(4) + 1);
					ItemStack carrot = new ItemStack(Material.CARROT, random.nextInt(4) + 1);
					ItemStack apple = new ItemStack(Material.APPLE, random.nextInt(4) + 1);

					chestItemList.add(chicken);
					chestItemList.add(rawBeef);
					chestItemList.add(wheat);
					chestItemList.add(carrot);
					chestItemList.add(apple);

					int additionalChance = 0;
					if(type.equalsIgnoreCase("Easy")) {
						additionalChance = 2;
					} else if(type.equalsIgnoreCase("Advanced")) {
						additionalChance = 5;
					}

					for (int i = 0; i < random.nextInt(3) + 1 + additionalChance; i++) {
						chest.getBlockInventory().setItem(random.nextInt(chest.getBlockInventory().getSize()), chestItemList.get(random.nextInt(chestItemList.size())));
						chest.update();
					}
				} catch (NullPointerException e) {
					continue;
				} catch (IllegalArgumentException e) {
					continue;
				}
			}
		}
	}

	public static int getNumbersFromString(String str) {
		String tempStr = "";
		for (int i = 0; i < str.length(); i++) {
			if(Character.isDigit(str.charAt(i))) {
				tempStr = tempStr + str.charAt(i);
			}
		}
		return Integer.parseInt(tempStr);
	}

	public StatsManager getStatsManager() {
		return this.statsManager;
	}

	public CustomMobManager getCustomMobManager() {
		return this.customMobManager;
	}

	public LangTools getLangTools() {
		return this.langTools;
	}

	public SQLManager getSQLManager() {
		return this.sqlManager;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if(economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	public Economy getEconomy() {
		return economy;
	}

	private ItemStack[] getScrapByItem(ItemStack clickedItem) {
		ArrayList<String> scrapLore = new ArrayList<String>();
		scrapLore.add(ChatColor.GRAY + "Drag and Drop on Equipment");
		scrapLore.add(ChatColor.GRAY + "Repairs 1-12 Durability");

		ItemStack clickedItemClone = clickedItem.clone();
		clickedItemClone.setItemMeta(null);
		clickedItemClone.setAmount(1);

		String scrapType = "";
		for (ItemStack is : itemMaterial.keySet()) {
			if(clickedItemClone.getType() == is.getType() && clickedItemClone.getDurability() == is.getDurability()) {
				scrapType = itemMaterial.get(is);
				break;
			}
		}

		ItemStack scrap = new ItemStack(Material.AIR);
		String scrapName = "[SCRAP_NAME]";

		String[] scrapTypeSplitter = new String[] { scrapType };
		if(scrapType.contains(":")) {
			scrapTypeSplitter = scrapType.split(":");
		}

		ItemStack[] scraps = new ItemStack[scrapTypeSplitter.length];

		for (int i = 0; i < scrapTypeSplitter.length; i++) {
			if(scrapTypeSplitter[i].equalsIgnoreCase("WOOD")) {
				scrap = new ItemStack(Material.LADDER, getScrapWorth(clickedItemClone));
				scrapName = "Wooden Scrap";
			} else if(scrapTypeSplitter[i].equalsIgnoreCase("MANACLOTH")) {
				scrap = new ItemStack(Material.DIAMOND_HOE, getScrapWorth(clickedItemClone), (short) 2);
				scrapName = "Manacloth Scrap";
			} else if(scrapTypeSplitter[i].equalsIgnoreCase("IRON")) {
				scrap = new ItemStack(Material.LIGHT_GRAY_DYE, getScrapWorth(clickedItemClone));
				scrapName = "Iron Scrap";
			} else if(scrapTypeSplitter[i].equalsIgnoreCase("GOLD")) {
				scrap = new ItemStack(Material.DANDELION_YELLOW, getScrapWorth(clickedItemClone));
				scrapName = "Gold Scrap";
			} else if(scrapTypeSplitter[i].equalsIgnoreCase("CHAINMAIL")) {
				scrap = new ItemStack(Material.IRON_BARS, getScrapWorth(clickedItemClone));
				scrapName = "Chainmail Scrap";
			} else if(scrapTypeSplitter[i].equalsIgnoreCase("CRYSTAL")) {
				scrap = new ItemStack(Material.LIGHT_BLUE_DYE, getScrapWorth(clickedItemClone));
				scrapName = "Crystal Scrap";
			} else if(scrapTypeSplitter[i].equalsIgnoreCase("DRAGONSCALE")) {
				scrap = new ItemStack(Material.DIAMOND_HOE, getScrapWorth(clickedItemClone), (short) 4);
				scrapName = "Dragonscale";
			} else if(scrapTypeSplitter[i].equalsIgnoreCase("DIAMOND")) {
				scrap = new ItemStack(Material.LIGHT_BLUE_DYE, getScrapWorth(clickedItemClone), (short) 12);
				scrapName = "Diamond Scrap";
			}
			
			if(scrap.getType() == Material.AIR) {
				continue;
			}
			
			ItemMeta scrapMeta = scrap.getItemMeta();
			scrapMeta.setDisplayName(ChatColor.GREEN + scrapName);
			scrapMeta.setLore(scrapLore);

			for (ItemFlag flags : ItemFlag.values()) {
				scrapMeta.addItemFlags(flags);
			}
			
			scrapMeta.setUnbreakable(true);

			scrap.setItemMeta(scrapMeta);

			scraps[i] = scrap;
		}

		return scraps;
	}

	@SuppressWarnings({ "static-access" })
	private int getScrapWorth(ItemStack item) {
		File itemConversionFile = new File(getDataFolder(), "itemConversion.yml");
		YamlConfiguration itemConversionConfig = YamlConfiguration.loadConfiguration(itemConversionFile);
		
		String configPath = item.getType().getKey().MINECRAFT + "." + item.getDurability() + ".scraps.amount";
		return itemConversionConfig.isInt(configPath) ? itemConversionConfig.getInt(configPath) : 0;
	}
	
	public void openMenu(Player player, String type) {
		if(type.equalsIgnoreCase("CLASS")) {
			type = "classes";
		} else if(type.equalsIgnoreCase("RACE")) {
			type = "races";
		}
		
		Inventory classInv = Bukkit.getServer().createInventory(player, Plugin.getCore().getConfig().getInt(type + ".menu.rows") * 9, Plugin.getCore().getConfig().getString(type + ".menu.title"));
		
		for(String itemKeys : Plugin.getCore().getConfig().getConfigurationSection(type + ".menu.items").getKeys(false)) {
			String itemPath = type + ".menu.items." + itemKeys;
			
			String materialName = Plugin.getCore().getConfig().getString(itemPath + ".type");
			String spigotMaterialName = materialName.split(":")[1];
			
			ItemStack item = new ItemStack(Material.matchMaterial(spigotMaterialName), Plugin.getCore().getConfig().getInt(itemPath + ".amount"), (short) Plugin.getCore().getConfig().getInt(itemPath + ".data"));
			
			if(item.getType() == Material.SKELETON_SKULL && item.getDurability() == 3 && !Plugin.getCore().getConfig().getString(itemPath + ".skull-name").isEmpty()) {
				SkullMeta skullItemMeta = (SkullMeta) item.getItemMeta();
				skullItemMeta.setOwningPlayer(UUIDTools.getOfflinePlayerByName(Plugin.getCore().getConfig().getString(itemPath + ".skull-name")));
				item.setItemMeta(skullItemMeta);
			}
			
			ItemMeta itemMeta = item.getItemMeta();
			if(!Plugin.getCore().getConfig().getString(itemPath + ".item-name").isEmpty()) {
				itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Plugin.getCore().getConfig().getString(itemPath + ".item-name")));
			}
			
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			
			item.setItemMeta(itemMeta);
			classInv.setItem(Plugin.getCore().getConfig().getInt(itemPath + ".slot") - 1, item);
		}
		
		player.openInventory(classInv);
	}
	
	public ArrayList<String> ensmallerString(String str, int characters, ChatColor color) {
		ArrayList<String> lore = new ArrayList<String>();
		String lineMsg = "";
		
		if(str.isEmpty()) {
			return lore;
		}
		
		for(int i=0;i<str.length();i++) {
			lineMsg += str.charAt(i);
			
			if((lineMsg.length() > characters && Character.isSpaceChar(str.charAt(i))) || i + 1 == str.length()) {
				while(lineMsg.startsWith(" ")) {
					lineMsg = lineMsg.substring(1);
				}
				
				if(color == null) {
					color = ChatColor.WHITE;
				}
				lore.add(color + lineMsg);
				
				lineMsg = "";
			}
		}
		
		return lore;
	}
	
	public OfflinePlayer getOfflinePlayerByName(String name) {
		for(OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
			if(player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;
	}
}