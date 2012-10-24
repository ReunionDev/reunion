package org.reunionemu.jreunion.game;

import java.util.List;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Status;
import org.reunionemu.jreunion.game.items.pet.PetEquipment;
import org.reunionemu.jreunion.server.Database;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.Session;
import org.reunionemu.jreunion.server.Tools;
import org.reunionemu.jreunion.server.Client.State;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Pet extends LivingObject {

	public int id = -1 ; //id used for database storage
	
	private Player owner;
	
	private int closeDefence;

	private int distantDefence;

	private int closeAttack;

	private int distantAttack;

	private int satiety;

	private long exp;

	private int loyalty;

	private int speed;

	private int assemblyLevel;

	private int requiredLevel;
	
	private long levelUpExp;

	private Item<?> amulet;

	private Item<?> basket;
	
	private PetEquipment equipment;
	
	private Position position;

	private int state;		/* 0-player have no pet
							 * 1-pet is an egg
							 * 2-pet is stored at npc
							 * 12-pet is spawned
							 */
	private int aplesStored;
	
	private int levelUpLime;
	
	private int breederTimer ; //in seconds
	
	private boolean isBreeding;
	
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	
	public Pet() {
		//to be removed later
		setSpeed(3);
		setAssemblyLevel(17);
		setRequiredLevel(2);
		setSatiety(50);
		setLevelUpExp(313);
		setBasket(null);
		setApplesStored(0);
		setLevelUpLime(330);
		setAmulet(null);
	}
	
	public Pet(Player owner, int state) {
		super();
		setOwner(owner);
		setName(getOwner().getName() + "_Labiyong");
		//setBreeding(false);
		setState(state);
		setBreederTimer(getState()>1 ? 0 : getDefaultBreedTime());
		create();
	}

	public void create(){
		//default values from official server
		setMaxHp(getBaseHp());
		setHp(getBaseHp());
		setDistantAttack(getBaseDistantAttack());
		setCloseAttack(getBaseCloseAttack());
		setDistantDefence(getBaseDistantDefence());
		setCloseDefence(getBaseCloseDefence());
		setSpeed(3);
		setAssemblyLevel(17);
		setRequiredLevel(2);
		setLoyalty(0);
		setSatiety(50);
		setLevel(1);
		setExp(0);
		setLevelUpExp(313);
		setBasket(null);
		setApplesStored(0);
		setLevelUpLime(330);
		setAmulet(null);
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getBaseCloseAttack() {
		return 24;
	}

	public int getBaseCloseDefence() {
		return 12;
	}

	public int getBaseDistantAttack() {
		return 16;
	}

	public int getBaseDistantDefence() {
		return 7;
	}
	
	public int getBaseHp() {
		return 96;
	}
	
	public Item<?> getAmulet() {
		return amulet;
	}

	public int getAssemblyLevel() {
		return assemblyLevel;
	}

	public Item<?> getBasket() {
		return basket;
	}

	public int getCloseAttack() {
		return closeAttack;
	}

	public int getCloseDefence() {
		return closeDefence;
	}

	public int getDistantAttack() {
		return distantAttack;
	}

	public int getDistantDefence() {
		return distantDefence;
	}

	public long getExp() {
		return exp;
	}

	public int getLoyalty() {
		return loyalty;
	}
	
	public int getMaxLoyalty() {
		return 20;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}

	public int getSatiety() {
		return satiety;
	}
	
	public int getMaxSatiety() {
		return 100;
	}

	public int getSpeed() {
		return speed;
	}

	public void setAmulet(Item<?> amulet) {
		this.amulet = amulet;
	}

	public void setAssemblyLevel(int assemblyLevel) {
		this.assemblyLevel = assemblyLevel;
	}

	public void setBasket(Item<?> basket) {
		this.basket = basket;
	}

	public void setCloseAttack(int closeAttack) {
		this.closeAttack = closeAttack;
	}

	public void setCloseDefence(int closeDefence) {
		this.closeDefence = closeDefence;
	}

	public void setDistantAttack(int distantAttack) {
		this.distantAttack = distantAttack;
	}

	public void setDistantDefence(int distantDefence) {
		this.distantDefence = distantDefence;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public synchronized void setLoyalty(int loyalty) {
		this.loyalty = Tools.between(loyalty, 0, getMaxLoyalty());
	}

	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	public synchronized void setSatiety(int satiety) {
		this.satiety = Tools.between(satiety, 0, getMaxSatiety());
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public long getLevelUpExp() {
		return levelUpExp;
	}

	public void setLevelUpExp(long levelUpExp) {
		this.levelUpExp = levelUpExp;
	}

	@Override
	public void enter(Session session) {
		session.getOwner().getClient().sendPacket(Type.IN_PET, getOwner(), false);
	}

	@Override
	public void exit(Session session) {
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	public Player getOwner(){
		return this.owner;
	}
	
	public void setOwner(Player owner){
		this.owner = owner;
	}

	public PetEquipment getEquipment() {
		return equipment;
	}

	public void setEquipment(PetEquipment equipment) {
		this.equipment = equipment;
	}
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		if(position == null)
			return;
		this.position = position.clone();
		this.position.setX(this.position.getX() + 10);
		this.position.setY(this.position.getY() + 10);
	}
	
	public static enum PetStatus {
		
		HP(0),
		DISTANT_ATTACK(1),
		CLOSE_ATTACK(2),
		DISTANT_DEFENCE(3),
		CLOSE_DEFENCE(4),
		SPEED(5),
		ASSEMBLY_LEVEL(6),
		REQ_LEVEL(7),
		LOYALTY(8), 
		SATIETY(9),
		LEVEL(10),
		EXP(11),
		LEVELUP_EXP(12),
		STATE(13),
		BASKET(15),
		APPLES(16),
		LEVELUPLIME(17),
		AMULET(18);
		
		int value;
		
		PetStatus(int value){
			this.value = value;
		}
		
		public int value(){
			return value;
		}
		
		public static PetStatus byValue(int statusId){			
			for(PetStatus status:PetStatus.values())
			{
				if(status.value()==statusId){					
					return status;
				}
			}
			return null;
		}
	}
	
	public void sendStatus(PetStatus status) {
		if(getOwner().getClient().getState()==State.INGAME||getOwner().getClient().getState()==State.LOADED){
			long min=0,max=0;
			switch (status) {
				case HP: //0
					min = getHp();
					max = getMaxHp();				
					break;
				case DISTANT_ATTACK: //1
					min = getDistantAttack();
					break;
				case CLOSE_ATTACK: //2
					min = getCloseAttack();
					break;
				case DISTANT_DEFENCE: //3
					min = getDistantAttack();
					break;
				case CLOSE_DEFENCE: //4
					min = getCloseDefence();
					break;
				case SPEED: //5
					min = getSpeed();
					break;
				case ASSEMBLY_LEVEL: //6
					min = getAssemblyLevel();
					break;
				case REQ_LEVEL: //7
					min = getRequiredLevel();
					break;
				case LOYALTY: //8
					min = getLoyalty();
					break;
				case SATIETY: //9
					min = getSatiety();
					break;
				case LEVEL: //10
					min = getLevel();
					break;
				case EXP: //11
					min = getExp();
					break;
				case LEVELUP_EXP: //12
					min = getLevelUpExp();
					break;
				case STATE: //13
					min = getState();
					max = getEntityId()==-1 ? 0 : getEntityId();
					break;
				case BASKET: //15
					min = (getBasket()==null ? 0 : getBasket().getType().getTypeId());
					break;
				case APPLES: //16
					min = getAplesStored();
					break;
				case LEVELUPLIME: //17
					min = getLevelUpLime();
					break;
				case AMULET: //18
					min = (getAmulet()==null ? 0 : getAmulet().getType().getTypeId());
					break;
				default:
					throw new RuntimeException(status+" not implemented yet");
			
			}			
			getOwner().getClient().sendPacket(Type.PSTATUS, status.value(), min, max, 0);
		}
	}
	
	public void born(){
		stopBreeding();
		setBreederTimer(0);
		setState(12);
		Item<?> egg =  getOwner().getEquipment().getShoulderMount();
		getOwner().getInterested().sendPacket(Type.CHAR_REMOVE, getOwner(), Slot.SHOULDER);
		getOwner().getEquipment().setShoulderMount(null);
		getOwner().getPosition().getLocalMap().removeEntity(egg);
		Database.getDinamicInstance().deleteItem(egg.getItemId());
		setEquipment(new PetEquipment(getOwner()));
		load();
		getOwner().save();
		LoggerFactory.getLogger(this.getClass()).info("Pet: "+this+" as borned!");
	}
	
	public void load(){
		setPosition(getOwner().getPosition());
		getPosition().getLocalMap().createEntityId(this);
		//setMaxHp(getHp());
		getOwner().getClient().sendPacket(Type.MYPET, getOwner(), this);
		//getOwner().getInterested().sendPacket(Type.IN_PET, getOwner(), true);
		
		sendStatus(PetStatus.HP); //0
		sendStatus(PetStatus.DISTANT_ATTACK); //1
		sendStatus(PetStatus.CLOSE_ATTACK); //2
		sendStatus(PetStatus.DISTANT_DEFENCE); //3
		sendStatus(PetStatus.CLOSE_DEFENCE); //4
		sendStatus(PetStatus.SPEED); //5
		sendStatus(PetStatus.ASSEMBLY_LEVEL); //6
		sendStatus(PetStatus.REQ_LEVEL); //7
		sendStatus(PetStatus.LOYALTY); //8
		sendStatus(PetStatus.SATIETY); //9
		sendStatus(PetStatus.LEVEL); //10
		sendStatus(PetStatus.LEVELUP_EXP); //12
		sendStatus(PetStatus.EXP); //11
		sendStatus(PetStatus.LEVELUPLIME); //17
		sendStatus(PetStatus.STATE); //13
		sendStatus(PetStatus.BASKET); //15
		sendStatus(PetStatus.APPLES); //16
		sendStatus(PetStatus.AMULET); //18
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getAplesStored() {
		return aplesStored;
	}

	public void setApplesStored(int aplesStored) {
		this.aplesStored = aplesStored;
	}

	public int getLevelUpLime() {
		return levelUpLime;
	}

	public void setLevelUpLime(int levelUpLime) {
		this.levelUpLime = levelUpLime;
	}

	public int getBreederTimer() {
		return breederTimer;
	}

	public synchronized void setBreederTimer(int breederTimer) {
		this.breederTimer = breederTimer;
	}
	
	public int getDefaultBreedTime(){
		return Server.getInstance().getWorld().getServerSetings().getPetBreedTime();
	}

	public boolean isBreeding() {
		return isBreeding;
	}

	public void setBreeding(boolean isBreeding) {
		String breedingState = isBreeding ? "started" : "stoped";
		LoggerFactory.getLogger(this.getClass()).info("Player: "+getOwner()+" "+breedingState+" breeding Pet: "+this);
		
		this.isBreeding = isBreeding;
	}
	
	public void startBreeding(){
		executorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {	
				int breedTime = getBreederTimer() - 10;
				if(breedTime <= 0){
					born();
				} else {
					setBreederTimer(breedTime);
				}	
			}
		}, 0, 10, TimeUnit.SECONDS);
	}
	
	public void stopBreeding(){
		executorService.shutdown();
		executorService = Executors.newScheduledThreadPool(1);
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id:");
		buffer.append(getEntityId());
		buffer.append("("+getId()+")");
		buffer.append(", ");
		
		buffer.append("name:");
		buffer.append(getName());
		buffer.append(", ");
		
		buffer.append("level:");
		buffer.append(getLevel());	
		buffer.append(", ");
		
		buffer.append("owner:");
		buffer.append(getOwner().getName());
				
		buffer.append("}");
		return buffer.toString();
	}
}