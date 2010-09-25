package com.googlecode.reunion.jreunion.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Npc extends LivingObject {


	private int type;
	
	private Spawn spawn;

	private int unknown1;

	private int unknown2;

	public Spawn getSpawn() {
		return spawn;
	}

	public void setSpawn(Spawn spawn) {
		this.spawn = spawn;
	}

	public Npc(int type) {
		super();
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

	@Override
	public void enter(Session session) {
		this.getPosition().getMap().getWorld().getCommand()
		.npcIn(session.getOwner(), this);		
	}

	@Override
	public void exit(Session session) {
		this.getPosition().getMap().getWorld().getCommand()
		.npcOut(session.getOwner(), this);
		
	}
}