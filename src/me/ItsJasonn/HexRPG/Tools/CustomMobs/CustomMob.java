package me.ItsJasonn.HexRPG.Tools.CustomMobs;

import java.util.Random;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import me.ItsJasonn.HexRPG.Main.Plugin;

public abstract class CustomMob {
	private int[] damage = new int[2];
	private CustomMobType type;
	
	private int maxHealth;
	private int health;
	
	private Entity entity;
	
	public CustomMob(Entity entity) {
		this.entity = entity;
	}
	
	public void setMinimumDamage(int minDamage) {
		damage[0] = minDamage;
	}
	
	public void setMaxDamage(int maxDamage) {
		damage[1] = maxDamage;
	}
	
	public int getRandomDamage() {
		Random random = new Random();
		return random.nextInt(getMaxDamage() - getMinimumDamage()) + getMinimumDamage();
	}
	
	public void setDamage(int minDamage, int maxDamage) {
		setMinimumDamage(minDamage);
		setMaxDamage(maxDamage);
	}
	
	public void setType(CustomMobType type) {
		this.type = type;
	}
	
	public abstract void executeDefault();
	
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getMinimumDamage() {
		return damage[0];
	}
	
	public int getMaxDamage() {
		return damage[1];
	}
	
	public CustomMobType getCustomType() {
		return type;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getHealth() {
		return health;
	}
	
	public Entity getVanillaEntity() {
		return entity;
	}
	
	public void apply() {
		if(entity instanceof Damageable && entity instanceof Attributable) {
			Attributable attr = (Attributable) entity;
			
			attr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
			((Damageable) entity).setHealth(maxHealth);
		}
		
		if(Plugin.getCore().getCustomMobManager().getCustomMobs().contains(this)) {
			Plugin.getCore().getCustomMobManager().getCustomMobs().remove(this);
		}
		Plugin.getCore().getCustomMobManager().getCustomMobs().add(this);
	}
	
	public static boolean isCustomMob(Entity entity) {
		for(CustomMob customMobs : Plugin.getCore().getCustomMobManager().getCustomMobs()) {
			if(customMobs.getVanillaEntity() == entity) {
				return true;
			}
		}
		return false;
	}
	
	public static CustomMob getCustomMob(Entity entity) {
		for(int i=0;i<Plugin.getCore().getCustomMobManager().getCustomMobs().size();i++) {
			if(Plugin.getCore().getCustomMobManager().getCustomMobs().get(i).getVanillaEntity() == entity) {
				return Plugin.getCore().getCustomMobManager().getCustomMobs().get(i);
			}
		}
		return null;
	}
}