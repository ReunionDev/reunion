package com.googlecode.reunion.jreunion.game;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Pet extends G_LivingObject {

	private int closeDef;

	private int distDef;

	private int closeAtk;

	private int distAtk;

	private int satiety;

	private int exp;

	private int loyalty;

	private int speed;

	private int assembLvl;

	private int reqLvl;

	private boolean amulet;

	private boolean basket;

	public G_Pet() {
		super();
	}

	public void setCloseDef(int closeDef) {
		this.closeDef = closeDef;
	}

	public int getCloseDef() {
		return this.closeDef;
	}

	public void setDistDef(int distDef) {
		this.distDef = distDef;
	}

	public int getDistDef() {
		return this.distDef;
	}

	public void setCloseAtk(int closeAtk) {
		this.closeAtk = closeAtk;
	}

	public int getCloseAtk() {
		return this.closeAtk;
	}

	public void setDistAtk(int distAtk) {
		this.distAtk = distAtk;
	}

	public int getDistAtk() {
		return this.distAtk;
	}

	public void setSatiety(int satiety) {
		this.satiety = satiety;
	}

	public int getSatiety() {
		return this.satiety;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getExp() {
		return this.exp;
	}

	public void setLoyalty(int loyalty) {
		this.loyalty = loyalty;
	}

	public int getLoyalty() {
		return this.loyalty;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return this.speed;
	}

	public void setAssembLvl(int assembLvl) {
		this.assembLvl = assembLvl;
	}

	public int getAssembLvl() {
		return this.assembLvl;
	}

	public void setReqLvl(int reqLvl) {
		this.reqLvl = reqLvl;
	}

	public int getReqLvl() {
		return this.reqLvl;
	}

	public void setAmulet(boolean amulet) {
		this.amulet = amulet;
	}

	public boolean getAmulet() {
		return this.amulet;
	}

	public void setBasket(boolean basket) {
		this.basket = basket;
	}

	public boolean getBasket() {
		return this.basket;
	}
}