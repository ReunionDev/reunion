package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.server.LocalMap;

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
