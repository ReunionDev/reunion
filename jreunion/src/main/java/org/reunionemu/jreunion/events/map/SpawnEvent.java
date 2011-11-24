package org.reunionemu.jreunion.events.map;

import org.reunionemu.jreunion.game.LivingObject;

public class SpawnEvent extends MapEvent {

	LivingObject spawnee;
	public SpawnEvent(LivingObject spawnee) {
		super(spawnee.getPosition().getLocalMap());
		this.spawnee = spawnee;
	}
	public LivingObject getSpawnee() {
		return spawnee;
	}
}
