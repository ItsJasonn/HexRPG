package me.ItsJasonn.HexRPG.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tr7zw.itemnbtapi.NBTItem;
import me.ItsJasonn.HexRPG.Main.Plugin;

public class StatsManager {
	private HashMap<Player, Integer> playerMaxHealth = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerDefense = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerDMG = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerDPS = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerAgility = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerStrength = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerEndurance = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerDexterity = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerCriticalChance = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerCriticalDamage = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerLifesteal = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> playerDropChance = new HashMap<Player, Integer>();
	
	private HashMap<String, Double> hpRegenerationCount = new HashMap<String, Double>();
	
	@SuppressWarnings({ "static-access" })
	public ItemStack checkStats(ItemStack item, int tier, String presetQuality) {
		YamlConfiguration statsConfig = new SubConfig(SubConfig.TYPES.STATS).getConfig();
		
		Random r = new Random();

		int dmgStat = 0;
		int HPstats = 0;
		int defenseStats = 0;
		int dpsStats = 0;
		int hpRegen = 0;
		int damageStats = 0;
		int agilityStats = 0;
		int strengthStats = 0;
		int enduranceStats = 0;
		int dexterityStats = 0;
		int critChance = 0;
		int critDamage = 0;
		int lifesteal = 0;
		
		String quality = "null";
		
		if(presetQuality.equalsIgnoreCase("none")) {
			int rChance = r.nextInt(100) + 1;
			int previousTotalChance = 0;
			
			for (String keys : statsConfig.getConfigurationSection("stats.quality-chances").getKeys(false)) {
				int currentChance = statsConfig.getInt("stats.quality-chances." + keys);
				previousTotalChance += currentChance;

				if(rChance <= previousTotalChance) {
					quality = WordUtils.capitalizeFully(keys);
					break;
				}
			}
		} else {
			quality = WordUtils.capitalizeFully(presetQuality);
		}

		int differenceDmgStat = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-damage") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-damage");
		int differenceHpStat = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-hp") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-hp");
		int differenceDefenseStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-defense") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-defense");
		int differenceDpsStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-additional-damage") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-additional-damage");
		int differenceHpRegen = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-hp-regen") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-hp-regen");
		int differenceDamageStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-damage") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-damage");
		int differenceAgilityStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-agility") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-agility");
		int differenceStrengthStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-strength") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-strength");
		int differenceEnduranceStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-endurance") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-endurance");
		int differenceDexterityStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-dexterity") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-dexterity");
		int differenceCritChance = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-crit-chance") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-crit-chance");
		int differenceCritDamage = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-crit-damage") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-crit-damage");
		int differenceLifesteal = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".max-lifesteal") - statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-lifesteal");
		
		if(differenceDmgStat > 0) {
			dmgStat = r.nextInt(differenceDmgStat) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-damage");
		} else {
			dmgStat = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-damage");
		}

		if(differenceHpStat > 0) {
			HPstats = r.nextInt(differenceHpStat) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-hp");
		} else {
			HPstats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-hp");
		}

		if(differenceDefenseStats > 0) {
			defenseStats = r.nextInt(differenceDefenseStats) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-defense");
		} else {
			defenseStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-defense");
		}

		if(differenceDpsStats > 0) {
			dpsStats = r.nextInt(differenceDpsStats) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-additional-damage");
		} else {
			dpsStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-additional-damage");
		}

		if(differenceHpRegen > 0) {
			hpRegen = r.nextInt(differenceHpRegen) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-hp-regen");
		} else {
			hpRegen = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-hp-regen");
		}

		if(differenceDamageStats > 0) {
			damageStats = r.nextInt(differenceDamageStats) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-damage");
		} else {
			damageStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-damage");
		}

		if(differenceAgilityStats > 0) {
			agilityStats = r.nextInt(differenceAgilityStats) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-agility");
		} else {
			agilityStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-agility");
		}

		if(differenceStrengthStats > 0) {
			strengthStats = r.nextInt(differenceStrengthStats) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-strength");
		} else {
			strengthStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-strength");
		}

		if(differenceEnduranceStats > 0) {
			enduranceStats = r.nextInt(differenceEnduranceStats) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-endurance");
		} else {
			enduranceStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-endurance");
		}

		if(differenceDexterityStats > 0) {
			dexterityStats = r.nextInt(differenceDexterityStats) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-dexterity");
		} else {
			dexterityStats = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-dexterity");
		}

		if(differenceCritChance > 0) {
			critChance = r.nextInt(differenceCritChance) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-crit-chance");
		} else {
			critChance = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-crit-chance");
		}

		if(differenceCritDamage > 0) {
			critDamage = r.nextInt(differenceCritDamage) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-crit-damage");
		} else {
			critDamage = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-crit-damage");
		}

		if(differenceLifesteal > 0) {
			lifesteal = r.nextInt(differenceLifesteal) + statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-lifesteal");
		} else {
			lifesteal = statsConfig.getInt("stats.tier-" + tier + "." + quality.toLowerCase() + ".min-lifesteal");
		}

		if(item.getType() == Material.BOW) {
			if(damageStats < statsConfig.getInt("stats.minimum-damage.ranged")) {
				damageStats = statsConfig.getInt("stats.minimum-damage.ranged");
			}
		} else {
			if(damageStats < statsConfig.getInt("stats.minimum-damage.melee")) {
				damageStats = statsConfig.getInt("stats.minimum-damage.melee");
			}
		}
		
		int itemType = -1;
		for (ItemStack armor : Plugin.getCore().armorList) {
			if(armor.getType() == item.getType() && armor.getDurability() == item.getDurability()) {
				itemType = 0;
			}
		}
		for (ItemStack weapon : Plugin.getCore().weaponList) {
			if(weapon.getType() == item.getType() && weapon.getDurability() == item.getDurability()) {
				itemType = 1;
			}
		}
		
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setInteger("hexUpgrades_a", 0);
		
		if(itemType == 0) {
			nbtItem.setInteger("hexDPS_a", dpsStats);
			nbtItem.setInteger("hexDefense_a", defenseStats);
			nbtItem.setInteger("hexHP_a", HPstats);
			nbtItem.setInteger("hexRegeneration_a", hpRegen);
			
			nbtItem.setInteger("hexAgility_a", agilityStats);
			nbtItem.setInteger("hexStrength_a", strengthStats);
			nbtItem.setInteger("hexEndurance_a", enduranceStats);
			nbtItem.setInteger("hexDexterity_a", dexterityStats);
		} else if(itemType == 1) {
			nbtItem.setInteger("hexDamage_a", dmgStat);
			nbtItem.setInteger("hexCriticalChance_a", critChance);
			nbtItem.setInteger("hexCriticalDamage_a", critDamage);
			nbtItem.setInteger("hexLifesteal_a", lifesteal);
		}
		
		HashMap<String, Integer> potentialList = new HashMap<String, Integer>();
		potentialList.put("damage", r.nextInt(3) + 1);
		potentialList.put("defense", r.nextInt(3) + 1);
		potentialList.put("hp", r.nextInt(3) + 1);
		potentialList.put("regeneration", r.nextInt(3) + 1);
		potentialList.put("agility", r.nextInt(3) + 1);
		potentialList.put("strength", r.nextInt(3) + 1);
		potentialList.put("endurance", r.nextInt(3) + 1);
		potentialList.put("dexterity", r.nextInt(3) + 1);
		potentialList.put("lifesteal", r.nextInt(3) + 1);
		
		for(int i=0;i<3;i++) {
			int rIndex = r.nextInt(potentialList.size());
			String key = new ArrayList<>(potentialList.keySet()).get(rIndex);
			
			nbtItem.setInteger("pot" + (i + 1) + "_" + key, potentialList.get(key));
		}
		
		nbtItem.setString("hexQuality_a", quality);
		nbtItem.setInteger("hexDurability_a", statsConfig.getInt("stats.max-durability"));
		
		if(itemType == 1) {
			int level = statsConfig.getInt("stats.quality-levels." + quality.toLowerCase());
			nbtItem.setInteger("hexRequiredLevel_a", level);
		}
		
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.abilities")) {
			int rAbility = r.nextInt(Plugin.getCore().getConfig().getConfigurationSection("abilities").getKeys(false).size());
			ArrayList<String> abilities = new ArrayList<String>();
			abilities.addAll(Plugin.getCore().getConfig().getConfigurationSection("abilities").getKeys(false));
			String ability = abilities.get(rAbility);
			
			nbtItem.setString("hexAbility_a", ability);
		}
		
		item = nbtItem.getItem();
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(resetDisplayName(item));
		itemMeta.setLore(resetStatsLore(item));
		
		itemMeta.setUnbreakable(true);
		
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if(tier == 2) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		item.setItemMeta(itemMeta);
		
		if(tier == 2) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		return item;
	}

	public void setUnidentified(ItemStack item) {
		YamlConfiguration scrollsConfig = new SubConfig(SubConfig.TYPES.SCROLLS).getConfig();
		
		ItemMeta itemMeta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();

		String name = getRPGName(item);
		itemMeta.setDisplayName(ChatColor.WHITE + Plugin.getCore().getLangTools().getMessage("stats.translation.unidentified") + " " + name);
		lore.add(ChatColor.GRAY + scrollsConfig.getString("description.unidentified-item"));
		itemMeta.setLore(lore);
		
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

		item.setItemMeta(itemMeta);
	}

	public boolean isUnidentified(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith(ChatColor.WHITE + Plugin.getCore().getLangTools().getMessage("stats.translation.unidentified"));
	}

	public String getRPGName(ItemStack item) {
		String name = WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));
		
		File itemConversionFile = new File(Plugin.getCore().getDataFolder(), "itemConversion.yml");
		YamlConfiguration itemConversionConfig = YamlConfiguration.loadConfiguration(itemConversionFile);
		
		String materialName = "minecraft:" + item.getType().getKey().getKey();
		String durability = Integer.toString(item.getDurability());
		
		if(itemConversionConfig.isConfigurationSection(materialName + "." + durability)) {
			return ChatColor.translateAlternateColorCodes('&', itemConversionConfig.getString(materialName + "." + durability + ".name"));
		}
		
		return name;
	}
	
	@SuppressWarnings("static-access")
	public boolean isCustomItem(ItemStack is) {
		if(is == null || is.getType() == Material.AIR) {
			return false;
		}
		
		for(ItemStack items : Plugin.getCore().itemList) {
			if(is.getType() == items.getType() && is.getDurability() == items.getDurability()) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("static-access")
	public boolean isWeapon(ItemStack is) {
		if(is == null || is.getType() == Material.AIR) {
			return false;
		}
		
		for(ItemStack items : Plugin.getCore().weaponList) {
			if(is.getType() == items.getType() && is.getDurability() == items.getDurability()) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("static-access")
	public boolean isArmor(ItemStack is) {
		if(is == null || is.getType() == Material.AIR) {
			return false;
		}
		
		for(ItemStack items : Plugin.getCore().armorList) {
			if(is.getType() == items.getType() && is.getDurability() == items.getDurability()) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("static-access")
	public boolean isThrowable(ItemStack is) {
		if(is == null || is.getType() == Material.AIR) {
			return false;
		}
		
		for(ItemStack items : Plugin.getCore().throwableList) {
			if(is.getType() == items.getType() && is.getDurability() == items.getDurability()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isTool(ItemStack is) {
		if(is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
			String type = is.getItemMeta().getDisplayName().trim();
			return Plugin.getCore().toolList.contains(type);
		}
		return false;
	}
	
	public boolean isScroll(ItemStack is) {
		try {
			YamlConfiguration scrollsConfig = new SubConfig(SubConfig.TYPES.SCROLLS).getConfig();
			
			if(is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
				String itemName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
				
				for(String scrollNames : scrollsConfig.getConfigurationSection("additionals").getKeys(false)) {
					String itemNameConfigReference = itemName.substring(0, itemName.lastIndexOf(Plugin.getCore().getLangTools().getUncoloredMessage("scrolls.translation.scroll")) - 1);
					
					if(scrollNames.equalsIgnoreCase(itemNameConfigReference)) {
						return true;
					}
				}
			}
		} catch(StringIndexOutOfBoundsException e) {
			return false;
		}
		return false;
	}
	
	public void giveArmorSetEffect(Player player, String armorType) {
		int additionalDamage = Plugin.getCore().getConfig().getInt("inventory.armor-set-effects." + armorType + ".additional-damage");
		int additionalDefense = Plugin.getCore().getConfig().getInt("inventory.armor-set-effects." + armorType + ".additional-defense");
		int additionalDropRate = Plugin.getCore().getConfig().getInt("inventory.armor-set-effects." + armorType + ".additional-drop-rate");

		if(additionalDamage > 0) {
			getPlayerDPS().put(player, getPlayerDPS().get(player) * ((additionalDamage / 100) + 1));
		}
		if(additionalDefense > 0) {
			getPlayerDefense().put(player, getPlayerDefense().get(player) * ((additionalDefense / 100) + 1));
		}
		if(additionalDropRate > 0) {
			getPlayerDropChance().put(player, getPlayerDropChance().get(player) * ((additionalDropRate / 100) + 1));
		}

		if(!Plugin.getCore().getConfig().getString("inventory.armor-set-effects." + armorType + ".effect.type").equalsIgnoreCase("none")) {
			PotionEffectType type = PotionEffectType.getByName(Plugin.getCore().getConfig().getString("inventory.armor-set-effects." + armorType + ".effect.type").toUpperCase().replace(" ", "_"));
			int level = Plugin.getCore().getConfig().getInt("inventory.armor-set-effects." + armorType + ".effect.level");
			
			if(level > 0) {
				player.addPotionEffect(new PotionEffect(type, 60, level - 1));
			}
		}
	}

	private String getColorByQuality(String quality) {
		quality = quality.toLowerCase();

		if(Plugin.getCore().getConfig().isString("stats.quality-colors." + quality)) {
			return Plugin.getCore().getConfig().getString("stats.quality-colors." + quality).replace("&", "§");
		}
		return "§f";
	}

	public ArrayList<String> getQualities() {
		ArrayList<String> qualities = new ArrayList<String>();
		for (String keys : Plugin.getCore().getConfig().getConfigurationSection("stats.quality-chances").getKeys(false)) {
			qualities.add(keys.toUpperCase());
		}

		return qualities;
	}

	public String getClass(OfflinePlayer player) {
		YamlConfiguration config = new SubConfig(SubConfig.TYPES.PLAYERSTATS).getConfig();
		return config.getString(player.getUniqueId().toString() + ".CLASS");
	}

	public String getRace(OfflinePlayer player) {
		YamlConfiguration config = new SubConfig(SubConfig.TYPES.PLAYERSTATS).getConfig();
		return config.getString(player.getUniqueId().toString() + ".RACE");
	}

	public int getWeaponLevel(ItemStack weapon) {
		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.required-level")) {
			if(isWeapon(weapon)) {
				NBTItem nbtItem = new NBTItem(weapon);
				return nbtItem.getInteger("hexRequiredLevel_a");
			}
		}

		return 0;
	}

	public void generateStatsData(OfflinePlayer player) {
		File statsFile = new SubConfig(SubConfig.TYPES.PLAYERSTATS).getFile();
		YamlConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);

		if(!statsConfig.isConfigurationSection(player.getUniqueId().toString())) {
			statsConfig.set(player.getUniqueId().toString() + ".CLASS", "NONE");
			statsConfig.set(player.getUniqueId().toString() + ".RACE", "NONE");
		} else {
			if(!statsConfig.isString(player.getUniqueId().toString() + ".CLASS")) {
				statsConfig.set(player.getUniqueId().toString() + ".CLASS", "NONE");
			}
			if(!statsConfig.isString(player.getUniqueId().toString() + ".RACE")) {
				statsConfig.set(player.getUniqueId().toString() + ".RACE", "NONE");
			}
		}
		
		try {
			statsConfig.save(statsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File levelFile = new File(Plugin.getCore().getDataFolder() + "/dat0/levels/", player.getUniqueId().toString() + ".yml");
		YamlConfiguration levelConfig = YamlConfiguration.loadConfiguration(levelFile);
		
		a("WOODCUTTING", levelConfig);
		a("FARMING", levelConfig);
		a("MINING", levelConfig);
		a("HITPOINTS", levelConfig);
		a("FISHING", levelConfig);
		a("SMITHING", levelConfig);
		
		if(!levelConfig.isInt("SKILLS.MASTER")) {
			levelConfig.set("SKILLS.MASTER", 6);
		}
		
		try {
			levelConfig.save(levelFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(player.isOnline()) {
			Player onlinePlayer = player.getPlayer();
			
			PlayerLevel level = new PlayerLevel(onlinePlayer);
			if(!level.hasLevel()) {
				level.setLevel(Plugin.getCore().getConfig().getInt("leveling.starting-level"));
				level.setExp(Plugin.getCore().getConfig().getInt("leveling.starting-exp"));
			}
		}
	}

	public double getDamage(LivingEntity damager, LivingEntity entity, double damageDone) {
		YamlConfiguration statsConfig = new SubConfig(SubConfig.TYPES.STATS).getConfig();
		
		int additionalDexterity = 0;
		int additionalEndurance = 0;
		
		if(damager instanceof Player) {
			Player damagerP = (Player) damager;

			additionalDexterity = getPlayerDexterity().get(damager) / 2;
			additionalEndurance = getPlayerEndurance().get(damager) / 10;
			if(damagerP.getInventory().getItemInOffHand().getType() != Material.BOW) {
				additionalDexterity = 0;
			} else {
				damageDone /= 2;
				additionalEndurance = 0;
			}
		}
		
		double damage = 0;
		
		int percentDPS = 0;
		if(damager instanceof Player) {
			Player damagerP = (Player) damager;

			damage = getPlayerDMG().get(damagerP) + (getPlayerStrength().get(damagerP) / 10) + additionalDexterity + additionalEndurance;
			percentDPS = getPlayerDPS().get(damagerP);
		} else if(damager instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) damager;
			if(Plugin.getCore().getConfig().getConfigurationSection("leveling.gained-exp").getKeys(false).contains(le.getType().toString())) {
				damage = Plugin.getCore().getConfig().getInt("leveling.gained-exp." + le.getType().toString());
			} else {
				damage = Plugin.getCore().getConfig().getInt("leveling.gained-exp.DEFAULT");
			}
		}
		
		int percentDefense = 0;
		int additionalCritical = 0;
		if(entity instanceof Player) {
			Player entityP = (Player) entity;
			
			percentDefense = 0;
			if(getPlayerDefense().containsKey(entityP)) {
				percentDefense = getPlayerDefense().get(entityP);
			}
			
			if(getPlayerCriticalChance().containsKey(entityP) && getPlayerCriticalDamage().containsKey(entityP)) {
				Random random = new Random();
				int r = random.nextInt(100) + 1;
				if(r >= 1 && r <= getPlayerCriticalChance().get(entityP)) {
					additionalCritical = getPlayerCriticalDamage().get(entityP);
				}
			}
		}
		
		damage = (damageDone - damageDone / 100.0D * percentDefense) + damageDone / 100.0D * (percentDPS + additionalCritical);

		if(damager instanceof Player) {
			Player damagerP = (Player) damager;
			
			damage = damageDone * Plugin.getCore().getConfig().getDouble("classes." + getClass(damagerP).toString().toLowerCase() + ".melee");
			damage = damageDone + Plugin.getCore().getConfig().getDouble("races." + getClass(damagerP).toString().toLowerCase() + ".melee");

			if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.required-level")) {
				PlayerLevel playerLevel = new PlayerLevel(damagerP);
				if(Plugin.getCore().getStatsManager().getWeaponLevel(damagerP.getInventory().getItemInOffHand()) > playerLevel.getLevel()) {
					return 0;
				}
			}
			
			if(damagerP.getInventory().getItemInOffHand().getType() == Material.BOW) {
				if(damage < statsConfig.getInt("stats.minimum-damage.ranged")) {
					damage = statsConfig.getInt("stats.minimum-damage.ranged");
				}
			} else {
				if(damage < statsConfig.getInt("stats.minimum-damage.melee")) {
					damage = statsConfig.getInt("stats.minimum-damage.melee");
				}
			}
		}
		
		if(damager instanceof Player) {
			Player damagerP = (Player) damager;

			if((damagerP.getInventory().getItemInMainHand() == null || damagerP.getInventory().getItemInMainHand().getType().equals(Material.AIR)) &&
					(damagerP.getInventory().getItemInOffHand() == null || damagerP.getInventory().getItemInOffHand().getType().equals(Material.AIR))) {
				damage = statsConfig.getInt("stats.fist-damage");
			}
		}
		
		return damage;
	}
	
	public ItemStack applyAncientScroll(Player player, ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		int currentDamage = nbtItem.hasKey("hexDamage_b") ? nbtItem.getInteger("hexDamage_b") : nbtItem.getInteger("hexDamage_a");
		nbtItem.setInteger("hexDamage_b", currentDamage + 2);
		nbtItem.setInteger("hexUpgrades_a", nbtItem.getInteger("hexUpgrades_a") + 1);
		
		item = nbtItem.getItem();
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(resetDisplayName(item));
		meta.setLore(resetStatsLore(item));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack applyChaosScroll(Player player, ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		Random random = new Random();
		
		if(isWeapon(item)) {
			int currentDamage = nbtItem.hasKey("hexDamage_b") ? nbtItem.getInteger("hexDamage_b") : nbtItem.getInteger("hexDamage_a");
			int currentCriticalChance = nbtItem.hasKey("hexCriticalChance_b") ? nbtItem.getInteger("hexCriticalChance_b") : nbtItem.getInteger("hexCriticalChance_a");
			int currentCriticalDamage = nbtItem.hasKey("hexCriticalDamage_b") ? nbtItem.getInteger("hexCriticalDamage_b") : nbtItem.getInteger("hexCriticalDamage_a");
			int currentLifesteal = nbtItem.hasKey("hexLifesteal_b") ? nbtItem.getInteger("hexLifesteal_b") : nbtItem.getInteger("hexLifesteal_a");
			
			nbtItem.setInteger("hexDamage_b", currentDamage + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexCriticalChance_b", currentCriticalChance + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexCriticalDamage_b", currentCriticalDamage + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexLifesteal_b", currentLifesteal + (random.nextInt(3) + 1));
		} else if(isArmor(item)) {
			int currentDamage = nbtItem.hasKey("hexDPS_b") ? nbtItem.getInteger("hexDPS_b") : nbtItem.getInteger("hexDPS_a");
			int currentDefense = nbtItem.hasKey("hexDefense_b") ? nbtItem.getInteger("hexDefense_b") : nbtItem.getInteger("hexDefense_a");
			int currentHP = nbtItem.hasKey("hexHP_b") ? nbtItem.getInteger("hexHP_b") : nbtItem.getInteger("hexHP_a");
			int currentRegeneration = nbtItem.hasKey("hexRegeneration_b") ? nbtItem.getInteger("hexRegeneration_b") : nbtItem.getInteger("hexRegeneration_a");
			int currentAgility = nbtItem.hasKey("hexAgility_b") ? nbtItem.getInteger("hexAgility_b") : nbtItem.getInteger("hexAgility_a");
			int currentStrength = nbtItem.hasKey("hexStrength_b") ? nbtItem.getInteger("hexStrength_b") : nbtItem.getInteger("hexStrength_a");
			int currentEndurance = nbtItem.hasKey("hexEndurance_b") ? nbtItem.getInteger("hexEndurance_b") : nbtItem.getInteger("hexEndurance_a");
			int currentDexterity = nbtItem.hasKey("hexDexterity_b") ? nbtItem.getInteger("hexDexterity_b") : nbtItem.getInteger("hexDexterity_a");
			
			nbtItem.setInteger("hexDPS_b", currentDamage + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexDefense_b", currentDefense + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexHP_b", currentHP + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexRegeneration_b", currentRegeneration + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexAgility_b", currentAgility + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexStrength_b", currentStrength + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexEndurance_b", currentEndurance + (random.nextInt(3) + 1));
			nbtItem.setInteger("hexDexterity_b", currentDexterity + (random.nextInt(3) + 1));
		}
		nbtItem.setInteger("hexUpgrades_a", nbtItem.getInteger("hexUpgrades_a") + 1);
		
		item = nbtItem.getItem();
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(resetDisplayName(item));
		meta.setLore(resetStatsLore(item));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack applyPrimeScroll(Player player, ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		
		int currentDamage = nbtItem.hasKey("hexDamage_b") ? nbtItem.getInteger("hexDamage_b") : nbtItem.getInteger("hexDamage_a");
		int currentLifesteal = nbtItem.hasKey("hexLifesteal_b") ? nbtItem.getInteger("hexLifesteal_b") : nbtItem.getInteger("hexLifesteal_a");
		
		nbtItem.setInteger("hexDamage_b", currentDamage + 10);
		nbtItem.setInteger("hexLifesteal_b", currentLifesteal + 4);
		nbtItem.setInteger("hexUpgrades_a", nbtItem.getInteger("hexUpgrades_a") + 1);
		
		item = nbtItem.getItem();
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(resetDisplayName(item));
		meta.setLore(resetStatsLore(item));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack applyDarkScroll(Player player, ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		int currentHP = nbtItem.hasKey("hexHP_b") ? nbtItem.getInteger("hexHP_b") : nbtItem.getInteger("hexHP_a");
		nbtItem.setInteger("hexHP_b", currentHP + 10);
		nbtItem.setInteger("hexUpgrades_a", nbtItem.getInteger("hexUpgrades_a") + 1);
		
		item = nbtItem.getItem();
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(resetDisplayName(item));
		meta.setLore(resetStatsLore(item));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public String resetDisplayName(ItemStack item) {
		YamlConfiguration statsConfig = new SubConfig(SubConfig.TYPES.STATS).getConfig();
		
		NBTItem nbtItem = new NBTItem(item);
		
		String name = getRPGName(item);
		String quality = nbtItem.getString("hexQuality_a");
		
		String secondaryColor = ChatColor.translateAlternateColorCodes('&', statsConfig.getString("stats.stats-colors.secondary"));
		String tertiaryColor = ChatColor.translateAlternateColorCodes('&', statsConfig.getString("stats.stats-colors.tertiary"));
		
		String stars = "";
		int index = 0;
		for(String keys : statsConfig.getConfigurationSection("stats.tier-1").getKeys(false)) {
			if(keys.equalsIgnoreCase(quality)) {
				stars += secondaryColor + "★";
				for(int i=0;i<statsConfig.getConfigurationSection("stats.tier-1").getKeys(false).size() - index - 1;i++) {
					stars += tertiaryColor + "★";
				}
				break;
			} else {
				stars += secondaryColor + "★";
			}
				
			index++;
		}
		
		String color = getColorByQuality(quality) + ChatColor.UNDERLINE;
		return stars + " " + color + name + tertiaryColor + " (+" + nbtItem.getInteger("hexUpgrades_a") + ")";
	}
	
	public ArrayList<String> resetStatsLore(ItemStack item) {
		YamlConfiguration statsConfig = new SubConfig(SubConfig.TYPES.STATS).getConfig();
		
		NBTItem nbtItem = new NBTItem(item);
		
		ArrayList<String> lore = new ArrayList<String>();
		int itemType = isArmor(item) ? 0 : 1;
		
		String primaryColor = ChatColor.translateAlternateColorCodes('&', statsConfig.getString("stats.stats-colors.primary"));
		String secondaryColor = ChatColor.translateAlternateColorCodes('&', statsConfig.getString("stats.stats-colors.secondary"));
		String tertiaryColor = ChatColor.translateAlternateColorCodes('&', statsConfig.getString("stats.stats-colors.tertiary"));
		
		if(itemType == 0) {
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.damage") + ": " + secondaryColor + (nbtItem.hasKey("hexDPS_b") ? nbtItem.getInteger("hexDPS_b") : nbtItem.getInteger("hexDPS_a")) + "% " + tertiaryColor + "(" + nbtItem.getInteger("hexDPS_a") + "% + " + (nbtItem.hasKey("hexDPS_b") ? nbtItem.getInteger("hexDPS_b") - nbtItem.getInteger("hexDPS_a") : 0) + "%)");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.defense") + ": " + secondaryColor + (nbtItem.hasKey("hexDefense_b") ? nbtItem.getInteger("hexDefense_b") : nbtItem.getInteger("hexDefense_a")) + "% " + tertiaryColor + "(" + nbtItem.getInteger("hexDefense_a") + "% + " + (nbtItem.hasKey("hexDefense_b") ? nbtItem.getInteger("hexDefense_b") - nbtItem.getInteger("hexDefense_a") : 0) + "%)");
			lore.add("");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.hp") + ": " + secondaryColor + (nbtItem.hasKey("hexHP_b") ? nbtItem.getInteger("hexHP_b") : nbtItem.getInteger("hexHP_a")) + " " + tertiaryColor + "(" + nbtItem.getInteger("hexHP_a") + " + " + (nbtItem.hasKey("hexHP_b") ? nbtItem.getInteger("hexHP_b") - nbtItem.getInteger("hexHP_a") : 0) + ")");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.regeneration") + ": " + secondaryColor + (nbtItem.hasKey("hexRegeneration_b") ? nbtItem.getInteger("hexRegeneration_b") : nbtItem.getInteger("hexRegeneration_a")) + "% " + tertiaryColor + "(" + nbtItem.getInteger("hexRegeneration_a") + "% + " + (nbtItem.hasKey("hexRegeneration_b") ? nbtItem.getInteger("hexRegeneration_b") - nbtItem.getInteger("hexRegeneration_a") : 0) + "%)");
			lore.add("");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.agility") + ": " + secondaryColor + (nbtItem.hasKey("hexAgility_b") ? nbtItem.getInteger("hexAgility_b") : nbtItem.getInteger("hexAgility_a")) + " " + tertiaryColor + "(" + nbtItem.getInteger("hexAgility_a") + " + " + (nbtItem.hasKey("hexAgility_b") ? nbtItem.getInteger("hexAgility_b") - nbtItem.getInteger("hexAgility_a") : 0) + ")");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.strength") + ": " + secondaryColor + (nbtItem.hasKey("hexStrength_b") ? nbtItem.getInteger("hexStrength_b") : nbtItem.getInteger("hexStrength_a")) + " " + tertiaryColor + "(" + nbtItem.getInteger("hexStrength_a") + " + " + (nbtItem.hasKey("hexStrength_b") ? nbtItem.getInteger("hexStrength_b") - nbtItem.getInteger("hexStrength_a") : 0) + ")");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.endurance") + ": " + secondaryColor + (nbtItem.hasKey("hexEndurance_b") ? nbtItem.getInteger("hexEndurance_b") : nbtItem.getInteger("hexEndurance_a")) + " " + tertiaryColor + "(" + nbtItem.getInteger("hexEndurance_a") + " + " + (nbtItem.hasKey("hexEndurance_b") ? nbtItem.getInteger("hexEndurance_b") - nbtItem.getInteger("hexEndurance_a") : 0) + ")");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.dexterity") + ": " + secondaryColor + (nbtItem.hasKey("hexDexterity_b") ? nbtItem.getInteger("hexDexterity_b") : nbtItem.getInteger("hexDexterity_a")) + " " + tertiaryColor + "(" + nbtItem.getInteger("hexDexterity_a") + " + " + (nbtItem.hasKey("hexDexterity_b") ? nbtItem.getInteger("hexDexterity_b") - nbtItem.getInteger("hexDexterity_a") : 0) + ")");
		} else if(itemType == 1) {
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.damage") + ": " + secondaryColor + (nbtItem.hasKey("hexDamage_b") ? nbtItem.getInteger("hexDamage_b") : nbtItem.getInteger("hexDamage_a")) + " " + tertiaryColor + "(" + nbtItem.getInteger("hexDamage_a") + " + " + (nbtItem.hasKey("hexDamage_b") ? nbtItem.getInteger("hexDamage_b") - nbtItem.getInteger("hexDamage_a") : 0) + ")");
			lore.add("");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.critical-chance") + ": " + secondaryColor + (nbtItem.hasKey("hexCriticalChance_b") ? nbtItem.getInteger("hexCriticalChance_b") : nbtItem.getInteger("hexCriticalChance_a")) + "% " + tertiaryColor + "(" + nbtItem.getInteger("hexCriticalChance_a") + "% + " + (nbtItem.hasKey("hexCriticalChance_b") ? nbtItem.getInteger("hexCriticalChance_b") - nbtItem.getInteger("hexCriticalChance_a") : 0) + "%)");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.critical-damage") + ": " + secondaryColor + (nbtItem.hasKey("hexCriticalDamage_b") ? nbtItem.getInteger("hexCriticalDamage_b") : nbtItem.getInteger("hexCriticalDamage_a")) + "% " + tertiaryColor + "(" + nbtItem.getInteger("hexCriticalDamage_a") + "% + " + (nbtItem.hasKey("hexCriticalDamage_b") ? nbtItem.getInteger("hexCriticalDamage_b") - nbtItem.getInteger("hexCriticalDamage_a") : 0) + "%)");
			lore.add("");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.lifesteal") + ": " + secondaryColor + (nbtItem.hasKey("hexLifesteal_b") ? nbtItem.getInteger("hexLifesteal_b") : nbtItem.getInteger("hexLifesteal_a")) + " " + tertiaryColor + "(" + nbtItem.getInteger("hexLifesteal_a") + " + " + (nbtItem.hasKey("hexLifesteal_b") ? nbtItem.getInteger("hexLifesteal_b") - nbtItem.getInteger("hexLifesteal_a") : 0) + ")");
		}
		
		if(Plugin.getCore().getConfig().getInt("stats.potential-lines-amount") > 0) {
			lore.add("");
			lore.add(secondaryColor + ChatColor.STRIKETHROUGH + "---------------------------");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.potential-lines"));
			for(int i=0;i<statsConfig.getInt("stats.potential-lines-amount");i++) {
				String potIndex = "pot" + (i + 1);
				
				if(nbtItem.hasKey(potIndex + "_damage")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_damage")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.damage"));
				} else if(nbtItem.hasKey(potIndex + "_defense")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_defense")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.defense"));
				} else if(nbtItem.hasKey(potIndex + "_hp")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_hp")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.hp"));
				} else if(nbtItem.hasKey(potIndex + "_regeneration")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_regeneration")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.regeneration"));
				} else if(nbtItem.hasKey(potIndex + "_agility")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_agility")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.agility"));
				} else if(nbtItem.hasKey(potIndex + "_strength")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_strength")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.strength"));
				} else if(nbtItem.hasKey(potIndex + "_endurance")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_endurance")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.endurance"));
				} else if(nbtItem.hasKey(potIndex + "_dexterity")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_dexterity")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.dexterity"));
				} else if(nbtItem.hasKey(potIndex + "_lifesteal")) {
					lore.add(primaryColor + "+" + secondaryColor + (nbtItem.getInteger(potIndex + "_lifesteal")) + "% " + primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.lifesteal"));
				}
			}
			lore.add(secondaryColor + ChatColor.STRIKETHROUGH + "---------------------------");
		}
		
		lore.add("");
		lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.quality") + ": " + secondaryColor + nbtItem.getString("hexQuality_a"));
		
		String progress = "";
		String progressSymbol = "◆";
		int totalUnits = statsConfig.getInt("stats.max-durability");
		int bars = 25;
		double unitPerBar = totalUnits / bars;
		for(int i=1;i<=bars;i++) {
			if(unitPerBar * 1 <= nbtItem.getInteger("hexDurability_a")) {
				if(i == 1) {
					progress = progress + secondaryColor + progressSymbol;
				} else {
					progress = progress + progressSymbol;
				}
			} else {
				for(int j=i;j<=bars;j++) {
					if(i == 1) {
						progress = progress + primaryColor + progressSymbol;
					} else {
						progress = progress + progressSymbol;
					}
				}
				break;
			}
		}
		lore.add(progress + " " + tertiaryColor + nbtItem.getInteger("hexDurability_a") + "/" + totalUnits);
		
		if(nbtItem.hasKey("hexRequiredLevel_a")) {
			lore.add("");
			lore.add(primaryColor + Plugin.getCore().getLangTools().getMessage("stats.translation.required-level") + ": " + secondaryColor + nbtItem.getInteger("hexRequiredLevel_a"));
		}
		
		if(nbtItem.hasKey("hexAbility_a")) {
			if(itemType == 1) {
				String ability = nbtItem.getString("hexAbility_a");
				
				String abilityDisplayName = Plugin.getCore().getConfig().getString("abilities." + ability + ".display.name");
				String abilityKey = Plugin.getCore().getConfig().getString("abilities." + ability + ".display.key");
				
				if(item.getType() == Material.BOW) {
					abilityKey = "Left";
				}
				
				String[] abilityLines = new String[] {
						tertiaryColor + WordUtils.capitalizeFully(abilityKey) + "-click while holding this weapon",
						tertiaryColor + "to activate '" + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', abilityDisplayName) + tertiaryColor + "'"
				};
				
				lore.add("");
				for(int i=0;i<abilityLines.length;i++) {
					lore.add(abilityLines[i]);
				}
			}
		}
		
		return lore;
	}
	
	public void resetPlayerStats(Player player) {
		generateStatsData(player);
		
		ItemStack helmet = null;
		ItemStack chestplate = null;
		ItemStack leggings = null;
		ItemStack boots = null;

		ArrayList<ItemStack> armorItems = new ArrayList<ItemStack>();

		PlayerInventory inv = player.getInventory();
		if(inv.getHelmet() != null) {
			helmet = inv.getHelmet();
			armorItems.add(helmet);
		}
		if(inv.getChestplate() != null) {
			chestplate = inv.getChestplate();
			armorItems.add(chestplate);
		}
		if(inv.getLeggings() != null) {
			leggings = inv.getLeggings();
			armorItems.add(leggings);
		}
		if(inv.getBoots() != null) {
			boots = inv.getBoots();
			armorItems.add(boots);
		}
		
		int baseHp = new SubConfig(SubConfig.TYPES.STATS).getConfig().getInt("stats.base-hp");
		
		int totalHealth = baseHp;
		int totalDamage = 0;
		int totalDefense = 0;
		int totalRegen = 0;
		int totalAgility = 0;
		int totalStrength = 0;
		int totalEndurance = 0;
		int totalDexterity = 0;

		if(!armorItems.isEmpty() && armorItems.size() > 0) {
			for(ItemStack armor : armorItems) {
				if(armor == null || !armor.hasItemMeta() || !armor.getItemMeta().hasLore() || isUnidentified(armor)) {
					continue;
				}
				
				NBTItem nbtItem = new NBTItem(armor);
				
				if(nbtItem.hasKey("hexHP_a")) {
					totalHealth += nbtItem.hasKey("hexHP_b") ? nbtItem.getInteger("hexHP_b") : nbtItem.getInteger("hexHP_a");
				}
				if(nbtItem.hasKey("hexDPS_a")) {
					totalDamage += nbtItem.hasKey("hexDPS_b") ? nbtItem.getInteger("hexDPS_b") : nbtItem.getInteger("hexDPS_a");
				}
				if(nbtItem.hasKey("hexDefense_a")) {
					totalDefense += nbtItem.hasKey("hexDefense_b") ? nbtItem.getInteger("hexDefense_b") : nbtItem.getInteger("hexefense_a");
				}
				if(nbtItem.hasKey("hexRegeneration_a")) {
					totalRegen += nbtItem.hasKey("hexRegeneration_b") ? nbtItem.getInteger("hexRegeneration_b") : nbtItem.getInteger("hexRegeneration_a");
				}
				if(nbtItem.hasKey("hexAgility_a")) {
					totalAgility += nbtItem.hasKey("hexAgility_b") ? nbtItem.getInteger("hexAgility_b") : nbtItem.getInteger("hexAgility_a");
				}
				if(nbtItem.hasKey("hexStrength_a")) {
					totalStrength += nbtItem.hasKey("hexStrength_b") ? nbtItem.getInteger("hexStrength_b") : nbtItem.getInteger("hexStrength_a");
				}
				if(nbtItem.hasKey("hexEndurance_a")) {
					totalEndurance += nbtItem.hasKey("hexEndurance_b") ? nbtItem.getInteger("hexEndurance_b") : nbtItem.getInteger("hexEndurance_a");
				}
				if(nbtItem.hasKey("hexDexterity_a")) {
					totalDexterity += nbtItem.hasKey("hexDexterity_b") ? nbtItem.getInteger("hexDexterity_b") : nbtItem.getInteger("hexDexterity_a");
				}
			}
		}
		
		totalDamage = totalDamage < 0 ? 0 : totalDamage;
		totalDefense = totalDefense < 0 ? 0 : totalDefense;
		totalRegen = totalRegen < 0 ? 0 : totalRegen;
		totalAgility = totalAgility < 0 ? 0 : totalAgility;
		totalStrength = totalStrength < 0 ? 0 : totalStrength;
		totalEndurance = totalEndurance < 0 ? 0 : totalEndurance;
		totalDexterity = totalDexterity < 0 ? 0 : totalDexterity;
		
		int baseDamage = totalDamage;
		int baseDefense = totalDefense;
		int baseRegen = totalRegen;
		int baseAgility = totalAgility;
		int baseStrength = totalStrength;
		int baseEndurance = totalEndurance;
		int baseDexterity = totalDexterity;
		
		if(isWeapon(player.getInventory().getItemInMainHand())) {
			for(int i=0;i<Plugin.getCore().getConfig().getInt("stats.potential-lines-amount");i++) {
				String potIndex = "pot" + (i + 1);
				NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
				
				totalDamage += nbtItem.hasKey(potIndex + "_damage") ? baseDamage / 100 * nbtItem.getInteger(potIndex + "_damage") : 0;
				totalDefense += nbtItem.hasKey(potIndex + "_defense") ? baseDefense / 100 * nbtItem.getInteger(potIndex + "_defense") : 0;
				totalRegen += nbtItem.hasKey(potIndex + "_regeneration") ? baseRegen / 100 * nbtItem.getInteger(potIndex + "_regeneration") : 0;
				totalAgility += nbtItem.hasKey(potIndex + "_agility") ? baseAgility / 100 * nbtItem.getInteger(potIndex + "_agility") : 0;
				totalStrength += nbtItem.hasKey(potIndex + "_strength") ? baseStrength / 100 * nbtItem.getInteger(potIndex + "_strength") : 0;
				totalEndurance += nbtItem.hasKey(potIndex + "_endurance") ? baseEndurance / 100 * nbtItem.getInteger(potIndex + "_endurance") : 0;
				totalDexterity += nbtItem.hasKey(potIndex + "_dexterity") ? baseDexterity / 100 * nbtItem.getInteger(potIndex + "_dexterity") : 0;
			}
		}
		
		totalHealth = totalHealth > 2048 ? 2048 : totalHealth;
		playerMaxHealth.put(player, baseHp + totalHealth);
		
		playerDefense.put(player, totalDefense > 99 ? 99 : totalDefense);
		playerDPS.put(player, totalDamage > 99 ? 99 : totalDamage);

		getHpRegenerationCount().put(player.getName(), playerMaxHealth.get(player) / 100.0d * totalRegen);

		getPlayerAgility().put(player, totalAgility);
		getPlayerStrength().put(player, totalStrength);
		getPlayerEndurance().put(player, totalEndurance);
		getPlayerDexterity().put(player, totalDexterity);
		
		PlayerLevel level = new PlayerLevel(player);
		
		double playerMaxHealth = getPlayerMaxHealth().get(player) + (Plugin.getCore().getConfig().getInt("leveling.skills.skill-extras.extra-hitpoints") * level.getSkillLevel("HITPOINTS") / 2);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerMaxHealth);

		if(!playerDMG.containsKey(player)) {
			playerDMG.put(player, Plugin.getCore().getConfig().getInt("stats.min-damage"));
		}
		
		int playerDMG = 0;
		int playerCriticalChance = 0;
		int playerCriticalDamage = 0;
		int playerLifesteal = 0;
		boolean usingFists = true;

		for (ItemStack is : new ItemStack[] { player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand() }) {
			if(is == null || is.getType() == Material.AIR) continue;

			if(isCustomItem(is)) {
				NBTItem nbtItem = new NBTItem(is);
				
				if(nbtItem.hasKey("hexDamage_a")) {
					playerDMG += nbtItem.hasKey("hexDamage_b") ? nbtItem.getInteger("hexDamage_a") + nbtItem.getInteger("hexDamage_b") : nbtItem.getInteger("hexDamage_a");
				}
				if(nbtItem.hasKey("hexCriticalChance_a")) {
					playerCriticalChance += nbtItem.hasKey("hexCriticalChance_b") ? nbtItem.getInteger("hexCriticalChance_a") + nbtItem.getInteger("hexCriticalChance_b") : nbtItem.getInteger("hexCriticalChance_a");
				}
				if(nbtItem.hasKey("hexCriticalDamage_a")) {
					playerCriticalDamage += nbtItem.hasKey("hexCriticalDamage_b") ? nbtItem.getInteger("hexCriticalDamage_a") + nbtItem.getInteger("hexCriticalDamage_b") : nbtItem.getInteger("hexCriticalDamage_a");
				}
				if(nbtItem.hasKey("hexLifesteal_a")) {
					playerLifesteal += nbtItem.hasKey("hexLifesteal_b") ? nbtItem.getInteger("hexLifesteal_a") + nbtItem.getInteger("hexLifesteal_b") : nbtItem.getInteger("hexLifesteal_a");
				}
				
				usingFists = false;
			}
		}

		if(usingFists) {
			playerDMG = Plugin.getCore().getConfig().getInt("stats.fist-damage");
		} else if(playerDMG < Plugin.getCore().getConfig().getInt("stats.min-damage")) {
			playerDMG = Plugin.getCore().getConfig().getInt("stats.min-damage");
		}

		if(new SubConfig(SubConfig.TYPES.FEATURES).getConfig().getBoolean("features.armor-sets")) {
			if((player.getEquipment().getHelmet() != null) && (player.getEquipment().getHelmet().getType() == Material.LEATHER_HELMET) && (player.getEquipment().getChestplate() != null) && (player.getEquipment().getChestplate().getType() == Material.LEATHER_CHESTPLATE)
					&& (player.getEquipment().getLeggings() != null) && (player.getEquipment().getLeggings().getType() == Material.LEATHER_LEGGINGS) && (player.getEquipment().getBoots() != null) && (player.getEquipment().getBoots().getType() == Material.LEATHER_BOOTS)) {
				giveArmorSetEffect(player, "manacloth");
			} else if((player.getEquipment().getHelmet() != null) && (player.getEquipment().getHelmet().getType() == Material.CHAINMAIL_HELMET) && (player.getEquipment().getChestplate() != null) && (player.getEquipment().getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE)
					&& (player.getEquipment().getLeggings() != null) && (player.getEquipment().getLeggings().getType() == Material.CHAINMAIL_LEGGINGS) && (player.getEquipment().getBoots() != null) && (player.getEquipment().getBoots().getType() == Material.CHAINMAIL_BOOTS)) {
				giveArmorSetEffect(player, "chainmail");
			} else if((player.getEquipment().getHelmet() != null) && (player.getEquipment().getHelmet().getType() == Material.GOLDEN_HELMET) && (player.getEquipment().getChestplate() != null) && (player.getEquipment().getChestplate().getType() == Material.GOLDEN_CHESTPLATE)
					&& (player.getEquipment().getLeggings() != null) && (player.getEquipment().getLeggings().getType() == Material.GOLDEN_LEGGINGS) && (player.getEquipment().getBoots() != null) && (player.getEquipment().getBoots().getType() == Material.GOLDEN_BOOTS)) {
				giveArmorSetEffect(player, "leather");
			} else if((player.getEquipment().getHelmet() != null) && (player.getEquipment().getHelmet().getType() == Material.IRON_HELMET) && (player.getEquipment().getChestplate() != null) && (player.getEquipment().getChestplate().getType() == Material.IRON_CHESTPLATE)
					&& (player.getEquipment().getLeggings() != null) && (player.getEquipment().getLeggings().getType() == Material.IRON_LEGGINGS) && (player.getEquipment().getBoots() != null) && (player.getEquipment().getBoots().getType() == Material.IRON_BOOTS)) {
				giveArmorSetEffect(player, "iron");
			} else if((player.getEquipment().getHelmet() != null) && (player.getEquipment().getHelmet().getType() == Material.DIAMOND_HELMET) && (player.getEquipment().getChestplate() != null) && (player.getEquipment().getChestplate().getType() == Material.DIAMOND_CHESTPLATE)
					&& (player.getEquipment().getLeggings() != null) && (player.getEquipment().getLeggings().getType() == Material.DIAMOND_LEGGINGS) && (player.getEquipment().getBoots() != null) && (player.getEquipment().getBoots().getType() == Material.DIAMOND_BOOTS)) {
				giveArmorSetEffect(player, "diamond");
			}
		}
		
		getPlayerDMG().put(player, playerDMG);
		getPlayerCriticalChance().put(player, playerCriticalChance);
		getPlayerCriticalDamage().put(player, playerCriticalDamage);
		getPlayerLifesteal().put(player, playerLifesteal);
		
		if(!playerDropChance.containsKey(player)) {
			playerDropChance.put(player, Plugin.getCore().getConfig().getInt("entities.drop-chance"));
		}
		
		if(playerAgility.containsKey(player)) {
			double baseWalkSpeed = 0.2d;
			double walkSpeed = baseWalkSpeed + playerAgility.get(player) * 0.0008d;
			
			if(Plugin.getCore().getConfig().isDouble("classes." + getClass(player).toLowerCase() + ".speed")) {
				walkSpeed = baseWalkSpeed * Plugin.getCore().getConfig().getDouble("classes." + getClass(player).toLowerCase() + ".speed");
			}
			if(Plugin.getCore().getConfig().isDouble("races." + getClass(player).toLowerCase() + ".speed")) {
				walkSpeed = baseWalkSpeed * Plugin.getCore().getConfig().getDouble("races." + getClass(player).toLowerCase() + ".speed");
			}
			
			walkSpeed = walkSpeed > 1.0d ? 1.0d : walkSpeed;
			player.setWalkSpeed((float) walkSpeed);
		}
	}
	
	public ItemStack removeDurability(Player player, ItemStack item) {
		if(item == null || item.getType() == Material.AIR || !isCustomItem(item)) {
			return item;
		}
		
		YamlConfiguration statsConfig = new SubConfig(SubConfig.TYPES.STATS).getConfig();
		
		NBTItem nbtItem = new NBTItem(item);
		if(!nbtItem.hasKey("hexDurability_a")) {
			return item;
		}
		
		int durability = nbtItem.getInteger("hexDurability_a");
		Random random = new Random();
		int loss = random.nextInt(statsConfig.getInt("stats.durability-loss.max") - statsConfig.getInt("stats.durability-loss.min")) + statsConfig.getInt("stats.durability-loss.min") + 1;
		if(durability - loss <= 0) {
			if(player != null) {
				player.sendMessage(Plugin.getCore().getLangTools().getMessage("items.item-broke"));
			}
			
			return new ItemStack(Material.AIR);
		}
		
		nbtItem.setInteger("hexDurability_a", durability - loss);
		item = nbtItem.getItem();
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(resetDisplayName(item));
		meta.setLore(resetStatsLore(item));
		item.setItemMeta(meta);
		
		return item;
	}
	
	public HashMap<Player, Integer> getPlayerMaxHealth() {
		return playerMaxHealth;
	}

	public HashMap<Player, Integer> getPlayerDefense() {
		return playerDefense;
	}

	public HashMap<Player, Integer> getPlayerDMG() {
		return playerDMG;
	}

	public HashMap<Player, Integer> getPlayerDPS() {
		return playerDPS;
	}

	public HashMap<Player, Integer> getPlayerAgility() {
		return playerAgility;
	}

	public HashMap<Player, Integer> getPlayerStrength() {
		return playerStrength;
	}

	public HashMap<Player, Integer> getPlayerEndurance() {
		return playerEndurance;
	}

	public HashMap<Player, Integer> getPlayerDexterity() {
		return playerDexterity;
	}

	public HashMap<Player, Integer> getPlayerCriticalChance() {
		return playerCriticalChance;
	}

	public HashMap<Player, Integer> getPlayerCriticalDamage() {
		return playerCriticalDamage;
	}

	public HashMap<Player, Integer> getPlayerLifesteal() {
		return playerLifesteal;
	}

	public HashMap<Player, Integer> getPlayerDropChance() {
		return playerDropChance;
	}

	public HashMap<String, Double> getHpRegenerationCount() {
		return hpRegenerationCount;
	}
	
	private void a(String a, YamlConfiguration config) {
		if(!config.isInt("SKILLS." + a + ".level")) {
			config.set("SKILLS." + a + ".level", 1);
		}
		if(!config.isInt("SKILLS." + a + ".exp")) {
			config.set("SKILLS." + a + ".exp", 0);
		}
	}
}