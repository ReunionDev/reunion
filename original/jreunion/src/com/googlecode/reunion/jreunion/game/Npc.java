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
	
	private int unknown3;

	public NpcSpawn getSpawn() {
		return spawn;
	}

	public void setSpawn(NpcSpawn spawn) {
		this.spawn = spawn;
	}

	public Npc(int type) {
		super();
		setType(type);
	}
	
	private int mutant;

	private int neoProgmare;

	public int getMutant() {
		return mutant;
	}
	
	@Override
	public void setHp(long hp){
		super.setHp(hp);
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
	
	private void setType(int npcType){
		this.type = npcType;
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
		
	}
	
	public int getUnknown3() {
		return unknown3;
	}

	public void setUnknown3(int unknown3) {
		this.unknown3 = unknown3;
		//this seems to be 9 or 10 for certain npcs and 1 for mobs
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
	
	
	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem npc = Reference.getInstance().getNpcReference().getItemById(id);

		if (npc == null) {
			// cant find Item in the reference continue to load defaults:
			setMaxHp(100);
			setHp(100);
			setName("Unknown");
		} else {
			
			if (npc.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setMaxHp(Integer.parseInt(npc.getMemberValue("Hp")));
				setHp(getMaxHp());
				
			} else {
				// use default
				setMaxHp(100);
				setHp(100);
			}
			
			setName(npc.getName());
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id:");
		buffer.append(getEntityId());
		buffer.append(", ");
		
		buffer.append("type:");
		buffer.append(getType());
		buffer.append(", ");
		
		buffer.append("name:");
		buffer.append(getName());	
				
		buffer.append("}");
		return buffer.toString();
	}
}