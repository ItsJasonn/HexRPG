package me.ItsJasonn.HexRPG.Tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public enum EffectTool {
	EXPLOSION_HUGE("hugeexplosion"), EXPLOSION_LARGE("largeexplode"), FIREWORKS_SPARK("fireworksSpark"), BUBBLE("bubble"), SUSPEND("suspend"), SUSPEND_DEPTH("depthsuspend"), TOWNAURA("townaura"), CRIT("crit"), CRIT_MAGIC("magicCrit"), SMOKE("smoke"), SMOKE_LARGE("largesmoke"), MOB_SPELL(
			"mobSpell"), MOB_SPELL_AMBIENT("mobSpellAmbient"), SPELL("spell"), INSTANT_SPELL("instantSpell"), WITHER_MAGIC("witchMagic"), NOTE("note"), PORTAL("portal"), ENCHANTMENT_TABLE("enchantmenttable"), EXPLODE("explode"), FLAME("flame"), LAVA("lava"), FOOTSTEP(
					"footstep"), SPLASH("splash"), CLOUD("cloud"), REDDUST("reddust"), SNOWBALL_POOF("snowballpoof"), DRIP_WATER("dripWater"), DRIP_LAVA("dripLava"), SNOW_SHOVEL("snowshovel"), SLIME("slime"), HEART("heart"), VILLAGER_ANGRY("angryVillager"), VILLAGER_HAPPY("happyVillager");
	
	public static HashMap<String, Float> i = new HashMap<String, Float>();
	public static HashMap<String, Float> y = new HashMap<String, Float>();
	public static HashMap<String, Boolean> yUp = new HashMap<String, Boolean>();
	private String id;

	private EffectTool(String id) {
		this.id = id;
	}

	public String getID() {
		return this.id;
	}

	public static void PlayEffect(Player player, Location location, String particle, boolean small, int r, int g, int b) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		float x = (float) location.getX();
		float y = (float) location.getY();
		float z = (float) location.getZ();
		
		Class<?> packetClass = ReflectionUtils.getNMSClass("PacketPlayOutWorldParticles");
		Class<?> enumParticleClass = ReflectionUtils.getNMSClass("EnumParticle");
        Constructor<?> packetConstructor = packetClass.getConstructor(enumParticleClass, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
        
        int enumIndex = 0;
        for(int i=0;i<enumParticleClass.getEnumConstants().length;i++) {
        	if(enumParticleClass.getEnumConstants()[i].toString().equalsIgnoreCase(particle)) {
        		enumIndex = i;
        		break;
        	}
        }
        
        Object packet = packetConstructor.newInstance(enumParticleClass.getEnumConstants()[enumIndex], true, x, y, z, r, g, b, 0, 1, new int[] { 1 });
        
        Method sendPacket = ReflectionUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet"));
        sendPacket.invoke(ReflectionUtils.getConnection(player), packet);
	}

	public static void generateParticleArroundEntity(Player players, Entity entity, float rayon, int speed, float distance, int[] rgb) {
		if(!i.containsKey(entity.getUniqueId().toString())) {
			i.put(entity.getUniqueId().toString(), 0.0f);
		}
		i.put(entity.getUniqueId().toString(), i.get(entity.getUniqueId().toString()) + distance);
		
		for (int k = 0; k < speed; k++) {
			y.put(entity.getUniqueId().toString(), Float.valueOf((float) entity.getLocation().getY()));
			
			Location loc = new Location(entity.getWorld(), entity.getLocation().add(0, 1.7, 0).getX() + Math.cos(((Float) i.get(entity.getUniqueId().toString())).floatValue()) / rayon, ((Float) y.get(entity.getUniqueId().toString())).floatValue(),
					entity.getLocation().add(0, 1.7, 0).getZ() + Math.sin(((Float) i.get(entity.getUniqueId().toString())).floatValue()) / rayon);
			loc.setY(loc.getY() + 2.0D);
			
			try {
				PlayEffect(players, loc, "REDSTONE", true, rgb[0], rgb[1], rgb[2]);
				PlayEffect(players, loc.add(0, -1.7, 0), "REDSTONE", true, rgb[0], rgb[1], rgb[2]);
			} catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		
		if (((Float) i.get(entity.getUniqueId().toString())).floatValue() >= 360.0F) {
			i.put(entity.getUniqueId().toString(), Float.valueOf(0.0F));
		}
	}
	public static void playColoredSmoke(Player players, Location location, int[] rgb) {
		Random random = new Random();
		
		for(int i=0;i<50;i++) {
			try {
				PlayEffect(players, location.clone().subtract((random.nextDouble() * 3) - 1, (random.nextDouble() * 3) - 1, (random.nextDouble() * 3) - 1), "REDSTONE", true, rgb[0], rgb[1], rgb[2]);
			} catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}
}