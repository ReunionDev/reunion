package org.reunionemu.jreunion.protocol.packets.client;


import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.reunionemu.jreunion.protocol.Packet;

public class CharNewPacket implements Packet {

	private static final long serialVersionUID = 1L;

	int slot;

	String name;

	Race race;

	Sex sex;

	int hair;

	int strength;

	int intellect;

	int dexterity;

	int constitution;

	int leadership;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("char_new ");
		builder.append(getSlot());
		builder.append(' ');
		builder.append(getName());
		builder.append(' ');
		builder.append(getRace().value());
		builder.append(' ');
		builder.append(getSex().value());
		builder.append(' ');
		builder.append(getHair());
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
		return builder.toString();
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

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
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
}
