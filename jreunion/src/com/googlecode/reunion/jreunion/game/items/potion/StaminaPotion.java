package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.LivingObject;

public class StaminaPotion extends Potion {

	public StaminaPotion(int id) {
		super(id);
	}

	@Override
	public void effect(LivingObject target, int effect) {
		synchronized(target){
			System.out.println("stamina potion effect: "+effect);
			target.setStamina(target.getStamina()+effect);		
		}
	}
}
