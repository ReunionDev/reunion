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

	private int hp;
	
	private Spawn spawn;

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
	public int getHp() {
		return hp;
	}

	public int getType() {
		return type;
	}
	
	public void setHp(int hp) {
		this.hp = hp;
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