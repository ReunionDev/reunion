package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PersonalOrnament extends EtcItem {
	private int incHp;

	private int incMana;

	private int incStm;

	private int incElect;

	private int incStr;

	private int incWis;

	private int incDex;

	private int incConst;

	private int incLeadership;

	private int incLime;

	private int incExp;

	public PersonalOrnament(int id) {
		super(id);
	}

	public int getIncConst() {
		return incConst;
	}

	public int getIncDex() {
		return incDex;
	}

	public int getIncElect() {
		return incElect;
	}

	public int getIncExp() {
		return incExp;
	}

	public int getIncHp() {
		return incHp;
	}

	public int getIncLeadership() {
		return incLeadership;
	}

	public int getIncLime() {
		return incLime;
	}

	public int getIncMana() {
		return incMana;
	}

	public int getIncStm() {
		return incStm;
	}

	public int getIncStr() {
		return incStr;
	}

	public int getIncWis() {
		return incWis;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}

	public void setIncConst(int incConst) {
		this.incConst = incConst;
	}

	public void setIncDex(int incDex) {
		this.incDex = incDex;
	}

	public void setIncElect(int incElect) {
		this.incElect = incElect;
	}

	public void setIncExp(int incExp) {
		this.incExp = incExp;
	}

	public void setIncHp(int incHp) {
		this.incHp = incHp;
	}

	public void setIncLeadership(int incLeadership) {
		this.incLeadership = incLeadership;
	}

	public void setIncLime(int incLime) {
		this.incLime = incLime;
	}

	public void setIncMana(int incMana) {
		this.incMana = incMana;
	}

	public void setIncStm(int incStm) {
		this.incStm = incStm;
	}

	public void setIncStr(int incStr) {
		this.incStr = incStr;
	}

	public void setIncWis(int incWis) {
		this.incWis = incWis;
	}
}