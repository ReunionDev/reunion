package org.reunionemu.jreunion.game.items.potion;

import org.reunionemu.jreunion.game.Player;

public class ManaPotion extends Potion {

	public ManaPotion(int id) {
		super(id);
	}


	@Override
	public void effect(Player target, int effect) {
		synchronized(target){
			target.setMana(target.getMana()+effect);
		
		}
	}
}
