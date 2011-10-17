package com.googlecode.reunion.jreunion.game.items;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Usable;
import com.googlecode.reunion.jreunion.game.items.etc.Etc;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class WarpGate extends Etc implements Usable{
	public WarpGate(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
	
	@Override
	public void use(Item<?> item, LivingObject user) {
		if(user instanceof Player){
			((Player)user).spawn();
		}
		
	}
}