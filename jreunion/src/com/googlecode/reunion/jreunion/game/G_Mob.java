package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_DatabaseUtils;
import com.googlecode.reunion.jreunion.server.S_ItemFactory;
import com.googlecode.reunion.jreunion.server.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.S_Parser;
import com.googlecode.reunion.jreunion.server.S_Reference;
import com.googlecode.reunion.jreunion.server.S_Server;
import com.googlecode.reunion.jreunion.server.S_Timer;

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

	public void attack(G_LivingObject livingObject) {
		if (getAttackType() == G_Enums.CLOSE_MELEE_DMG) {
			if (livingObject instanceof G_Player) {
				closeMeleeAttackPlayer((G_Player) livingObject);
			}
		} else if (getAttackType() == G_Enums.RANGE_MELEE_DMG) {
			if (livingObject instanceof G_Player) {
				rangeMeleeAttackPlayer((G_Player) livingObject);
			}
		} else if (getAttackType() == G_Enums.RANGE_MAGIC_DMG) {
			if (livingObject instanceof G_Player) {
				rangeMagicAttackPlayer((G_Player) livingObject);
			}
		}
	}

	private void closeMeleeAttackPlayer(G_Player player) {

	}

	public int getAttackType() {
		return attackType;
	}

	public int getDistance(G_LivingObject livingObject) {
		double xcomp = Math.pow(livingObject.getPosX() - getPosX(), 2);
		double ycomp = Math.pow(livingObject.getPosY() - getPosY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);

		return (int) distance;
	}

	public int getDmg() {
		return dmg;
	}

	public int getDmgType() {
		return dmgType;
	}

	public int getExp() {
		return exp;
	}

	public int getIsAttacking() {
		return isAttacking;
	}

	public int getIsMoving() {
		return isMoving;
	}

	public int getLime() {
		return lime;
	}

	public int getMutant() {
		return mutant;
	}

	public int getNeoProgmare() {
		return neoProgmare;
	}

	public boolean getRunning() {
		return running;
	}

	public int getSpeed() {
		return speed;
	}

	public S_Timer getTimer() {
		return time;
	}

	public int getType() {
		return type;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public int getUnknown1() {
		return unknown1;
	}

	public int getUnknown2() {
		return unknown2;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		S_ParsedItem mob = S_Reference.getInstance().getMobReference()
				.getItemById(id);

		if (mob == null) {
			// cant find Item in the reference continue to load defaults:
			setExp(1);
			setLime(1);
			setDmg(0);
			setAttackType(0);
			setMutant(0);
			setNeoProgmare(0);
			setSpeed(1);
		} else {

			if (mob.checkMembers(new String[] { "Exp" })) {
				// use member from file
				setExp((int) (Integer.parseInt(mob.getMemberValue("Exp")) * S_Server
						.getInstance().getWorldModule().getServerSetings()
						.getXp()));
			} else {
				// use default
				setExp(1);
			}
			if (mob.checkMembers(new String[] { "Lime" })) {
				// use member from file
				setLime((int) (Integer.parseInt(mob.getMemberValue("Lime")) * S_Server
						.getInstance().getWorldModule().getServerSetings()
						.getLime()));
			} else {
				// use default
				setLime(1);
			}
			if (mob.checkMembers(new String[] { "Dmg" })) {
				// use member from file
				setDmg(Integer.parseInt(mob.getMemberValue("Dmg")));
			} else {
				// use default
				setDmg(0);
			}
			if (mob.checkMembers(new String[] { "AttackType" })) {
				// use member from file
				setAttackType(Integer
						.parseInt(mob.getMemberValue("AttackType")));
			} else {
				// use default
				setAttackType(0);
			}
			if (mob.checkMembers(new String[] { "DmgType" })) {
				// use member from file
				setDmgType(Integer.parseInt(mob.getMemberValue("DmgType")));
			} else {
				// use default
				setDmgType(0);
			}
			if (mob.checkMembers(new String[] { "Mutant" })) {
				// use member from file
				setMutant(Integer.parseInt(mob.getMemberValue("Mutant")));
			} else {
				// use default
				setMutant(0);
			}
			if (mob.checkMembers(new String[] { "NeoProgmare" })) {
				// use member from file
				setNeoProgmare(Integer.parseInt(mob
						.getMemberValue("NeoProgmare")));
			} else {
				// use default
				setNeoProgmare(0);
			}
			if (mob.checkMembers(new String[] { "Speed" })) {
				// use member from file
				setSpeed(Integer.parseInt(mob.getMemberValue("Speed")));
			} else {
				// use default
				setSpeed(1);
			}
		}
	}

	public void moveToPlayer(G_Player player, double distance) {
		int run = 0;

		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		if (time.getTimeElapsedSeconds() > 1) {
			time.Stop();
			time.Reset();
			setIsAttacking(0);
		}
		if (time.getTimeElapsedSeconds() < 1 && time.isRunning()) {
			return;
		}
		if (!time.isRunning()) {
			time.Start();
		}

		if (distance < 100) {
			if (getAttackType() == 1 || getAttackType() == 2) {
				setIsAttacking(1);
				S_Server.getInstance().getWorldModule().getWorldCommand()
						.NpcAttackChar(player, this);
				return;
			} else if (distance < 20) {
				setIsAttacking(1);
				S_Server.getInstance().getWorldModule().getWorldCommand()
						.NpcAttackChar(player, this);
				return;
			}
		}

		if (getRunning() == true) {
			run = 1;
		} else {
			run = 0;
		}

		double xcomp = player.getPosX() - getPosX();
		double ycomp = player.getPosY() - getPosY();

		if (xcomp >= 0 && ycomp >= 0) {
			xcomp = Math.pow(xcomp, 1.1);
			ycomp = Math.pow(ycomp, 1.1);
		}

		xcomp = xcomp / (distance / getSpeed());
		ycomp = ycomp / (distance / getSpeed());

		int newPosX = (int) (getPosX() + xcomp);
		int newPosY = (int) (getPosY() + ycomp);

		if (S_Server.getInstance().getWorldModule().getMapManager()
				.getMobArea().get((newPosX / 10 - 300), (newPosY / 10)) == true) {
			setPosX(newPosX);
			setPosY(newPosY);
		} else {
			return;
		}

		String packetData = "walk npc " + getEntityId() + " " + getPosX() + " "
				+ getPosY() + " 0 " + run + "\n";
		// S> walk npc [UniqueId] [Xpos] [Ypos] [ZPos] [Running]

				client.SendData(packetData);

		if (player.getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> playerIter = player.getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				G_Player pl = playerIter.next();

				client = S_Server.getInstance().getNetworkModule()
						.getClient(pl);
				if (client == null) {
					continue;
				}
				if (getDistance(pl) < S_DatabaseUtils.getInstance()
						.getSessionRadius()) {
							client.SendData(packetData);
				}
			}
		}
	}

	private void rangeMagicAttackPlayer(G_Player player) {

	}

	private void rangeMeleeAttackPlayer(G_Player player) {

	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public void setDead(G_Player player) {
		setCurrHp(0);

		S_Server.getInstance().getWorldModule().getMobManager().removeMob(this);
		G_Spawn spawn = S_Server.getInstance().getWorldModule().getMapManager()
				.getSpawnByMob(getEntityId());
		if (spawn != null) {
			spawn.setDead(true);
		}
		S_Parser dropList = S_Reference.getInstance().getDropListReference();
		Iterator<S_ParsedItem> iter =dropList.getItemListIterator();
		while(iter.hasNext()) {			
			S_ParsedItem item = iter.next();
			if(item.getMemberValue("Mob").equals(""+this.getType())){
				Random r = new Random();
				float rate = Float.parseFloat(item.getMemberValue("Rate"));
				if( r.nextFloat()<rate){
					System.out.println(item.getMemberValue("Item"));
					int itemType = Integer.parseInt(item.getMemberValue("Item"));
					S_Server.getInstance().getWorldModule()
					.getWorldCommand().dropItem(player, itemType, this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0, 0);
				}
			}			
		}
		S_Server.getInstance().getWorldModule().getWorldCommand()
				.serverSay("Experience: " + getExp() + " Lime: " + getLime());

	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}

	public void setDmgType(int dmgType) {
		this.dmgType = dmgType;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public void setIsAttacking(int isAttacking) {
		this.isAttacking = isAttacking;
	}

	public void setIsMoving(int isMoving) {
		this.isMoving = isMoving;
	}

	public void setLime(int lime) {
		this.lime = lime;
	}

	public void setMutant(int mutant) {
		this.mutant = mutant;
	}

	public void setNeoProgmare(int neoProgmare) {
		this.neoProgmare = neoProgmare;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setUnknown1(int unknown1) {
		this.unknown1 = unknown1;
	}

	public void setUnknown2(int unknown2) {
		this.unknown2 = unknown2;
	}
}