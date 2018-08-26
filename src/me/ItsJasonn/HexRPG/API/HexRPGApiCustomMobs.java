package me.ItsJasonn.HexRPG.API;

import java.util.ArrayList;

import me.ItsJasonn.HexRPG.Main.Plugin;
import me.ItsJasonn.HexRPG.Tools.CustomMobs.CustomMob;

public class HexRPGApiCustomMobs {
	public ArrayList<CustomMob> getCustomMobs() {
		return Plugin.getCore().getCustomMobManager().getCustomMobs();
	}
}