package me.ItsJasonn.HexRPG.Instances.Abilities;

import java.util.List;

public class CustomAbilities extends Ability {
	public CustomAbilities(double damage, double stunDuration, double poisonDamage, double poisonDuration, float movementSpeed, double movementSpeedDuration, boolean removeStuns, List<String> giveItems) {
		super(damage, stunDuration, poisonDamage, poisonDuration, movementSpeed, movementSpeedDuration, removeStuns);
	}
	
	public void execute() {
		// TODO: Process config abilities in here
	}
}