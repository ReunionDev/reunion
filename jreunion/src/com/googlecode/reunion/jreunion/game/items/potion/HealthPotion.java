package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;

public class HealthPotion extends Potion {

	public HealthPotion(int id) {
		super(id);
	}


	@Override
	public void effect(Player target, int effect) {
		synchronized(target){
			target.setHp(target.getHp()+effect);
		
		}
		
	}

}
