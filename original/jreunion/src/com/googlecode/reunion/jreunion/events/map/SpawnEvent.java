package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.LivingObject;

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
