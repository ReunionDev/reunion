package org.reunionemu.jreunion.game.items.potion;

import org.reunionemu.jreunion.game.Player;

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
