package me.ItsJasonn.HexRPG.PlaceholderAPI;

import org.bukkit.entity.Player;

import me.ItsJasonn.HexRPG.Main.Core;
import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.PlayerLevel;
import me.clip.placeholderapi.external.EZPlaceholderHook;

@SuppressWarnings("deprecation")
public class HexRPGPlaceholders extends EZPlaceholderHook {
	public HexRPGPlaceholders(Core core) {
		super(core, "hexrpgplaceholder");
	}

	@Override
	public String onPlaceholderRequest(Player player, String cmd) {
		PlayerLevel playerLevel = new PlayerLevel(player);
		
		if(cmd.equalsIgnoreCase("hexrpg_maxhp") || cmd.equalsIgnoreCase("hexrpg_maxHealth")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerMaxHealth().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_hp") || cmd.equalsIgnoreCase("hexrpg_health")) {
			return String.valueOf(player.getHealth());
		} else if(cmd.equalsIgnoreCase("hexrpg_damage")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerDPS().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_defense")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerDefense().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_regen") || cmd.equalsIgnoreCase("hexrpg_regeneration")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getHpRegenerationCount().get(player.getName()));
		} else if(cmd.equalsIgnoreCase("hexrpg_agility")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerAgility().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_strength")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerStrength().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_endurance")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerEndurance().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_dexterity")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerDexterity().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_criticalChance")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerCriticalChance().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_criticalDamage")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerCriticalDamage().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_lifeSteal")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getPlayerLifesteal().get(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_class")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getClass(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_race")) {
			return String.valueOf(Plugin.getCore().getStatsManager().getRace(player));
		} else if(cmd.equalsIgnoreCase("hexrpg_exp") || cmd.equalsIgnoreCase("hexrpg_xp")) {
			return String.valueOf(playerLevel.getExp());
		} else if(cmd.equalsIgnoreCase("hexrpg_level")) {
			return String.valueOf(playerLevel.getLevel());
		} else if(cmd.equalsIgnoreCase("hexrpg_requiredExp") || cmd.equalsIgnoreCase("hexrpg_requiredXp")) {
			return String.valueOf(playerLevel.getRequiredExp());
		} else if(cmd.equalsIgnoreCase("hexrpg_woodcutting_level")) {
			return String.valueOf(playerLevel.getSkillLevel("WOODCUTTING"));
		} else if(cmd.equalsIgnoreCase("hexrpg_farming_level")) {
			return String.valueOf(playerLevel.getSkillLevel("FARMING"));
		} else if(cmd.equalsIgnoreCase("hexrpg_mining_level")) {
			return String.valueOf(playerLevel.getSkillLevel("MINING"));
		} else if(cmd.equalsIgnoreCase("hexrpg_hitpoints_level")) {
			return String.valueOf(playerLevel.getSkillLevel("HITPOINTS"));
		} else if(cmd.equalsIgnoreCase("hexrpg_fishing_level")) {
			return String.valueOf(playerLevel.getSkillLevel("FISHING"));
		} else if(cmd.equalsIgnoreCase("hexrpg_smithing_level")) {
			return String.valueOf(playerLevel.getSkillLevel("SMITHING"));
		} else if(cmd.equalsIgnoreCase("hexrpg_master_level")) {
			return String.valueOf(playerLevel.getSkillLevel("MASTER"));
		}
		
		return null;
	}
}