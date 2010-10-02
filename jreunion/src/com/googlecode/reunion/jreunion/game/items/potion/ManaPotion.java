package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;

public class ManaPotion extends Potion {

	public ManaPotion(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void effect(Player target, int effect) {
		synchronized(target){
			target.setMana(target.getMana()+effect);
		
		}
		
	}

}
