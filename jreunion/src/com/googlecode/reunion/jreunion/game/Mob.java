package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemManager;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Area.Field;
import com.sun.xml.internal.bind.v2.ClassFactory;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Mob extends Npc {

	private int dmg;

	private int exp;

	private int lime;

	private int attackType;	

	private int speed;

	private boolean isMoving;

	private boolean isAttacking;
	
	private boolean isBoss;


	public Mob(int type) {
		super(type);
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
		player.setHp(player.getHp() - getDmg());
	}

	public int getAttackType() {
		return attackType;
	}


	public int getDmg() {
		return dmg;
	}

	public int getExp() {
		return exp;
	}

	public boolean getIsAttacking() {
		return isAttacking;
	}
	
	public boolean getIsBoss(){
		return isBoss;
	}

	public boolean getIsMoving() {
		return isMoving;
	}

	public int getLime() {
		return lime;
	}
	
	public int getSpeed() {
		return speed;
	}


	@Override
	public void loadFromReference(int id) {
		//super.loadFromReference(id);

		ParsedItem mob = Reference.getInstance().getMobReference()
				.getItemById(id);

		if (mob == null) {
			// cant find Item in the reference continue to load defaults:
			setMaxHp(1);
			setHp(1);
			setLevel(1);
			setExp(1);
			setLime(1);
			setDmg(0);
			setAttackType(0);
			setMutant(0);
			setNeoProgmare(0);
			setSpeed(1);
			setName("Unknown");
		} else {
			
			if (mob.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setMaxHp(Integer.parseInt(mob.getMemberValue("Hp")));
				setHp(Integer.parseInt(mob.getMemberValue("Hp")));
			} else {
				// use default
				setHp(1);
				setMaxHp(1);
			}
			
			if (mob.checkMembers(new String[] { "Level" })) {
				// use member from file
				setLevel(Integer.parseInt(mob.getMemberValue("Level")));
			} else {
				// use default
				setLevel(1);
			}
			
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
			
			setName(mob.getName());
					
		}
	}

	public void moveToPlayer(Player player, double distance) {
	
		Client client = player.getClient();

		if (client == null) {
			return;
		}
/*
		if (time.getTimeElapsedSeconds() > 1) {
			time.Stop();
			time.Reset();
			setIsAttacking(false);
		}
		if (time.getTimeElapsedSeconds() < 1 && time.isRunning()) {
			return;
		}
		if (!time.isRunning()) {
			time.Start();
		}

		if (distance < 100) {
			if (getAttackType() == 1 || getAttackType() == 2) {
				setIsAttacking(true);
				this.getPosition().getLocalMap().getWorld().getCommand()
						.NpcAttackChar(player, this);
				return;
			} else if (distance < 20) {
				setIsAttacking(true);
				this.getPosition().getLocalMap().getWorld().getCommand()
						.NpcAttackChar(player, this);
				return;
			}
		}
		*/

		//player.getPosition().
		
		double xcomp = player.getPosition().getX() - getPosition().getX();
		double ycomp = player.getPosition().getY() - getPosition().getY();

		// Huh?
		if (xcomp >= 0 && ycomp >= 0) {
			xcomp = Math.pow(xcomp, 1.1);
			ycomp = Math.pow(ycomp, 1.1);
		}

		xcomp = xcomp / (distance / getSpeed());
		ycomp = ycomp / (distance / getSpeed());

		int newPosX = (int) (getPosition().getX() + xcomp);
		int newPosY = (int) (getPosition().getY() + ycomp);
		
		if (getPosition().getLocalMap()
				.getArea().get((newPosX / 10 - 300), (newPosY / 10), Field.MOB) == true) {
			Position newPosition = getPosition().clone();
			newPosition.setX(newPosX);
			newPosition.setY(newPosY);
			walk(getPosition(), isRunning());
			
		} 
	}

	private void rangeMagicAttackPlayer(Player player) {

	}

	private void rangeMeleeAttackPlayer(Player player) {

	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}
	
	public int getMobDirectionX() {

		double directionX = Math.random() * 2;

		if (directionX >= 1.5) {
			return this.getPosition().getX() + (int) (directionX * this.getSpeed());
		} else {
			return this.getPosition().getX() + (int) (-directionX * this.getSpeed());
		}
	}

	public int getMobDirectionY() {

		double directionY = Math.random() * 2;

		if (directionY >= 1.5) {
			return this.getPosition().getY() + (int) (directionY * this.getSpeed());
		} else {
			return this.getPosition().getY() + (int) (-directionY * this.getSpeed());
		}
	}
	
	public void workMob() {

		// int newPosX,newPosY;
		// double directionX=0, directionY=0;
	

		Position newPos = this.getPosition().clone();
		// Members of the new position to where the mob should move
		newPos.setX(getMobDirectionX());
		newPos.setY(getMobDirectionY());

		// Members for the random direction of mob
		/*
		 * directionX = Math.random()*2; directionY = Math.random()*2;
		 * 
		 * if(directionX >= 1.5) newPosX =
		 * mob.getPosX()+(int)(directionX*mob.getSpeed()); else newPosX =
		 * mob.getPosX()+(int)(-directionX*mob.getSpeed());
		 * 
		 * if(directionY >= 1.5) newPosY =
		 * mob.getPosY()+(int)(directionY*mob.getSpeed()); else newPosY =
		 * mob.getPosY()+(int)(-directionY*mob.getSpeed());
		 */

		Iterator<Player> iterPlayer = Server.getInstance().getWorld()
				.getPlayerManager().getPlayerListIterator();

		while (iterPlayer.hasNext()) {
			Player player = iterPlayer.next();
			Client client = player.getClient();

			if (client == null) {
				continue;
			} else if (client.getState() != Client.State.INGAME
					|| this.getPosition().getLocalMap() != player.getPosition().getLocalMap()) {
				continue;
			}

			double distance = this.getPosition().distance(player.getPosition());

			/*
			 * double xcomp = Math.pow(player.getPosX() - mob.getPosX(), 2);
			 * double ycomp = Math.pow(player.getPosY() - mob.getPosY(), 2);
			 * double distance = Math.sqrt(xcomp + ycomp);
			 */

			// Condition that verify if the mob can move freely or not.
			// If the distance between the mob and the player is less or equal
			// then 150 (distance that makes the mob move to the player
			// direction)
			// and if the player position is a walkable position for mob then
			// the
			// mob will chase the player, else the mob will move freely.
			
			if (distance <= 150) {
				try {
				if (this.getPosition().getLocalMap()
						.getArea()
						.get((player.getPosition().getX() / 10 - 300),
								(player.getPosition().getY() / 10),Field.MOB) == true) {
					
				}
				} catch (Exception e) {
					Logger.getLogger(Mob.class).info("Mob Bug");
					//TODO: Fix Mob move bug
				}
			}

			// Condition that detects that the mob its out of player session
			// range
			if (distance >= player.getSessionRadius()) {
				//player.getSession().exit(mob);
			}
			

			if (distance < player.getSessionRadius()) {
				if (!this.getIsAttacking()) {
					
					
					/*
					String packetData = "walk npc " + mob.getId() + " "
							+ mob.getPosition().getX() + " " + mob.getPosition().getY() + " 0 " + (mob.isRunning()?1:0)
							+ "\n";
							*/
					// S> walk npc [UniqueId] [Xpos] [Ypos] [ZPos] [Running]
					this.setPosition(newPos);
					this.walk(this.getPosition(), this.isRunning());
					//client.sendPacket(Type.WALK, mob);
					//client.sendData( packetData);
				}
			}
		}

			Spawn spawn = this.getSpawn();
			if(spawn!=null){
				
				double distance = spawn.getPosition().distance(newPos);
			
				if (distance <= spawn.getRadius()) {
					// Logger.getLogger(MobManager.class).info("Distance <= Radius\n");					
					this.setPosition(newPos);
				}
			}
		
	}

	public void kill(Player player) {
		setHp(0);

		this.getPosition().getLocalMap().removeEntity(this);
		
		
		NpcSpawn spawn = this.getSpawn();
		if (spawn != null) {
			spawn.kill();
		}
		
		synchronized(player){
			
			player.setLime(player.getLime()+this.getLime());
			player.setTotalExp(player.getTotalExp()+this.getExp());
			player.setLevelUpExp(player.getLevelUpExp()-this.getExp());
			
			if(player.getQuestState() != null){
				player.getQuestState().handleProgress(this, player);
			}
		}
		
		Parser dropList = Reference.getInstance().getDropListReference();
		Iterator<ParsedItem> iter =dropList.getItemListIterator();
		Random r = new Random();
		while(iter.hasNext()) {			
			ParsedItem parsedItem = iter.next();
			if(parsedItem.getMemberValue("Mob").equals(""+this.getType())){
				float rate = Float.parseFloat(parsedItem.getMemberValue("Rate"));
				if( r.nextFloat()<rate){
					Logger.getLogger(Mob.class).info(parsedItem.getMemberValue("Item"));
					int itemType = Integer.parseInt(parsedItem.getMemberValue("Item"));
					ItemManager itemManager = player.getClient().getWorld().getItemManager();
					
					Item<?> item = itemManager.create(itemType);
					item.setExtraStats(0);
					item.setGemNumber(0);
					item.setDurability(item.getType().getMaxDurability());
					item.setUnknown1(0);
					item.setUnknown2(0);
					
					final RoamingItem roamingItem = getPosition().getLocalMap().getWorld().getCommand().dropItem(this.getPosition(), item);
					roamingItem.setOwner(player);
					
					java.util.Timer timer = new java.util.Timer();
					float dropExclusivity = player.getClient().getWorld().getServerSetings().getDropExclusivity();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							roamingItem.setOwner(null);
							
						}
					},(long)(dropExclusivity*1000));
					
				}
			}			
		}
		player.getClient().sendPacket(Type.SAY, "Experience: " + getExp() + " Lime: " + getLime());
	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}

	public void setIsAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
	}
	
	public void setIsBoss(boolean isBoss){
		this.isBoss = isBoss;
	}

	public void setIsMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public void setLime(int lime) {
		this.lime = lime;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void setBoss(){
		if(!getIsBoss()){
			setIsBoss(true);
			setMaxHp(getMaxHp()*2);
			setHp(getMaxHp());
			setExp(getExp()*2);
			setLime(getLime()*2);
			setDmg(getDmg()*2);
		}
	}
}