package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Spawn;
import com.googlecode.reunion.jreunion.server.Area.Field;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class MobManager {
	private java.util.List<Mob> mobList = new Vector<Mob>();

	private boolean moveToPlayer = true;
	int mobIdCounter = 20000;

	public MobManager() {

	}

	public void addMob(Mob mob) {
		if (containsMob(mob)) {
			return;
		}
		mobList.add(mob);
	}

	public boolean containsMob(Mob mob) {
		return mobList.contains(mob);
	}

	public Mob createMob(int type) {
		synchronized(this){
			
			ParsedItem parsedItem = Reference.getInstance().getMobReference().getItemById(type);			
			if(parsedItem==null){
				
				Logger.getLogger(MobManager.class).warn("Unknown mob type: "+type);
				return null;
				
			}
			String className = "com.googlecode.reunion.jreunion.game." + parsedItem.getMemberValue("Class");		
			
			Mob mob = (Mob)ClassFactory.create(className, type);
			if(mob==null)
				return null;
			
			mob.setId(mobIdCounter++);
			addMob(mob);
			return mob;
		}
	}

	public Mob getMob(int uniqueid) {
		
		Iterator<Mob> iter = getMobListIterator();
		while (iter.hasNext()) {
			Mob mob = iter.next();
			if (mob.getId() == uniqueid) {
				return mob;
			}
		}
		return null;
	}

	public int getMobDirectionX(Mob mob) {

		double directionX = Math.random() * 2;

		if (directionX >= 1.5) {
			return mob.getPosition().getX() + (int) (directionX * mob.getSpeed());
		} else {
			return mob.getPosition().getX() + (int) (-directionX * mob.getSpeed());
		}
	}

	public int getMobDirectionY(Mob mob) {

		double directionY = Math.random() * 2;

		if (directionY >= 1.5) {
			return mob.getPosition().getY() + (int) (directionY * mob.getSpeed());
		} else {
			return mob.getPosition().getY() + (int) (-directionY * mob.getSpeed());
		}
	}

	public Iterator<Mob> getMobListIterator() {
		return new Vector<Mob>(mobList).iterator();
	}

	public int getNumberOfMobs() {
		return mobList.size();
	}

	public void removeMob(Mob mob) {
		if (!containsMob(mob)) {
			return;
		}
		while (containsMob(mob)) {
			mobList.remove(mob);
		}
		//ItemManager.getEntityManager().destroyEntity(mob);
	}

	public void workMob(Mob mob) {

		if (mobList.size() == 0) {
			return;
		}

		// int newPosX,newPosY;
		// double directionX=0, directionY=0;

		if (moveToPlayer == false) {
			moveToPlayer = true;
		}

		Position newPos = mob.getPosition().clone();
		// Members of the new position to where the mob should move
		newPos.setX(getMobDirectionX(mob));
		newPos.setY(getMobDirectionY(mob));

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
					|| mob.getPosition().getLocalMap() != player.getPosition().getLocalMap()) {
				continue;
			}

			double distance = mob.getPosition().distance(player.getPosition());

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
				if (mob.getPosition().getLocalMap()
						.getArea()
						.get((player.getPosition().getX() / 10 - 300),
								(player.getPosition().getY() / 10),Field.MOB) == true) {
					moveToPlayer = false;
				}
				} catch (Exception e) {
					Logger.getLogger(MobManager.class).info("Mob Bug");
					//TODO: Fix Mob move bug
				}
			}

			// Condition that detects that the mob its out of player session
			// range
			if (distance >= player.getSessionRadius()) {
				//player.getSession().exit(mob);
			}

			if (distance < player.getSessionRadius()) {
				if (mob.getIsAttacking() == 0) {
					
					
					/*
					String packetData = "walk npc " + mob.getId() + " "
							+ mob.getPosition().getX() + " " + mob.getPosition().getY() + " 0 " + (mob.isRunning()?1:0)
							+ "\n";
							*/
					// S> walk npc [UniqueId] [Xpos] [Ypos] [ZPos] [Running]
					mob.walk(mob.getPosition(), mob.isRunning());
					//client.sendPacket(Type.WALK, mob);
					//client.sendData( packetData);
				}
			}
		}

		if (moveToPlayer == true) {
			Spawn spawn = mob.getSpawn();
			if(spawn!=null){
				
				double distance = spawn.getPosition().distance(newPos);
			
				if ((int) distance <= spawn.getRadius()) {
					// Logger.getLogger(MobManager.class).info("Distance <= Radius\n");					
					mob.setPosition(newPos);
				}
			}
		}
	}
}