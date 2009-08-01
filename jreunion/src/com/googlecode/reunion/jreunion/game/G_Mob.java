package com.googlecode.reunion.jreunion.game;

import java.util.*;

import com.googlecode.reunion.jreunion.server.*;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Mob extends G_LivingObject {
	
	private int uniqueId;

	private int type;

	private int dmg;
	
	private int exp;
	
	private int lime;
	
	private int mutant;
	
	private int neoProgmare;
	
	private int unknown1;
	
	private int unknown2;
	
	private boolean running;
	
	private int attackType;
	
	private int dmgType;
	
	private int speed;
	
	private int isMoving;
	
	private int isAttacking;
	
	private S_Timer time = new S_Timer();

	public G_Mob(int type) {
		super();
		this.type = type;
		loadFromReference(type);
	}

	public int getUniqueId() {
		return this.uniqueId;
	}

	public int getType() {
		return this.type;
	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}
	public int getDmg() {
		return this.dmg;
	}
	
	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}
	public int getAttackType() {
		return this.attackType;
	}
	
	public void setDmgType(int dmgType) {
		this.dmgType = dmgType;
	}
	public int getDmgType() {
		return this.dmgType;
	}	
		
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getExp() {
		return this.exp;
	}
	
	public void setLime(int lime) {
		this.lime = lime;
	}
	public int getLime() {
		return this.lime;
	}
	
	public void setMutant(int mutant) {
		this.mutant = mutant;
	}
	public int getMutant() {
		return this.mutant;
	}
	
	public void setNeoProgmare(int neoProgmare) {
		this.neoProgmare = neoProgmare;
	}
	public int getNeoProgmare() {
		return this.neoProgmare;
	}
	
	public void setUnknown1(int unknown1) {
		this.unknown1 = unknown1;
	}
	public int getUnknown1() {
		return this.unknown1;
	}
	
	public void setUnknown2(int unknown2) {
		this.unknown2 = unknown2;
	}
	public int getUnknown2() {
		return this.unknown2;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean getRunning() {
		return this.running;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getSpeed() {
		return this.speed;
	}
	
	public void setIsMoving(int isMoving) {
		this.isMoving = isMoving;
	}
	public int getIsMoving() {
		return this.isMoving;
	}
		
	public void setIsAttacking(int isAttacking) {
		this.isAttacking = isAttacking;
	}
	public int getIsAttacking() {
		return this.isAttacking;
	}
	
	public S_Timer getTimer() {
		return this.time;
	}
	
	public void setDead(){
		this.setCurrHp(0);
		
		S_Server.getInstance().getWorldModule().getMobManager().removeMob(this);
		G_Spawn spawn= S_Server.getInstance().getWorldModule().getMapManager().getSpawnByMob(this.getEntityId());
		if (spawn!=null)
		spawn.setDead(true);
		
		S_Server.getInstance().getWorldModule().getWorldCommand().
			serverSay("Experience: "+this.getExp()+" Lime: "+this.getLime());
				
	}
	
	public void attack(G_LivingObject livingObject) {
		if(getAttackType() == G_Enums.CLOSE_MELEE_DMG){
			if(livingObject instanceof G_Player)
				this.closeMeleeAttackPlayer((G_Player)livingObject);
		}
		else if(getAttackType() == G_Enums.RANGE_MELEE_DMG){
				if(livingObject instanceof G_Player)
					this.rangeMeleeAttackPlayer((G_Player)livingObject);
		}
		else if(getAttackType() == G_Enums.RANGE_MAGIC_DMG){
				if(livingObject instanceof G_Player)
					this.rangeMagicAttackPlayer((G_Player)livingObject);
		}
	}
	
	private void closeMeleeAttackPlayer(G_Player player) {
	
	}
	
	private void rangeMeleeAttackPlayer(G_Player player) {
		
	}
	
	private void rangeMagicAttackPlayer(G_Player player) {
		
	}
	
	public int getDistance(G_LivingObject livingObject){
		double xcomp = Math.pow(livingObject.getPosX() - this.getPosX(), 2);
		double ycomp = Math.pow(livingObject.getPosY() - this.getPosY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);
		
		return (int)distance;
	}
	
	public void moveToPlayer(G_Player player,double distance) {
		int run=0;
		
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
		
		if(client==null)
			return;
		
		if(time.getTimeElapsedSeconds() > 1){
			time.Stop();
			time.Reset();
			this.setIsAttacking(0);
		}
		if(time.getTimeElapsedSeconds() < 1 && time.isRunning())
			return;
		if(!time.isRunning())
			time.Start();
				
		if(distance < 100){
			if(this.getAttackType() == 1 || this.getAttackType() == 2){
				this.setIsAttacking(1);
				S_Server.getInstance().getWorldModule().getWorldCommand().NpcAttackChar(player,this);
				return;
			}
			else if(distance < 20){
				this.setIsAttacking(1);
				S_Server.getInstance().getWorldModule().getWorldCommand().NpcAttackChar(player,this);
				return;
			}
		}
				
		if(this.getRunning()==true)
			run=1;
		else
			run=0;
		
		double xcomp = player.getPosX() - this.getPosX();
		double ycomp = player.getPosY() - this.getPosY();
		
		
		if(xcomp >= 0 && ycomp >= 0){
			xcomp = Math.pow(xcomp,1.1);
			ycomp = Math.pow(ycomp,1.1);
		}
		
		xcomp = xcomp/(distance/this.getSpeed());
		ycomp = ycomp/(distance/this.getSpeed());
		
		
		int newPosX = (int)(this.getPosX()+xcomp);
		int newPosY = (int)(this.getPosY()+ycomp);
		
		if(S_Server.getInstance().getWorldModule().getMapManager().getMobArea().get(((newPosX/10)-300),(newPosY/10)) == true){
			this.setPosX(newPosX);
			this.setPosY(newPosY);
		}
		else
			return;
			
		String packetData = "walk npc " + this.getEntityId() + " "
		+ this.getPosX() + " " + this.getPosY() + " 0 " + run + "\n";
		//S> walk npc [UniqueId] [Xpos] [Ypos] [ZPos] [Running]
		
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
		
		if (player.getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = player.getSession().getPlayerListIterator();
			
			while(playerIter.hasNext()){
				G_Player pl = playerIter.next();
				
				client = S_Server.getInstance().getNetworkModule().getClient(pl);
				if (client == null)
					continue;
				if(this.getDistance(pl) < S_DatabaseUtils.getInstance().getSessionRadius())
					S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
			}
		}
	}
	
	public void loadFromReference(int id)
	{	
		super.loadFromReference(id);
	
	  S_ParsedItem mob = S_Reference.getInstance().getMobReference().getItemById(id);
		
	  if (mob==null)
	  {
		// cant find Item in the reference continue to load defaults:
		  setExp(1);
		  setLime(1);
		  setDmg(0);
		  setAttackType(0);
		  setMutant(0);
		  setNeoProgmare(0);
		  setSpeed(1);
	  }
	  else {
		
		if(mob.checkMembers(new String[]{"Exp"}))
		{
			// use member from file
			setExp((int)(Integer.parseInt(mob.getMemberValue("Exp"))*S_Server.getInstance().getWorldModule().getServerSetings().getXp()));
		}
		else
		{
			// use default
			setExp(1);
		}
		if(mob.checkMembers(new String[]{"Lime"}))
		{
			// use member from file
			setLime((int)(Integer.parseInt(mob.getMemberValue("Lime"))*S_Server.getInstance().getWorldModule().getServerSetings().getLime()));
		}
		else
		{
			// use default
			setLime(1);
		}
		if(mob.checkMembers(new String[]{"Dmg"}))
		{
			// use member from file
			setDmg(Integer.parseInt(mob.getMemberValue("Dmg")));
		}
		else
		{
			// use default
			setDmg(0);
		}
		if(mob.checkMembers(new String[]{"AttackType"}))
		{
			// use member from file
			setAttackType(Integer.parseInt(mob.getMemberValue("AttackType")));
		}
		else
		{
			// use default
			setAttackType(0);
		}
		if(mob.checkMembers(new String[]{"DmgType"}))
		{
			// use member from file
			setDmgType(Integer.parseInt(mob.getMemberValue("DmgType")));
		}
		else
		{
			// use default
			setDmgType(0);
		}
		if(mob.checkMembers(new String[]{"Mutant"}))
		{
			// use member from file
			setMutant(Integer.parseInt(mob.getMemberValue("Mutant")));
		}
		else
		{
			// use default
			setMutant(0);
		}
		if(mob.checkMembers(new String[]{"NeoProgmare"}))
		{
			// use member from file
			setNeoProgmare(Integer.parseInt(mob.getMemberValue("NeoProgmare")));
		}
		else
		{
			// use default
			setNeoProgmare(0);
		}
		if(mob.checkMembers(new String[]{"Speed"}))
		{
			// use member from file
			setSpeed(Integer.parseInt(mob.getMemberValue("Speed")));
		}
		else
		{
			// use default
			setSpeed(1);
		}
	  }
	}
}