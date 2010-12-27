package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Spawn;
import com.googlecode.reunion.jreunion.server.Area.Field;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class MobManager {
	private java.util.List<Mob> mobList = new Vector<Mob>();

	private boolean moveToPlayer = true;
	int mobIdCounter = 20000;

	public MobManager() {

	}

	public void addMob(Mob mob) {
		if (containsMob(mob)) {
			return;
		}
		mobList.add(mob);
	}

	public boolean containsMob(Mob mob) {
		return mobList.contains(mob);
	}

	public Mob createMob(int type) {
		synchronized(this){
			
			ParsedItem parsedItem = Reference.getInstance().getMobReference().getItemById(type);			
			if(parsedItem==null){
				
				Logger.getLogger(MobManager.class).warn("Unknown mob type: "+type);
				return null;
			}
			String className = "com.googlecode.reunion.jreunion.game." + parsedItem.getMemberValue("Class");
			Mob mob = (Mob)ClassFactory.create(className, type);
			if(mob==null)
				return null;
			
			mob.setEntityId(mobIdCounter++);
			addMob(mob);
			return mob;
		}
	}

	public Mob getMob(int uniqueid) {
		
		Iterator<Mob> iter = getMobListIterator();
		while (iter.hasNext()) {
			Mob mob = iter.next();
			if (mob.getEntityId() == uniqueid) {
				return mob;
			}
		}
		return null;
	}

	

	public Iterator<Mob> getMobListIterator() {
		return new Vector<Mob>(mobList).iterator();
	}

	public int getNumberOfMobs() {
		return mobList.size();
	}

	public void removeMob(Mob mob) {
		if (!containsMob(mob)) {
			return;
		}
		while (containsMob(mob)) {
			mobList.remove(mob);
		}
		//ItemManager.getEntityManager().destroyEntity(mob);
	}


}