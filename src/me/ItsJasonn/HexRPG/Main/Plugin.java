package me.ItsJasonn.HexRPG.Main;

import org.bukkit.event.Listener;

public class Plugin implements Listener {
	private static Core core;

	@SuppressWarnings("static-access")
	public Plugin(Core core) {
		this.core = core;
	}
	
	public static Core getCore() {
		return core;
	}
}
