package com.googlecode.reunion.jreunion.game.items.potion;

import com.googlecode.reunion.jreunion.game.Player;

public class ElectricityPotion extends Potion {

	public ElectricityPotion(int id) {
		super(id);
	}

	@Override
	public void effect(Player target, int effect) {
		synchronized(target){
			target.setElectricity(target.getElectricity()+effect);
		
		}
	}
}
