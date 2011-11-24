package org.reunionemu.jreunion.game.items.potion;

import org.reunionemu.jreunion.game.Player;

public class AllPotion extends Potion {

	public AllPotion(int id) {
		super(id);
	}

	@Override
	public void effect(Player target, int effect) {
		synchronized(target){
			target.setElectricity(target.getElectricity()+effect);
			target.setHp(target.getHp()+effect);
			target.setMana(target.getMana()+effect);
			target.setStamina(target.getStamina()+effect);
		}
	}
}