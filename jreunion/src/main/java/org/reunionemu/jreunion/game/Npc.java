package org.reunionemu.jreunion.game;

import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.game.npc.NpcShop;
import org.reunionemu.jreunion.server.ItemManager;
import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.Area.Field;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.Reference;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.Session;
import org.reunionemu.jreunion.server.SessionList;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Npc<T extends NpcType> extends LivingObject {


	private T type;
	
	private NpcSpawn spawn;
	
	private boolean isMoving;

	private boolean isAttacking;
	
	private boolean isBoss;

	private int unknown1;

	private int unknown2;
	
	private int unknown3;
	
	private NpcShop shop;

	public Npc(T type) {
		super();
		setType(type);
		
		shop = null;
		
		setMaxHp(type.getMaxHp());
		setHp(type.getMaxHp());
	}
		
	@Override
	public void setHp(long hp){
		super.setHp(hp);
	}
	
	@Override
	public void setMaxHp(long hp){
		super.setMaxHp(this.getType().getMaxHp());
	}
	
	@Override
	public void setName(String livingObjectName){
		super.setName(this.getType().getName());
	}
	
	@Override
	public void setDmgType(int dmgType) {
		super.setDmgType(this.getType().getDmgType());
	}
	
	public T getType() {
		return type;
	}
	
	private void setType(T npcType){
		this.type = npcType;
	}
	
	public NpcSpawn getSpawn() {
		return spawn;
	}

	public void setSpawn(NpcSpawn spawn) {
		this.spawn = spawn;
	}
	
	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public boolean isAttacking() {
		return isAttacking;
	}

	public void setAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
	}

	public boolean isBoss() {
		return isBoss;
	}

	public void setBoss(boolean isBoss) {
		this.isBoss = isBoss;
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
	
	public NpcShop getShop() {
		return shop;
	}

	public void setShop(NpcShop shop) {
		this.shop = shop;
	}
	
	public void loadShop() {
		shop = new NpcShop(this);
	}
	
	@Override
	public void enter(Session session) {
		
		session.getOwner().getClient().sendPacket(Type.IN_NPC, this);
	}

	@Override
	public void exit(Session session) {
		
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	public void setBoss(){
		if(this.getType() instanceof Mob){
			Mob mob = ((Mob)this.getType());
			
			if(!isBoss()){
				setBoss(true);
				setMaxHp(getMaxHp()*2);
				setHp(getMaxHp());
				mob.setExp(mob.getExp()*2);
				mob.setLime(mob.getLime()*2);
				mob.setDmg(mob.getDmg()*2);
			}
		}
	}
	
	/*
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
	*/
	
	public void kill(Player player) {
		
		LocalMap localMap = getPosition().getLocalMap();
		SessionList<Session> list = localMap.GetSessions(getPosition());
		long serverXpRate = Server.getInstance().getWorld().getServerSetings().getXp();
		long serverLimeRate = Server.getInstance().getWorld().getServerSetings().getLime();
		long npcLime = 0;
		long npcExp = 0;
		
		if(this.getType() instanceof Mob){
			npcLime = ((Mob)this.getType()).getLime();
			npcExp = ((Mob)this.getType()).getExp();
		}
		
		setHp(0);
		localMap.removeEntity(this);
		list.exit(this, false);
		
		NpcSpawn spawn = this.getSpawn();
		if (spawn != null) {
			spawn.kill();
		}
		
		synchronized(player){
			
			player.setLime(player.getLime()+(npcLime*serverLimeRate));
			player.setTotalExp(player.getTotalExp()+(npcExp*serverXpRate));
			player.setLevelUpExp(player.getLevelUpExp()-(npcExp*serverXpRate));
			
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
					int itemType = Integer.parseInt(parsedItem.getMemberValue("Item"));
					ItemManager itemManager = player.getClient().getWorld().getItemManager();
					
					Item<?> item = itemManager.create(itemType);
					item.setGemNumber(0);
					item.setExtraStats(item.getType().getMaxExtraStats());
					item.setDurability(item.getType().getMaxDurability());
					item.setUnknown1(0);
					item.setUnknown2(0);
					
					final RoamingItem roamingItem = getPosition().getLocalMap().getWorld().getCommand().dropItem(this.getPosition(), item);
					roamingItem.setOwner(player);
					Logger.getLogger(Mob.class).info("Mob "+this+" droped roaming item "+roamingItem);
					
					java.util.Timer dropExclusivityTimer = new java.util.Timer();
					long dropExclusivity = player.getClient().getWorld().getServerSetings().getDropExclusivity();
					dropExclusivityTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							roamingItem.setOwner(null);
						}
					},dropExclusivity*1000);
					
				}
			}			
		}
		player.getClient().sendPacket(Type.SAY, "Experience: " + npcExp + " Lime: " + npcLime);
	}
	
	public void moveToPlayer(Player player, double distance) {
		
		Client client = player.getClient();

		if (client == null || !(getType() instanceof Mob)) {
			return;
		}
		
		Mob mob = (Mob)getType();
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

		xcomp = xcomp / (distance / mob.getSpeed());
		ycomp = ycomp / (distance / mob.getSpeed());

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
	
	public int getDirectionX() {

		if(!(getType() instanceof Mob))
			return 0;
		
		Mob mob = (Mob)getType();
		
		double directionX = Math.random() * 2;

		if (directionX >= 1.5) {
			return this.getPosition().getX() + (int) (directionX * mob.getSpeed());
		} else {
			return this.getPosition().getX() + (int) (-directionX * mob.getSpeed());
		}
	}

	public int getDirectionY() {

		if(!(getType() instanceof Mob))
			return 0;
		
		Mob mob = (Mob)getType();
		
		double directionY = Math.random() * 2;

		if (directionY >= 1.5) {
			return this.getPosition().getY() + (int) (directionY * mob.getSpeed());
		} else {
			return this.getPosition().getY() + (int) (-directionY * mob.getSpeed());
		}
	}
	
	public void work() {

		// int newPosX,newPosY;
		// double directionX=0, directionY=0;
	

		Position newPos = this.getPosition().clone();
		// Members of the new position to where the mob should move
		newPos.setX(getDirectionX());
		newPos.setY(getDirectionY());

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
				if (!this.isAttacking()) {
					
					
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
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id:");
		buffer.append(getEntityId());
		buffer.append(", ");
		
		buffer.append("type:");
		buffer.append(getType().getTypeId());
		buffer.append(", ");
		
		buffer.append("name:");
		buffer.append(getName());	
				
		buffer.append("}");
		return buffer.toString();
	}
}