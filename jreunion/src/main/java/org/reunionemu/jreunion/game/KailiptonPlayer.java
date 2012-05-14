package org.reunionemu.jreunion.game;

import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.Tools;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class KailiptonPlayer extends Player {

	public KailiptonPlayer(Client client) {
		super(client);
	}
		
	public long getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 80) +(getLeadership() / 2);
	}
	
	@Override
	public long getMaxHp(){
		return Tools.statCalc(getStrength(), 80) + Tools.statCalc(getConstitution(), 40)+ (getLeadership() / 2);		
	}
	
	public long getMaxMana(){
		return Tools.statCalc(getWisdom(), 30) + (getLeadership() / 2);		
	}
	
	public long getMaxStamina(){
		return getStrength() + (getLeadership() / 2);
	}

	@Override
	public long getBaseDamage() {
		return (getLevel() / 5) + (getWisdom() / 4);
	}

}