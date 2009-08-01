package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_Map;
import com.googlecode.reunion.jreunion.server.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.S_Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_LivingObject extends G_Entity {

	private int team;
	
	private int posX;

	private int posY;

	private int posZ;

	private G_LivingObject target;

	private S_Map map;

	private int targetPosX;

	private int targetPosY;

	private int targetPosZ;

	private double rotation;
	
	private int currHp;

	private int maxHp;
	
	private int currMana;

	private int maxMana;

	private int currElect;

	private int maxElect;

	private int currStm;

	private int maxStm;
	
	private int level;

	public G_LivingObject() {
		super();
		
	}

	public void setPosX(int posX) {
		/*server.S_Server.getInstance().worldModule.worldCommand
				.serverSay(((G_Player) this).getPlayerName() + " new position "
						+ this.getPlayerPosX() + " " + this.getPlayerPosY()
						+ " " + this.getPlayerPosZ() + " "
						+ ((G_Player) this).getPlayerRotation());*/
		this.posX = posX;
	}

	public int getPosX() {
		return this.posX;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getPosY() {
		return this.posY;
	}

	public void setPosZ(int posZ) {
		this.posZ = posZ;
	}

	public int getPosZ() {
		return this.posZ;
	}

	public void setTarget(G_LivingObject target) {
		this.target = target;
	}

	public G_LivingObject getTarget() {
		return this.target;
	}

	public void setMap(S_Map map) {
		this.map = map;
	}

	public S_Map getMap() {
		return this.map;
	}

	public void setTargetPosX(int targetPosX) {
		this.targetPosX = targetPosX;
	}

	public int getTargetPosX() {
		return this.targetPosX;
	}

	public void setTargetPosY(int targetPosY) {
		this.targetPosY = targetPosY;
	}

	public int getTargetPosY() {
		return this.targetPosY;
	}

	public void setTargetPosZ(int targetPosZ) {
		this.targetPosZ = targetPosZ;
	}

	public int getTargetPosZ() {
		return this.targetPosZ;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getRotation() {
		return this.rotation;
	}
	public void setCurrHp(int currHp) {
		this.currHp = currHp;
	}

	public int getCurrHp() {
		return this.currHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getMaxHp() {
		return this.maxHp;
	}
	public void setCurrMana(int currMana) {
		this.currMana = currMana;
	}

	public int getCurrMana() {
		return this.currMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getMaxMana() {
		return this.maxMana;
	}

	public void setCurrElect(int currElect) {
		this.currElect = currElect;
	}

	public int getCurrElect() {
		return this.currElect;
	}

	public void setMaxElect(int maxElect) {
		this.maxElect = maxElect;
	}

	public int getMaxElect() {
		return this.maxElect;
	}

	public void setCurrStm(int currStm) {
		this.currStm = currStm;

		// Client client =
		// S_Server.getInstance().networkModule.getClient(this.getPlayerSession().getPlayer(this.getEntityId()));
		// if(client.clientState == 10)
		// S_Server.getInstance().networkModule.SendPacket(client.networkId,"status
		// 2 "+this.getPlayerCurrStm()+" "+this.getPlayerMaxStm());
	}

	public int getCurrStm() {
		return this.currStm;
	}

	public void setMaxStm(int maxStm) {
		this.maxStm = maxStm;
	}

	public int getMaxStm() {
		return this.maxStm;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}
	
	public void loadFromReference(int id)
	{	
	
		S_ParsedItem mob = S_Reference.getInstance().getMobReference().getItemById(id);
		
	  if (mob==null)
	  {
		// cant find Item in the reference continue to load defaults:
		  setCurrHp(1);
		  setMaxHp(1);
		  setLevel(1);
	  }
	  else {
		
		if(mob.checkMembers(new String[]{"Hp"}))
		{
			// use member from file
			setCurrHp(Integer.parseInt(mob.getMemberValue("Hp")));
		}
		else
		{
			// use default
			setCurrHp(1);
		}
		if(mob.checkMembers(new String[]{"Hp"}))
		{
			// use member from file
			setMaxHp(Integer.parseInt(mob.getMemberValue("Hp")));
		}
		else
		{
			// use default
			setMaxHp(1);
		}
		if(mob.checkMembers(new String[]{"Level"}))
		{
			// use member from file
			setLevel(Integer.parseInt(mob.getMemberValue("Level")));
		}
		else
		{
			// use default
			setLevel(1);
		}
	  }
	}
}