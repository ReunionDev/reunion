package com.googlecode.reunion.jreunion.game.items.capsule;

import com.googlecode.reunion.jreunion.game.Player;

public class AbsorptionCapsule extends Capsule {

	public AbsorptionCapsule(int id) {
		super(id);
	}


	@Override
	public void effect(Player target, int effect) {
		synchronized(target){
			
		
		}
	}
}