package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.LivingObject;

public class HealthPotion extends Potion {

	public HealthPotion(int id) {
		super(id);
	}


	@Override
	public void effect(LivingObject target, int effect) {
		synchronized(target){
			target.setHp(target.getHp()+effect);
		
		}
		
	}

}
