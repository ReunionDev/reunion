package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;
import com.googlecode.reunion.jreunion.server.Timer;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Mob extends Npc {

	private int type;

	private int dmg;

	private int exp;

	private int lime;

	private boolean running;

	private int attackType;

	private int dmgType;

	private int speed;

	private int isMoving;

	private int isAttacking;
	


	private Timer time = new Timer();

	public Mob(int type) {
		super(type);
		
		this.type = type;
		loadFromReference(type);
	}

	public void attack(LivingObject livingObject) {
		if (getAttackType() == Enums.CLOSE_MELEE_DMG) {
			if (livingObject instanceof Player) {
				closeMeleeAttackPlayer((Player) livingObject);
			}
		} else if (getAttackType() == Enums.RANGE_MELEE_DMG) {
			if (livingObject instanceof Player) {
				rangeMeleeAttackPlayer((Player) livingObject);
			}
		} else if (getAttackType() == Enums.RANGE_MAGIC_DMG) {
			if (livingObject instanceof Player) {
				rangeMagicAttackPlayer((Player) livingObject);
			}
		}
	}

	private void closeMeleeAttackPlayer(Player player) {

	}

	public int getAttackType() {
		return attackType;
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
	


	public boolean getRunning() {
		return running;
	}

	public int getSpeed() {
		return speed;
	}

	public Timer getTimer() {
		return time;
	}

	public int getType() {
		return type;
	}



	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem mob = Reference.getInstance().getMobReference()
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
				setExp((int) (Integer.parseInt(mob.getMemberValue("Exp")) * Server
						.getInstance().getWorld().getServerSetings()
						.getXp()));
			} else {
				// use default
				setExp(1);
			}
			if (mob.checkMembers(new String[] { "Lime" })) {
				// use member from file
				setLime((int) (Integer.parseInt(mob.getMemberValue("Lime")) * Server
						.getInstance().getWorld().getServerSetings()
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
	
	

	public void moveToPlayer(Player player, double distance) {
	
		Client client = player.getClient();

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
				this.getPosition().getMap().getWorld().getCommand()
						.NpcAttackChar(player, this);
				return;
			} else if (distance < 20) {
				setIsAttacking(1);
				this.getPosition().getMap().getWorld().getCommand()
						.NpcAttackChar(player, this);
				return;
			}
		}


		double xcomp = player.getPosition().getX() - getPosition().getX();
		double ycomp = player.getPosition().getY() - getPosition().getY();

		if (xcomp >= 0 && ycomp >= 0) {
			xcomp = Math.pow(xcomp, 1.1);
			ycomp = Math.pow(ycomp, 1.1);
		}

		xcomp = xcomp / (distance / getSpeed());
		ycomp = ycomp / (distance / getSpeed());

		int newPosX = (int) (getPosition().getX() + xcomp);
		int newPosY = (int) (getPosition().getY() + ycomp);

		if (getPosition().getMap()
				.getMobArea().get((newPosX / 10 - 300), (newPosY / 10)) == true) {
			getPosition().setX(newPosX);
			getPosition().setY(newPosY);
		} else {
			return;
		}
		int run = getRunning()?1:0;

		String packetData = "walk npc " + getId() + " " + getPosition().getX() + " "
				+ getPosition().getY() + " 0 " + run + "\n";
		// S> walk npc [UniqueId] [Xpos] [Ypos] [ZPos] [Running]

		client.SendData(packetData);

			Iterator<WorldObject> playerIter = player.getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				Player pl =  (Player) playerIter.next();

				client = pl.getClient();
				if (client == null) {
					continue;
				}
				if (getPosition().distance(pl.getPosition()) < pl.getSessionRadius()) {
					client.SendData(packetData);
				}
			}
		
	}

	private void rangeMagicAttackPlayer(Player player) {

	}

	private void rangeMeleeAttackPlayer(Player player) {

	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public void kill(Player player) {
		setHp(0);

		this.getPosition().getMap().getWorld().getMobManager().removeMob(this);
		Spawn spawn = this.getSpawn();
		if (spawn != null) {
			spawn.kill();
		}
		Parser dropList = Reference.getInstance().getDropListReference();
		Iterator<ParsedItem> iter =dropList.getItemListIterator();
		Random r = new Random();
		while(iter.hasNext()) {			
			ParsedItem parsedItem = iter.next();
			if(parsedItem.getMemberValue("Mob").equals(""+this.getType())){
				float rate = Float.parseFloat(parsedItem.getMemberValue("Rate"));
				if( r.nextFloat()<rate){
					System.out.println(parsedItem.getMemberValue("Item"));
					int itemType = Integer.parseInt(parsedItem.getMemberValue("Item"));
					
					Item item = ItemFactory.create(itemType);
					item.setExtraStats(0);
					item.setGemNumber(0);					
					final RoamingItem roamingItem = getPosition().getMap().getWorld().getCommand().dropItem(this.getPosition(), item);
					roamingItem.setOwner(player);
					
					java.util.Timer timer = new java.util.Timer();
					float dropExclusivity = Float.parseFloat(Reference.getInstance().getServerReference().getItem("Server").getMemberValue("DropExclusivity"));
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							roamingItem.setOwner(null);
							
						}
					},(long)(dropExclusivity*1000));
					
				}
			}			
		}
		this.getPosition().getMap().getWorld().getCommand()
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

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public void enter(Session session) {
		this.getPosition().getMap().getWorld().getCommand()
		.mobIn(session.getOwner(), this, false);		
	}

	@Override
	public void exit(Session session) {
		this.getPosition().getMap().getWorld().getCommand()
		.mobOut(session.getOwner(), this);
		
	}
}