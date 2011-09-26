package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Tools;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class KailiptonPlayer extends Player {

	public KailiptonPlayer(Client client) {
		super(client);
	}
		
	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 80) +(getLeadership() / 2);
	}
	
	@Override
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 80) + Tools.statCalc(getConstitution(), 40)+ (getLeadership() / 2);		
	}
	
	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 30) + (getLeadership() / 2);		
	}
	
	public int getMaxStamina(){
		return getStrength() + (getLeadership() / 2);
	}

	@Override
	public float getBaseDamage() {
		return (getLevel() / 5) + (getWisdom() / 2);
	}

}