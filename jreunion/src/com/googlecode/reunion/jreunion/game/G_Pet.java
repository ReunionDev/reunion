package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_Session;

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

	public boolean getAmulet() {
		return amulet;
	}

	public int getAssembLvl() {
		return assembLvl;
	}

	public boolean getBasket() {
		return basket;
	}

	public int getCloseAtk() {
		return closeAtk;
	}

	public int getCloseDef() {
		return closeDef;
	}

	public int getDistAtk() {
		return distAtk;
	}

	public int getDistDef() {
		return distDef;
	}

	public int getExp() {
		return exp;
	}

	public int getLoyalty() {
		return loyalty;
	}

	public int getReqLvl() {
		return reqLvl;
	}

	public int getSatiety() {
		return satiety;
	}

	public int getSpeed() {
		return speed;
	}

	public void setAmulet(boolean amulet) {
		this.amulet = amulet;
	}

	public void setAssembLvl(int assembLvl) {
		this.assembLvl = assembLvl;
	}

	public void setBasket(boolean basket) {
		this.basket = basket;
	}

	public void setCloseAtk(int closeAtk) {
		this.closeAtk = closeAtk;
	}

	public void setCloseDef(int closeDef) {
		this.closeDef = closeDef;
	}

	public void setDistAtk(int distAtk) {
		this.distAtk = distAtk;
	}

	public void setDistDef(int distDef) {
		this.distDef = distDef;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public void setLoyalty(int loyalty) {
		this.loyalty = loyalty;
	}

	public void setReqLvl(int reqLvl) {
		this.reqLvl = reqLvl;
	}

	public void setSatiety(int satiety) {
		this.satiety = satiety;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public void enter(S_Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit(S_Session session) {
		// TODO Auto-generated method stub
		
	}
}