package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.LivingObject;

public class ElectricityPotion extends Potion {

	public ElectricityPotion(int id) {
		super(id);
	}

	@Override
	public void effect(LivingObject target, int effect) {
		synchronized(target){
			target.setElectricity(target.getElectricity()+effect);
		
		}
		
	}

}
