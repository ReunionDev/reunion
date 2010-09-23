package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.server.LocalMap;

public class MobSpawnEvent extends MapEvent {

	Mob mob;
	public MobSpawnEvent(Mob mob) {
		super(mob.getPosition().getMap());
		this.mob = mob;
	}
	public Mob getMob() {
		return mob;
	}
}
