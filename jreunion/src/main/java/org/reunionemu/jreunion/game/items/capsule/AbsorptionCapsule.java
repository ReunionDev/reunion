package org.reunionemu.jreunion.game.items.capsule;

import org.reunionemu.jreunion.game.Player;

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