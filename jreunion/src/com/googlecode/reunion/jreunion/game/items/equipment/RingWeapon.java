package com.googlecode.reunion.jreunion.game.items.equipment;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.RangedWeapon;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class RingWeapon extends RangedWeapon {
	public RingWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
	
	@Override
	public boolean use(Player player) {
		int manaUsed = getManaUsed();
		if (manaUsed > 0) {
			synchronized(player){
				if(player.getMana() < manaUsed) {					
					return false;				
				}
				player.setMana(player.getMana()- manaUsed);				
			}
		}
		return true;
	}

}