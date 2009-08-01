package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_PersonalOrnament extends G_EtcItem {
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

	public G_PersonalOrnament(int id) {
		super(id);
	}

	public void setIncHp(int incHp) {
		this.incHp = incHp;
	}

	public int getIncHp() {
		return this.incHp;
	}

	public void setIncMana(int incMana) {
		this.incMana = incMana;
	}

	public int getIncMana() {
		return this.incMana;
	}

	public void setIncStm(int incStm) {
		this.incStm = incStm;
	}

	public int getIncStm() {
		return this.incStm;
	}

	public void setIncElect(int incElect) {
		this.incElect = incElect;
	}

	public int getIncElect() {
		return this.incElect;
	}

	public void setIncStr(int incStr) {
		this.incStr = incStr;
	}

	public int getIncStr() {
		return this.incStr;
	}

	public void setIncWis(int incWis) {
		this.incWis = incWis;
	}

	public int getIncWis() {
		return this.incWis;
	}

	public void setIncDex(int incDex) {
		this.incDex = incDex;
	}

	public int getIncDex() {
		return this.incDex;
	}

	public void setIncConst(int incConst) {
		this.incConst = incConst;
	}

	public int getIncConst() {
		return this.incConst;
	}

	public void setIncLeadership(int incLeadership) {
		this.incLeadership = incLeadership;
	}

	public int getIncLeadership() {
		return this.incLeadership;
	}

	public void setIncLime(int incLime) {
		this.incLime = incLime;
	}

	public int getIncLime() {
		return this.incLime;
	}

	public void setIncExp(int incExp) {
		this.incExp = incExp;
	}

	public int getIncExp() {
		return this.incExp;
	}
	public void loadFromReference(int id) 
	{
		super.loadFromReference(id);
	}
}