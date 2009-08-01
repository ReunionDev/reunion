package com.googlecode.reunion.jreunion.server;

import java.util.*;

import com.googlecode.reunion.jreunion.game.*;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_NpcManager {

	private java.util.List<G_Npc> npcList = new Vector<G_Npc>();

	public S_NpcManager() {
		
	}

	public void addNpc(G_Npc npc) {
		if(containsNpc(npc))
			return;
		npcList.add(npc);
	}
	public void removeNpc(G_Npc npc) {
		if(!containsNpc(npc))
			return;
		while(containsNpc(npc))
			npcList.remove(npc);
		G_EntityManager.getEntityManager().destroyEntity(npc);
	}
	public boolean containsNpc(G_Npc npc) {
		return npcList.contains(npc);
	}

	public int getNumberOfNpcs() {
		return npcList.size();
	}

	/*public G_Npc createNpc(int type) {
		G_Npc npc = new G_Npc(type);
		G_EntityManager.getEntityManager().createEntity(npc);
		addNpc(npc);

		return npc;
	}*/

	public G_Npc createNpc(int type)
	{
		S_ParsedItem parsednpc = S_Reference.getInstance().getNpcReference().getItemById(type);
		if (parsednpc==null)
		{
			return null;	
		}
		
		String classname = parsednpc.getMemberValue("Class");
		
		G_Npc npc=null;
			
			try {
				Class c = Class.forName("com.googlecode.reunion.jreunion.game."+classname);
				npc =  (G_Npc) c.getConstructors()[0].newInstance(type);
				
			} catch (Exception e) {
				
				System.out.println("Cannot create class:"+classname);
				e.printStackTrace();
				return null;
			}
		
		com.googlecode.reunion.jreunion.game.G_EntityManager.getEntityManager().createEntity(npc);
		addNpc(npc);
		
		return npc;
	}
	
	public G_Npc getNpc(int uniqueId) {
		
		Iterator<G_Npc> iter = getNpcListIterator();
		
		while(iter.hasNext()) {
			G_Npc npc = iter.next();
			
			if (npc.getEntityId() == uniqueId)
				return npc;
		}
		return null;
	}

	public G_Npc[] getNpcList(int type) {
		G_Npc[] newNpcList = new G_Npc[10];
		Iterator<G_Npc> iter = getNpcListIterator();
		int pos=0;
		
		while(iter.hasNext()) {
			G_Npc npc = iter.next();
			
			if (npc.getType() == type){
				newNpcList[pos] = npc;
				pos++;
				if(pos > newNpcList.length)
					return newNpcList;
			}
		}
		return newNpcList;
	}
		
	public Iterator<G_Npc> getNpcListIterator() {
		return npcList.iterator();
	}
	
	
}