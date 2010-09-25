package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.server.LocalMap;

public class NpcSpawnEvent extends MapEvent {

	Npc npc;
	public NpcSpawnEvent(Npc npc) {
		super(npc.getPosition().getMap());
		this.npc = npc;
	}
	public Npc getNpc() {
		return npc;
	}
}
