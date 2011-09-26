package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.ClassFactory;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Session;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Npc extends LivingObject {


	private int type;
	
	private NpcSpawn spawn;

	private int unknown1;

	private int unknown2;

	public NpcSpawn getSpawn() {
		return spawn;
	}

	public void setSpawn(NpcSpawn spawn) {
		this.spawn = spawn;
	}

	public Npc(int type) {
		super();
		this.setMaxHp(100);
		this.setHp(this.getMaxHp());
		this.type = type;
	}
	
	private int mutant;

	private int neoProgmare;

	public int getMutant() {
		return mutant;
	}
	@Override
	public void setHp(int hp){
		super.setHp(hp);
		this.getInterested().sendPacket(Type.ATTACK_VITAL, this);
	
	}

	public int getNeoProgmare() {
		return neoProgmare;
	}

	public void setMutant(int mutant) {
		this.mutant = mutant;
	}

	public void setNeoProgmare(int neoProgmare) {
		this.neoProgmare = neoProgmare;
	}
	
	public int getType() {
		return type;
	}
	
	public void setUnknown1(int unknown1) {
		this.unknown1 = unknown1;
	}

	public void setUnknown2(int unknown2) {
		this.unknown2 = unknown2;
	}
	
	public int getUnknown1() {
		return unknown1;
	}

	public int getUnknown2() {
		return unknown2;
		//this seems to be 10 for certain npcs and 1 for mobs
	}
	
	@Override
	public void enter(Session session) {
		
		session.getOwner().getClient().sendPacket(Type.IN_NPC, this);
	}

	@Override
	public void exit(Session session) {
		
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	public static Npc create(int typeId) {
		
		ParsedItem parsedNpc = Reference.getInstance().getMobReference().getItemById(typeId);
		if (parsedNpc == null) {
			parsedNpc = Reference.getInstance().getNpcReference().getItemById(typeId);
			if (parsedNpc == null) {
				return null;
			}
		}		
		String className = "com.googlecode.reunion.jreunion.game." + parsedNpc.getMemberValue("Class");		
		
		Npc npc = (Npc)ClassFactory.create(className, typeId);
		
		//npc.loadFromReference(typeId);
		return npc;
		
	}
	
	
}