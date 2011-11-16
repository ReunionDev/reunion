package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Tools;



/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class BulkanPlayer extends Player {

	public BulkanPlayer(Client client) {
		super(client);
	}

	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 50) +(getLeadership() / 2);
	}
	
	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 80) + (getLeadership() / 2);		
	}
	
	@Override
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 50) + Tools.statCalc(getConstitution(), 20)+ (getLeadership() / 2);		
	}
	
	public int getMaxStamina(){
		return Tools.statCalc(getStrength(), 60) + (getLeadership() / 2)+ getConstitution();
	}	
	
	@Override
	public float getBaseDamage() {
		return (getLevel()/6) + (getStrength()/4) + (getDexterity()/4) + (getConstitution()/8);
	}
}