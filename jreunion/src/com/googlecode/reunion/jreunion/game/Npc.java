package com.googlecode.reunion.jreunion.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
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
	}
	private int dmgType;
	public int getDmgType() {
		return dmgType;
	}

	public void setDmgType(int dmgType) {
		this.dmgType = dmgType;
	}
	
	public int getPercentageHp(){
		
		double percentageHp = this.getHp() * 100 / this.getMaxHp();

		if (percentageHp > 0 && percentageHp < 1) {
			percentageHp = 1;
		}
		return (int) percentageHp;
		
	}

	@Override
	public void enter(Session session) {
		
		session.getOwner().getClient().sendPacket(Type.IN_NPC, this);
	}

	@Override
	public void exit(Session session) {
		
		session.getOwner().getClient().sendPacket(Type.OUT_NPC, this);
	}
}