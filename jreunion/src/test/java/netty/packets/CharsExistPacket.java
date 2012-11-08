package netty.packets;

import netty.Packet;

import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;

public class CharsExistPacket implements Packet {
	
	private static final long serialVersionUID = 1L;

	int slot;
	
	int id;
	
	String name;
		
	Race race;
	
	Sex sex;
	
	int hair;
	
	int level;
	
	int hp;
	int maxHp;
	
	int mana;
	int maxMana;
	
	int stamina;
	int maxStamina;
	
	int electricity;
	int maxElectricity;	

	int strength;
	
	int intellect;
	
	int dexterity; 
	
	int constitution;
	
	int leadership;	
	
	int unknown1;
	
	int helmetTypeId;
	
	int chestTypeId;
	
	int pantsTypeId;
	
	int shoulderTypeId;
	
	int bootsTypeId;
	
	int offhandTypeId;
	
	int unknown2;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("chars_exist ");
		builder.append(getSlot());		
		builder.append(' ');
		builder.append(getId());
		builder.append(' ');
		builder.append(getName());
		builder.append(' ');
		builder.append(getRace().value());
		builder.append(' ');
		builder.append(getSex().value());
		builder.append(' ');
		builder.append(getHair());
		builder.append(' ');
		builder.append(getLevel());
		builder.append(' ');
		builder.append(getHp());
		builder.append(' ');
		builder.append(getMaxHp());
		builder.append(' ');
		builder.append(getMana());
		builder.append(' ');
		builder.append(getMaxMana());
		builder.append(' ');
		builder.append(getStamina());
		builder.append(' ');
		builder.append(getMaxStamina());
		builder.append(' ');
		builder.append(getElectricity());
		builder.append(' ');
		builder.append(getMaxElectricity());
		builder.append(' ');
		builder.append(getStrength());
		builder.append(' ');
		builder.append(getIntellect());
		builder.append(' ');
		builder.append(getDexterity());
		builder.append(' ');
		builder.append(getConstitution());
		builder.append(' ');
		builder.append(getLeadership());
		builder.append(' ');
		builder.append(getUnknown1());
		builder.append(' ');
		builder.append(getHelmetTypeId());
		builder.append(' ');
		builder.append(getChestTypeId());
		builder.append(' ');
		builder.append(getPantsTypeId());
		builder.append(' ');
		builder.append(getShoulderTypeId());
		builder.append(' ');
		builder.append(getBootsTypeId());
		builder.append(' ');
		builder.append(getOffhandTypeId());
		builder.append(' ');
		builder.append(getUnknown2());
		return builder.toString();
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public int getHair() {
		return hair;
	}

	public void setHair(int hair) {
		this.hair = hair;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getStamina() {
		return stamina;
	}

	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	public int getMaxStamina() {
		return maxStamina;
	}

	public void setMaxStamina(int maxStamina) {
		this.maxStamina = maxStamina;
	}

	public int getElectricity() {
		return electricity;
	}

	public void setElectricity(int electricity) {
		this.electricity = electricity;
	}

	public int getMaxElectricity() {
		return maxElectricity;
	}

	public void setMaxElectricity(int maxElectricity) {
		this.maxElectricity = maxElectricity;
	}

	public int getUnknown1() {
		return unknown1;
	}

	public void setUnknown1(int unknown1) {
		this.unknown1 = unknown1;
	}

	public int getUnknown2() {
		return unknown2;
	}

	public void setUnknown2(int unknown2) {
		this.unknown2 = unknown2;
	}

	public int getIntellect() {
		return intellect;
	}

	public void setIntellect(int intellect) {
		this.intellect = intellect;
	}

	public int getDexterity() {
		return dexterity;
	}

	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public int getConstitution() {
		return constitution;
	}

	public void setConstitution(int constitution) {
		this.constitution = constitution;
	}

	public int getLeadership() {
		return leadership;
	}

	public void setLeadership(int leadership) {
		this.leadership = leadership;
	}


	public int getHelmetTypeId() {
		return helmetTypeId;
	}
	public void setHelmetTypeId(int helmetTypeId) {
		this.helmetTypeId = helmetTypeId;
	}
	public int getChestTypeId() {
		return chestTypeId;
	}
	public void setChestTypeId(int chestTypeId) {
		this.chestTypeId = chestTypeId;
	}
	public int getPantsTypeId() {
		return pantsTypeId;
	}
	public void setPantsTypeId(int pantsTypeId) {
		this.pantsTypeId = pantsTypeId;
	}
	public int getShoulderTypeId() {
		return shoulderTypeId;
	}
	public void setShoulderTypeId(int shoulderTypeId) {
		this.shoulderTypeId = shoulderTypeId;
	}
	public int getBootsTypeId() {
		return bootsTypeId;
	}
	public void setBootsTypeId(int bootsTypeId) {
		this.bootsTypeId = bootsTypeId;
	}
	public int getOffhandTypeId() {
		return offhandTypeId;
	}
	public void setOffhandTypeId(int offhandTypeId) {
		this.offhandTypeId = offhandTypeId;
	}
}
