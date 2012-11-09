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

	public int getBootsTypeId() {
		return bootsTypeId;
	}

	public int getChestTypeId() {
		return chestTypeId;
	}

	public int getConstitution() {
		return constitution;
	}

	public int getDexterity() {
		return dexterity;
	}

	public int getElectricity() {
		return electricity;
	}

	public int getHair() {
		return hair;
	}

	public int getHelmetTypeId() {
		return helmetTypeId;
	}

	public int getHp() {
		return hp;
	}

	public int getId() {
		return id;
	}

	public int getIntellect() {
		return intellect;
	}

	public int getLeadership() {
		return leadership;
	}

	public int getLevel() {
		return level;
	}

	public int getMana() {
		return mana;
	}

	public int getMaxElectricity() {
		return maxElectricity;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public int getMaxStamina() {
		return maxStamina;
	}

	public String getName() {
		return name;
	}

	public int getOffhandTypeId() {
		return offhandTypeId;
	}

	public int getPantsTypeId() {
		return pantsTypeId;
	}

	public Race getRace() {
		return race;
	}

	public Sex getSex() {
		return sex;
	}

	public int getShoulderTypeId() {
		return shoulderTypeId;
	}

	public int getSlot() {
		return slot;
	}

	public int getStamina() {
		return stamina;
	}

	public int getStrength() {
		return strength;
	}

	public int getUnknown1() {
		return unknown1;
	}

	public int getUnknown2() {
		return unknown2;
	}

	public void setBootsTypeId(int bootsTypeId) {
		this.bootsTypeId = bootsTypeId;
	}

	public void setChestTypeId(int chestTypeId) {
		this.chestTypeId = chestTypeId;
	}

	public void setConstitution(int constitution) {
		this.constitution = constitution;
	}

	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public void setElectricity(int electricity) {
		this.electricity = electricity;
	}

	public void setHair(int hair) {
		this.hair = hair;
	}

	public void setHelmetTypeId(int helmetTypeId) {
		this.helmetTypeId = helmetTypeId;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIntellect(int intellect) {
		this.intellect = intellect;
	}

	public void setLeadership(int leadership) {
		this.leadership = leadership;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public void setMaxElectricity(int maxElectricity) {
		this.maxElectricity = maxElectricity;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public void setMaxStamina(int maxStamina) {
		this.maxStamina = maxStamina;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOffhandTypeId(int offhandTypeId) {
		this.offhandTypeId = offhandTypeId;
	}

	public void setPantsTypeId(int pantsTypeId) {
		this.pantsTypeId = pantsTypeId;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public void setShoulderTypeId(int shoulderTypeId) {
		this.shoulderTypeId = shoulderTypeId;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public void setUnknown1(int unknown1) {
		this.unknown1 = unknown1;
	}

	public void setUnknown2(int unknown2) {
		this.unknown2 = unknown2;
	}

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
}
