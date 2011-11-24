package org.reunionemu.jreunion.game.items;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.etc.Etc;

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