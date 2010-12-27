package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Npc;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NpcManager {

	private java.util.List<Npc> npcList = new Vector<Npc>();
	int npcIdCounter = 10000;

	public NpcManager() {

	}

	public void addNpc(Npc npc) {
		if (containsNpc(npc)) {
			return;
		}
		npcList.add(npc);
	}

	public boolean containsNpc(Npc npc) {
		return npcList.contains(npc);
	}

	public Npc createNpc(int type) {
		ParsedItem parsedNpc = Reference.getInstance().getNpcReference().getItemById(type);
		if (parsedNpc == null) {
			return null;
		}
		String className = "com.googlecode.reunion.jreunion.game." + parsedNpc.getMemberValue("Class");		
		
		Npc npc = (Npc)ClassFactory.create(className, type);
		if(npc==null)
			return null;
		
		npc.setEntityId(++npcIdCounter);
		addNpc(npc);

		return npc;
	}

	public Npc getNpc(int uniqueId) {

		Iterator<Npc> iter = getNpcListIterator();

		while (iter.hasNext()) {
			Npc npc = iter.next();

			if (npc.getEntityId() == uniqueId) {
				return npc;
			}
		}
		return null;
	}

	public Npc[] getNpcList(int type) {
		Npc[] newNpcList = new Npc[10];
		Iterator<Npc> iter = getNpcListIterator();
		int pos = 0;

		while (iter.hasNext()) {
			Npc npc = iter.next();

			if (npc.getType() == type) {
				newNpcList[pos] = npc;
				pos++;
				if (pos > newNpcList.length) {
					return newNpcList;
				}
			}
		}
		return newNpcList;
	}

	public Iterator<Npc> getNpcListIterator() {
		return npcList.iterator();
	}

	public int getNumberOfNpcs() {
		return npcList.size();
	}

	public void removeNpc(Npc npc) {
		if (!containsNpc(npc)) {
			return;
		}
		while (containsNpc(npc)) {
			npcList.remove(npc);
		}
	}

}