package org.reunionemu.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.items.equipment.*;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.game.npc.NpcShop;
import org.reunionemu.jreunion.server.Area;
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
	
	private int mutantType;		
	
	private int unknown1;

	private int unknown2;
	
	private int unknown3;
	
	private NpcShop shop;
	
	private long areaRadius;
	
	private long attackRadius;

	public Npc(T type) {
		super();
		setType(type);
		
		shop = null;
		
		setMaxHp(type.getMaxHp());
		setHp(type.getMaxHp());
		setAreaRadius(Server.getInstance().getWorld().getServerSetings().getMobRadiusArea());
		setLevel(getType().getLevel());
		if(this.getType() instanceof Mob){
			if(((Mob)this.getType()).getAttackType() == AttackType.CLOSE_MELEE.value){
				setAttackRadius(Server.getInstance().getWorld().getServerSetings().getCloseAttackRadius());
			} else {
				setAttackRadius(Server.getInstance().getWorld().getServerSetings().getRangeAttackRadius());
			}
		}
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
	
	public long getAreaRadius() {
		return areaRadius;
	}

	public void setAreaRadius(long areaRadius) {
		this.areaRadius = areaRadius;
	}
	
	@Override
	public void enter(Session session) {
		session.getOwner().getClient().sendPacket(Type.IN_NPC, this);
	}

	@Override
	public void exit(Session session) {
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	public long getAttackRadius() {
		return attackRadius;
	}

	public void setAttackRadius(long attackRadius) {
		this.attackRadius = attackRadius;
	}
	
	public void setBoss(){
		if(this.getType() instanceof Mob){
			//Mob mob = ((Mob)this.getType());
			
			if(!isBoss()){
				setBoss(true);
				setMaxHp(getMaxHp()*2);
				setHp(getMaxHp());
				//mob.setExp(mob.getExp()*2);
				//mob.setLime(mob.getLime()*2);
				//mob.setDmg(mob.getDmg()*2);
			} 
		}
	}
	
	public int getMutantType() {
		return mutantType;
	}

	public void setMutantType(int mutantType) {
		this.mutantType = mutantType;
	}
	
	/**Returns a random mutant type:
	 * 1 - Red - Resistance against short-range physical attack
	 * 2 - Blue - Resistance against magical attack
	 * 3 - Green - Resistance power against summon attack
	 * 4 - Yellow - Resistance power against long-range physical attack 
	 * 
	 * @return mutantType
	 */
	public int getRandomMutantType() {
		int mutantType = 0;
		
		while(mutantType < 1 || mutantType > 4){
			mutantType = (int)(Server.getRand().nextFloat()*10);
		}
		
		return mutantType;
	}
	
	public boolean isMutant(){
		return getMutantType() == 0 ? false : true;
	}
	
	public float getMutantResistance(LivingObject livingObject){
	
		float mobMutantModifier = Server.getInstance().getWorld().getServerSetings().getMobMutantModifier();
		float returnvalue = 0.5f;
		
		 /*  1(red) - Resistance against short-range physical attack
		  *  2(blue) - Resistance against magical attack
		  *  3(green) - Resistance against summon attack
		  *  4(yellow) - Resistance against long-range physical attack
		  */
		 if(getMutantType() == livingObject.getLastAttackType()){
			 // Full resistance to specific damage type
			 returnvalue = 1 - mobMutantModifier;	 
		 } else {
			 // Generally half resistance
			 returnvalue = 1 - (mobMutantModifier / 2 );
		 }
		 
		 if (returnvalue > 0.9) { returnvalue = 0.9f; }
		 if (returnvalue < 0.1) { returnvalue = 0.1f; }
		 
		 return (float) (returnvalue);
	}
	
	public int getMutantGemStoneType(boolean full){
		if(getType().getLevel() <= 30){
			return (full) ? 215 : 535;
		}
		else if(getLevel() <= 60){
			return (full) ? 216 : 536;
		}
		else if(getLevel() <= 90){
			return (full) ? 217 : 537;
		}
		else if(getLevel() <= 120){
			return (full) ? 218 : 538;
		}
		else if(getLevel() <= 150){
			return (full) ? 219 : 539;
		}
		else if(getLevel() <= 180){
			return (full) ? 220 : 540;
		}
		else {
			return (full) ? 221 : 541;
		}
	}
	
	public void kill(Player player) {
		
		LocalMap localMap = getPosition().getLocalMap();
		SessionList<Session> list = localMap.GetSessions(getPosition());
		long serverXpRate = Server.getInstance().getWorld().getServerSetings().getXp();
		long serverLimeRate = Server.getInstance().getWorld().getServerSetings().getLime();
		float mutantModifier = Server.getInstance().getWorld().getServerSetings().getMobMutantModifier();
		int expPlayerMobDifference = Server.getInstance().getWorld().getServerSetings().getExpPlayerMobDifference();
		int expLowerStep = Server.getInstance().getWorld().getServerSetings().getExpLowerStep();
		
		long npcLime = 0;
		long npcExp = 0;
		float modifier = 1;
		
		if(this.getLevel()/player.getLevel() >=1)
			modifier = 1;
		else if((player.getLevel()-this.getLevel()) > expPlayerMobDifference)
		{
			modifier = (((player.getLevel()-this.getLevel()-expPlayerMobDifference)*expLowerStep)-100)*(-1);
			modifier /= 100;
		}
		
		if(this.getType() instanceof Mob){
			npcLime = (long)(((Mob)this.getType()).getLime()*serverLimeRate); //on lime no modifier is needed
			npcExp = (long)(((Mob)this.getType()).getExp()*serverXpRate*modifier);
		}
		
		
		if(isMutant()){
			npcLime = (long) (npcLime * (1 + mutantModifier));
			npcExp = (long) (npcExp * (1 + mutantModifier));
		}
		
		if(isBoss()){
			npcLime = (long) (npcLime * 2);
			npcExp = (long) (npcLime * 2);
		}
		
		npcExp = (npcExp <= 0) ? 1 : npcExp;	//check that player will receive a minimum amount of exp
		npcLime = (npcLime <= 0) ? 1 : npcLime; //check that player will receive a minimum amount of lime
		
		setHp(0);
		localMap.removeEntity(this);
		list.exit(this, false);
		
		NpcSpawn spawn = this.getSpawn();
		if (spawn != null) {
			spawn.kill();
		}
		
		synchronized(player){
			//Distribute exp and lime to players from mob kill
			List<Player> playerList = new Vector<Player> ();
			if(player.getParty() == null){
				playerList.add(player);
			} else{
				npcExp = (long)(npcExp / player.getParty().getMembers().size());
				npcLime = (long)(npcLime / player.getParty().getMembers().size());
				playerList = player.getParty().getMembers();
			}
			
			for(Player member : playerList){
				member.setLime(member.getLime()+(npcLime));
				member.setTotalExp(member.getTotalExp()+(npcExp));
				member.setLevelUpExp(member.getLevelUpExp()-(npcExp));
				member.getClient().sendPacket(Type.SAY, "Experience"+ ((serverXpRate != 1) ? "(x"+serverXpRate+")" : "")+": " + (npcExp) + " Lime"+ ((serverLimeRate != 1) ? "(x"+serverLimeRate+")" : "")+": " + (npcLime));
				if(member.getQuestState() != null){
					player.getQuestState().handleProgress(this, player);
				}
			}
		}
		
		ItemManager itemManager = player.getClient().getWorld().getItemManager();
		List<Item<?>> itemList = new Vector<Item<?>> ();
		
		//Handle with the mutant Item drop
		if(isMutant()){
			itemList.add(itemManager.create(getMutantGemStoneType(false), 0, 0, 0, 0, 0));
		}
		
		float r = Server.getRand().nextFloat();
		
		//Rough Gem Drop
		if(r <= .01 && Server.getRand().nextFloat() <= .20)
		{
			itemList.add(itemManager.create(getMutantGemStoneType(true), 0, 0, 0, 0, 0));
		}
		
		//Handle with the Item drop chance
		Parser dropListParser = Reference.getInstance().getDropListReference();
		Iterator<ParsedItem> iter = dropListParser.getItemListIterator();
		List<ItemType> dropList = new Vector<ItemType> ();
		
		while(iter.hasNext()) {			
			ParsedItem parsedItem = iter.next();
		
			if(Integer.parseInt(parsedItem.getMemberValue("Mob")) == getType().getTypeId()){
				float rate = Float.parseFloat(parsedItem.getMemberValue("Rate"));
				
				if( r < rate){
					int itemTypeId = Integer.parseInt(parsedItem.getMemberValue("Item"));
					dropList.add(itemManager.getItemType(itemTypeId));
				}
			}
		}
		
		if(dropList.size() > 0){
			int dropListRandomPos = dropList.size()==1 ? 0 : dropList.size();
			
			if(dropListRandomPos > 0) {
				//randomly select one item from the item list.
				while(dropListRandomPos >= dropList.size()){
					dropListRandomPos = Server.getRand().nextInt(dropList.size()-1);
				}
			}
		
			ItemType itemType = dropList.get(dropListRandomPos);
			//handles with the luck of drop a plus item.
			float gemLuck = Server.getRand().nextFloat();
			float itemPlusByOne = getPosition().getLocalMap().getWorld().getServerSetings().getItemPlusByOne();
			float itemPlusByTwo = getPosition().getLocalMap().getWorld().getServerSetings().getItemPlusByTwo();
			int gemNumber = 0;
		
			if(itemType.isUpgradable() && itemType.getLevel() <= 180){
				gemNumber = (gemLuck < itemPlusByOne ? (gemLuck < itemPlusByTwo ? 3 : 1) : 0);
			}
					
			itemList.add(itemManager.create(itemType.getTypeId(), gemNumber, (int)itemType.getMaxExtraStats(),
					itemType.getMaxDurability(), 0, 0));
		}
		
		//handles the items drop command and packets.
		for(Item<?> item : itemList){
			RoamingItem roamingItem = getPosition().getLocalMap().getWorld().
					getCommand().dropItem(this.getPosition(), item, player);
			roamingItem.setOwner(player);
			roamingItem.setDropExclusivity(player);
			LoggerFactory.getLogger(Npc.class).info("Mob "+this+" droped roaming item "+roamingItem);
		}
	}
	
	public void moveFree() {
		
		if(isRunning())
			return;
		
		setIsRunning(true);
		
		//Area npcArea = getPosition().getLocalMap().getArea();
		Position newPos =  getRandomPosition();
		int newPositionTries = 10;
		
		//while((!npcArea.get(newPos.getX() / 10, newPos.getY() / 10,Field.MOB))){
		while(!isPathWalkable(newPos) && newPositionTries-- > 0) {
			newPos =  getRandomPosition();
		}
		
		if(newPositionTries < 0){
			//LoggerFactory.getLogger(this.getClass()).warn("Mob %s couldn't move.",this);
			setIsRunning(false);
			return;
		}
		
		walk(newPos, isRunning());
		setIsRunning(false);
	}
	
	/**
	 * Checks if Position is within other Npc Radius Area.
	 * 
	 * @param position
	 * @return true or false
	 */
	public boolean isNpcCollision(Position position){
		for(Entity entity : getPosition().getLocalMap().getEntities()){
			if(!(entity instanceof Npc)){
				continue;
			}
			Npc<?> npc = (Npc<?>)entity;
			if(position.within(npc.getPosition(), getAreaRadius())){
				return true;
			}
		}
		return false;
	}
	
	public int getNewPosX(Position position, double distance){ //distance: r (player)
		//x = a + r * cos(t)
		double nSpeed = ((Mob)this.getType()).getSpeed(); //r (mob)
		//double percent = ((100*nSpeed)/distance);
		double pPosX = position.getX(); //x
		double nPosX = getPosition().getX(); //a
		
		double directionAngle = Math.acos((pPosX-nPosX)/distance);
		
		//return (int) ((((pPosX-nPosX)*percent)/100)+nPosX);
		return (int)(nPosX + (nSpeed * Math.cos(directionAngle)));
	}
	
	public int getNewPosY(Position position, double distance){ //distance: r (player)
		//y = b + r * sin(t)
		double nSpeed = ((Mob)this.getType()).getSpeed(); //r (mob)
		//double percent = ((100*nSpeed)/distance);
		double pPosY = position.getY(); //y
		double nPosY = getPosition().getY(); //b
		
		double directionAngle = Math.asin((pPosY-nPosY)/distance);
		
		//return (int) ((((pPosY-nPosY)*percent)/100)+nPosY);
		return (int)(nPosY + (nSpeed * Math.sin(directionAngle)));
	}

	public Position getRandomPosition(){
		Position newPosition = this.getPosition().clone();
		double directionAngle = 360;
		
		while(directionAngle >= 360){
			directionAngle = (int)(Server.getRand().nextFloat()*100);
		}
		
		newPosition.setX(getRandomPosX(directionAngle));
		newPosition.setY(getRandomPosY(directionAngle));
		
		return newPosition;
	}
	
	public int getRandomPosX(double directionAngle){
		//x = a + r * cos(t)
		double nPosX = getPosition().getX(); //a
		int speed = ((Mob)this.getType()).getSpeed(); //r
		
		return (int)(nPosX + (speed * Math.cos(directionAngle))); //x
	}
	
	public int getRandomPosY(double directionAngle){
		//y = b + r * sin(t)
		double nPosY = getPosition().getY(); //b
		int speed = ((Mob)this.getType()).getSpeed(); //r
		
		return (int)(nPosY + (speed * Math.sin(directionAngle))); //y
	}

	public void moveToPlayer(Player player) {
		Client client = player.getClient();
		
		if(client==null || isRunning())
			return;
		
		setIsRunning(true);
		
		Position newPosition = getPosition().clone();
		double distance = this.getPosition().distance(player.getPosition());
		int newPosX = getNewPosX(player.getPosition().clone(), distance);
		int newPosY = getNewPosY(player.getPosition().clone(), distance);
		
		newPosX = (newPosX == 0) ? newPosition.getX() : newPosX;
		newPosY = (newPosY == 0) ? newPosition.getY() : newPosY;
		
		newPosition.setX(newPosX);
		newPosition.setY(newPosY);
		
		if(!isPathWalkable(newPosition)){
			setIsRunning(false);
			moveFree();
			return;
		}
		
		walk(newPosition, isRunning());
		setIsRunning(false);
	}
	
	public boolean isPathWalkable(Position position){
		Area mobArea = this.getPosition().getLocalMap().getArea(); 
		double distance = this.getPosition().distance(position);
		Position newPos = getPosition().clone();
		
		if(distance > this.getSpawn().getRadius())
			return false;
		
		while(distance > 0){
			int posX = getNewPosX(position.clone(), distance);
			int posY = getNewPosY(position.clone(), distance);
			
			posX = (posX == 0) ? newPos.getX() : posX;
			posY = (posY == 0) ? newPos.getY() : posY;
			
			newPos.setX(posX);
			newPos.setY(posY);
			
			if(!mobArea.get(posX / 10, posY / 10,Field.MOB) || isNpcCollision(newPos)){
				return false;
			}
			distance -= 0.1;
		}
		
		return true;
	}
	
	public void attackPlayer(Player player) {
		Client client = player.getClient();
		
		if(client==null || isAttacking())
			return;
		
		setAttacking(true);
		
		List<Skill> defensiveSkills = player.getDefensiveSkills();
		int npcDmg = getDamage((int) player.getDef());
		npcDmg = (isBoss() ? npcDmg*2 : npcDmg); //check if npc is boss.
		//npcDmg = (npcDmg < 1) ? 1 : npcDmg; //make sure the npc will have a minimum damage value.
 	
		if (defensiveSkills.size() == 0) {
			player.setHp(player.getHp() - npcDmg);
		} else {
			for (Skill skill : defensiveSkills) {
				if (player.getSkillLevel(skill) > 0) {
					if(!skill.work(player, this)){
						player.setHp(player.getHp() - npcDmg);
					}
				} else {
					player.setHp(player.getHp() - npcDmg);
				}
			}
		}
		
		this.getInterested().sendPacket(Type.ATTACK,this,player,0);
		setAttacking(false);
	}
	
	public int getDamage(int playerDef){
		
		if(!(getType() instanceof Mob)){
			return 0;
		}
		Mob mob = (Mob)this.getType();
		float mobMutantModifier = isMutant() ? Server.getInstance().getWorld().getServerSetings().getMobMutantModifier()+1 : 1;
		int damage = (int)((mob.getDmg()*mobMutantModifier) - (playerDef/2 + ((playerDef/2)*Server.getRand().nextFloat())));
		return damage < 1 ? 1 : damage; 
	}
	
	public void work() {
		try {
			
			int isMovementEnabled = Server.getInstance().getWorld().getServerSetings().getMobsMovement();
		
			if(isRunning() || getHp() == 0 || isMovementEnabled == 0)
				return;
		
			Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
			double smallestDistance = 150;
			Player closestPlayer = null;
			Area mobArea = getPosition().getLocalMap().getArea(); 
			boolean moveFree = false;
		
			while (iterPlayer.hasNext()) {
				//for(Player player : getPosition().getLocalMap().getPlayerList()) {
				Player player = iterPlayer.next();
				
				if(this.getPosition().getMap().getId() != player.getPosition().getMap().getId()){
				    continue;
				}
				
				Position position = player.getPosition();
				double distance = getPosition().distance(player.getPosition());
			
				if(moveFree == false){
					moveFree = player.getSession().contains(this.getPosition()) ? true : false;
				}
			
				if (player.getClient() == null) {
					continue;
				} else if (player.getClient().getState() != Client.State.INGAME
					|| this.getPosition().getLocalMap() != player.getPosition().getLocalMap()
					|| !player.getSession().contains(this.getPosition())
					|| !mobArea.get(position.getX() / 10, position.getY() / 10,Field.MOB)
					|| player.getHp() <= 0) {
						player.update();
						continue;
				}
				
				if(distance < smallestDistance){
					smallestDistance = distance;
					closestPlayer = player;
				}
			
			}
			
			// Condition that verify if the mob can move freely or not.
			// If the distance between the mob and the player is less or equal
			// then 150 (distance that makes the mob move to the player
			// direction) and if the player position is a walkable position
			// for mob then the  mob will chase or attack the player, else the mob will
			// move freely.
		
			if (smallestDistance < 150) {
				if(smallestDistance > getAttackRadius()) {
					moveToPlayer(closestPlayer);
				} else {
					attackPlayer(closestPlayer);
				}	
			} else if(moveFree){
				moveFree();
			}
		} catch (Exception  e) {
			LoggerFactory.getLogger(this.getClass()).info("Mob Bug "+e);
			//TODO: Fix Mob move bug
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
		buffer.append(", ");
		
		buffer.append("spawn:");
		buffer.append(getSpawn().getId());
				
		buffer.append("}");
		return buffer.toString();
	}
}