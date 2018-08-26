package me.ItsJasonn.HexRPG.Tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class NPC {
	private String name;
	private EntityType type;
	private World world;
	private double x;
	private double y;
	private double z;

	public NPC(String name, EntityType type) {
		this.name = name;
		this.type = type;

		this.world = Bukkit.getWorld("world");
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
	}

	public void setLocation(World world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void create() {
		Entity npc = this.world.spawnEntity(new Location(this.world, this.x, this.y, this.z), this.type);
		npc.setCustomNameVisible(true);
		npc.setCustomName(this.name);
		
		if(npc instanceof LivingEntity) {
			((LivingEntity) npc).setAI(false);
			((LivingEntity) npc).setCollidable(false);
		}
	}
}
