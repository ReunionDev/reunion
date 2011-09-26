package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.Player;

public class StaminaPotion extends Potion {

	public StaminaPotion(int id) {
		super(id);
	}

	@Override
	public void effect(Player target, int effect) {
		synchronized(target){
			target.setStamina(target.getStamina()+effect);		
		}
	}
}
