package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Tools;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class HumanPlayer extends Player {

	public HumanPlayer(Client client) {
		super(client);
	}

	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 30) +(getLeadership() / 2);
	}
	
	@Override
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 80) + Tools.statCalc(getConstitution(), 30)+ (getLeadership() / 2);		
	}
	
	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 50) + (getLeadership() / 2);		
	}
	public int getMaxStamina(){
		return getStrength() + (getLeadership() / 2);
	}

	@Override
	public float getBaseDamage() {
		return (getLevel() / 6) + (getDexterity() / 4)+ getStrength();
		
	}
}