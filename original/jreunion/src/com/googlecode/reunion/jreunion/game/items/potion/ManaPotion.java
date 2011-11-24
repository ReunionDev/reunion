package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.Player;

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
